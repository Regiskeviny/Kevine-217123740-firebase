package com.example.franky.noticeboard;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private EditText userEmail;
    private EditText userPassword;
    private Button loginButton;
    private TextView signUp;
    private ProgressBar loginProgess;
    private Context context;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        context = this;
        userEmail = (EditText) findViewById(R.id.userEmail);
        userPassword = (EditText) findViewById(R.id.userPassword);
        loginButton = (Button) findViewById(R.id.loginButton);
        signUp = (TextView) findViewById(R.id.signUp);
        loginProgess = (ProgressBar) findViewById(R.id.loginProgress);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userEmail.getText().toString();
                String password = userPassword.getText().toString();

                if (email.isEmpty() || email == null){
                    userEmail.setError("Enter user Email");
                }else if(password.isEmpty() || password == null){
                    userPassword.setError("Enter Password");
                }else{
                    login(email,password);
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Sign up", Toast.LENGTH_SHORT).show();

                loginProgess.setVisibility(View.VISIBLE);
                Intent intent = new Intent(context, SignUp.class);
                startActivity(intent);
                loginProgess.setVisibility(View.GONE);
            }
        });
    }




    public void login(String email,String password){
        loginProgess.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithEmail:success");
                            loginProgess.setVisibility(View.GONE);
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                            Intent intent = new Intent(context,MainActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithEmail:failure", task.getException());
                            loginProgess.setVisibility(View.GONE);
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }
}
