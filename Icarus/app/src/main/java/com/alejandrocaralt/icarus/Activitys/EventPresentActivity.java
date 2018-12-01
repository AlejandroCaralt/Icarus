package com.alejandrocaralt.icarus.Activitys;

import android.app.Presentation;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.alejandrocaralt.icarus.Adapters.EventAdapter;
import com.alejandrocaralt.icarus.Models.Event;
import com.alejandrocaralt.icarus.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventPresentActivity extends AppCompatActivity {
    private List<Event> eventList ;

    private RecyclerView recycler ;
    private EventAdapter adapter ;
    private LinearLayoutManager manager ;

    private FirebaseAuth fbAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_present);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        final Intent intent = getIntent();
        fbAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        eventList = new ArrayList<Event>();


        List<Event> arrayList = (List) intent.getSerializableExtra(getString(R.string.serializable_event));
        String usr;

        for (Event e : arrayList) {
            for (String s : e.getBikers()){
                usr = fbAuth.getCurrentUser().getUid();
                if (usr.equals(s)){
                    eventList.add(e);
                }
            }
        }

        inflateRecycler(eventList);
    }

    private void inflateRecycler(List<Event> events) {
        recycler = findViewById(R.id.recyclerView) ;
        manager = new LinearLayoutManager(EventPresentActivity.this) ;
        recycler.setLayoutManager(manager) ;
        adapter = new EventAdapter(EventPresentActivity.this, events, new EventAdapter.OnItemClickListener() {
            @Override public void onItemClick(Event event) {
            }
        }) ;
        recycler.setAdapter(adapter) ;
        registerForContextMenu(recycler) ;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu) ;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        Event event = eventList.get(adapter.getPosition());

        deleteParticipation(event);

        return super.onContextItemSelected(item);
    }


    public void deleteParticipation(final Event event ) {
        DocumentReference docRef = db.collection(getString(R.string.fbDb_events_collection)).document(event.getId());
        String usr = fbAuth.getCurrentUser().getUid();

        docRef.update(getString(R.string.fbDb_bikers_document), FieldValue.arrayRemove(usr)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    eventList.remove(event);
                    adapter.notifyDataSetChanged();
                    setResult(200) ;
                    Toast.makeText(EventPresentActivity.this, getString(R.string.epresent_removing_successful), Toast.LENGTH_SHORT).show() ;
                } else {
                    Toast.makeText(EventPresentActivity.this, getString(R.string.epresent_removing_error), Toast.LENGTH_SHORT).show() ;
                }
            }
        });

    }
}
