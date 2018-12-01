package com.alejandrocaralt.icarus.Activitys;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alejandrocaralt.icarus.Models.User;
import com.alejandrocaralt.icarus.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText inputEmail, inputPwd, inputPwdR ;
    private Button registerBtn;

    private FirebaseAuth fbAuth ;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputEmail = findViewById(R.id.reg_inputEmail) ;
        inputPwd = findViewById(R.id.reg_inputPassword) ;
        inputPwdR = findViewById(R.id.reg_inputPasswordRepeat) ;
        registerBtn = findViewById(R.id.reg_registerBtn) ;

        fbAuth = FirebaseAuth.getInstance() ;

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = inputEmail.getText().toString().trim();
                String pwd = inputPwd.getText().toString().trim();
                String pwdr = inputPwdR.getText().toString().trim();

                if(user.isEmpty() || pwd.isEmpty()) {
                    Snackbar.make(view, R.string.register_error_empty, Snackbar.LENGTH_LONG).show();
                } else if (pwd.length() < 5) {
                    Snackbar.make(view, R.string.register_error_length, Snackbar.LENGTH_LONG).show();
                } else if (!pwd.equals(pwdr)) {
                    Snackbar.make(view, R.string.register_error_pwd_match, Snackbar.LENGTH_LONG).show();
                } else {
                    registerFirebase(view, user, pwd );
                }
            }
        });
    }

    private void registerFirebase(final View view,final String usr, String pwd) {
            fbAuth.createUserWithEmailAndPassword(usr, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Snackbar.make(view, R.string.register_complete, Snackbar.LENGTH_LONG).show();
                    if (task.isSuccessful()) {
                        String uid = fbAuth.getCurrentUser().getUid();

                        User user = new User(uid, usr);

                        db.collection(getString(R.string.fbDb_users_collection)).add(user);

                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Snackbar.make(view, R.string.register_error_login, Snackbar.LENGTH_LONG).show();
                    }
                }
            });
    }

}
