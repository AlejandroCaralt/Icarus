package com.alejandrocaralt.icarus.Activitys;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.alejandrocaralt.icarus.Adapters.EventAdapter;
import com.alejandrocaralt.icarus.Models.Event;
import com.alejandrocaralt.icarus.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final private int CODIGO_DE_ACTIVIDAD = 777 ;

    private static List<Event> eventList ;

    private RecyclerView recycler ;
    private EventAdapter adapter ;
    private LinearLayoutManager manager ;

    private FirebaseFirestore db;
    private FirebaseAuth fbAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        fbAuth = FirebaseAuth.getInstance();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setSubtitle(fbAuth.getCurrentUser().getEmail());

        eventList = new ArrayList<Event>();

        takeFirestoreData(db.collection(getString(R.string.fbDb_events_collection)));



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                Toast.makeText(this, getString(R.string.main_nooption_msg), Toast.LENGTH_SHORT).show() ;
                break;
            case R.id.action_myEvents:
                Bundle bundle = new Bundle();
                bundle.putSerializable(getString(R.string.serializable_event), (Serializable) eventList);

                Intent intent = new Intent(MainActivity.this, EventPresentActivity.class);
                intent.putExtras(bundle);

                //startActivity(intent);
                startActivityForResult(intent,CODIGO_DE_ACTIVIDAD);
                break;
            case R.id.action_logout:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                FirebaseAuth.getInstance().signOut();
                finish();
                break;
                default:
                    return super.onOptionsItemSelected(item);
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {

        switch(requestCode) {

            case CODIGO_DE_ACTIVIDAD:

                switch (resultCode) {
                    case 200:
                        takeFirestoreData(db.collection(getString(R.string.fbDb_events_collection)));
                        adapter.notifyDataSetChanged();
                        break;
                }
                break ;
        }

        super.onActivityResult(requestCode, resultCode, data) ;


    }

    public void takeFirestoreData(CollectionReference collectionReference) {

        eventList = new ArrayList<>();

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()){
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot d : list) {
                                Event event = d.toObject(Event.class);
                                event.setId(d.getId());
                                eventList.add(event);
                            }
                            recycler = findViewById(R.id.recyclerView) ;
                            manager = new LinearLayoutManager(MainActivity.this) ;
                            recycler.setLayoutManager(manager) ;

                            adapter = new EventAdapter(MainActivity.this,eventList, new EventAdapter.OnItemClickListener() {
                                @Override public void onItemClick(Event event) {
                                    Intent intent = new Intent(MainActivity.this, EventDetailActivity.class);
                                    intent.putExtra(getString(R.string.serializable_event), event);
                                    //startActivity(intent);
                                    startActivityForResult(intent,CODIGO_DE_ACTIVIDAD);
                                }
                            }) ;

                            recycler.setAdapter(adapter) ;

                        }
                    }
                });
    }

}