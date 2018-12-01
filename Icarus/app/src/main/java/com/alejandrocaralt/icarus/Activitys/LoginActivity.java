package com.alejandrocaralt.icarus.Activitys;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alejandrocaralt.icarus.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    // Layout items
    private EditText inputEml, inputPwd ;
    private Button loginBtn , registerBtn;


    // Instantiate FirebaseAuth and FirebaseDatabase
    private FirebaseAuth fbAuth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // Instantiate the layout items
        inputEml = findViewById(R.id.log_inputEmail) ;
        inputPwd = findViewById(R.id.log_inputPassword) ;
        loginBtn = findViewById(R.id.log_loginBtn) ;
        registerBtn = findViewById(R.id.log_registerBtn) ;

        fbAuth = FirebaseAuth.getInstance() ;

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user = inputEml.getText().toString().trim() ;
                String pwd = inputPwd.getText().toString().trim() ;

                if (user.isEmpty() || pwd.isEmpty()) {
                    Snackbar.make(view, R.string.login_error_empty, Snackbar.LENGTH_LONG).show();
                } else if(pwd.length() < 5) {
                    Snackbar.make(view, R.string.login_error_length, Snackbar.LENGTH_LONG).show();
                } else {
                    loginFirebase(view, user, pwd);
                }
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class) ;
                startActivity(intent);
            }
        });

    }

    private void loginFirebase(final View view, String usr, String pwd) {
        fbAuth.signInWithEmailAndPassword(usr, pwd)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Snackbar.make(view, R.string.login_error, Snackbar.LENGTH_LONG).show();
                        }
                    }
                });

    }
}
