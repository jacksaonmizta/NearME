package com.example.nearme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login_Form extends AppCompatActivity {
    public EditText emailId, password;
    Button btnSignIn;
    TextView tvSignUp;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__form);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.tv_username);
        password = findViewById(R.id.tv_password);
        tvSignUp = findViewById(R.id.Register_buttonTV);
        btnSignIn = findViewById(R.id.Login_buttonID);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if( mFirebaseUser != null){
                    Toast.makeText(Login_Form.this,"You are logged in",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login_Form.this, PermissionActivity.class);
                    startActivity(intent);

                }
                else{
                    Toast.makeText(Login_Form.this,"Please Login",Toast.LENGTH_SHORT).show();

                }

            }
        };

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailId.getText().toString();
                String pwd = password.getText().toString();
                if(email.isEmpty()){
                    emailId.setError("Please enter email address");
                    emailId.requestFocus();
                }

                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailId.setError("Please enter valid email address");
                    emailId.requestFocus();
                }

                if (password.getText().toString().length() < 6) {
                    password.setError("password minimum contain 6 character");
                    password.requestFocus();
                }
                if (password.getText().toString().equals("")) {
                    password.setError("please enter password");
                    password.requestFocus();
                }

                else if (!(email.isEmpty() && pwd.isEmpty())){
                    mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(Login_Form.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(Login_Form.this,"Login Error, Please login again", Toast.LENGTH_SHORT).show();

                            }
                            else{
                                Intent intToHome = new Intent(Login_Form.this, MainActivity.class);
                                startActivity(intToHome);
                            }
                        }
                    });
                }

                else{
                    Toast.makeText(Login_Form.this,"Error occured!", Toast.LENGTH_SHORT).show();

                }
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intSignUp = new Intent(Login_Form.this, Signup_Form.class);
                startActivity(intSignUp);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}
