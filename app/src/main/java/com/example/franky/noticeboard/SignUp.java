package com.example.franky.noticeboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import static android.content.ContentValues.TAG;

public class SignUp extends Activity implements AdapterView.OnItemSelectedListener {
    private Context context;
    private TextView Login;
    private EditText userEmail,userPassword,RePassword;
    private Button signUpButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    public Spinner spinner;
    private ProgressBar loginProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        //Firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference();


        context = this;
        loginProgress = (ProgressBar) findViewById(R.id.loginProgress);
        userEmail = (EditText) findViewById(R.id.userEmail);
        userPassword = (EditText) findViewById(R.id.userPassword);
        RePassword = (EditText) findViewById(R.id.RePassword);
        Login = (TextView) findViewById(R.id.Login);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Login", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, Login.class);
                startActivity(intent);
                finish();

            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String useremail = userEmail.getText().toString();
                String password = userPassword.getText().toString();
                String repassword = RePassword.getText().toString();

                if (useremail.isEmpty() || useremail == null){
                    userEmail.setError("Enter user Email");
                }else if(password.isEmpty() || password == null){
                    userPassword.setError("Enter Password");
                }else if(repassword.isEmpty() || repassword == null){
                    RePassword.setError(" Enter Password");
                }else if(!repassword.equals(password)){
                    RePassword.setError("Password Didn't Match");
                }else{
                    loginProgress.setVisibility(View.VISIBLE);
                    final String itemSelected = spinner.getSelectedItem().toString();
                    mAuth.createUserWithEmailAndPassword(useremail, password)
                            .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        //updateUI(user);
                                        addNewUser(user.getUid(),user.getEmail(),itemSelected.toString());
                                        loginProgress.setVisibility(View.GONE);

                                    } else {
                                        Log.e(TAG, "Failed to register user", task.getException());
                                        Toast.makeText(context,
                                                "Failed to register user",
                                                Toast.LENGTH_SHORT).show();
                                        loginProgress.setVisibility(View.GONE);
                                    }
                                }
                            });

                }
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Toast.makeText(context, "User Created", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "User Not Created", Toast.LENGTH_SHORT).show();
        }
    }

    //Add new user details
    private void addNewUser(String userId, String email, String subscription) {
        User user = new User(email,subscription);

        mDatabase.child("users").child(userId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(context,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    System.out.println(task.getException().toString());
                    Toast.makeText(context, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "Account can't be created", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        Object item = parent.getItemAtPosition(pos);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}

@IgnoreExtraProperties
class User {
    public String email;
    public String subscription;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email,String subscription) {
        this.email = email;
        this.subscription = subscription;
    }

}