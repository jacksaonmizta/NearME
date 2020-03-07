package com.example.nearme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Signup_Form extends AppCompatActivity {
    public EditText emailId, password;
    Button btnSignUp;
    TextView tvSignIn;
    Spinner mySpinner;
    FirebaseAuth mFirebaseAuth;
    RadioButton radioButton;
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup__form);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.tv_username);
        password = findViewById(R.id.tv_password);
        tvSignIn = findViewById(R.id.LoginSignUp_tv);
        btnSignUp = findViewById(R.id.Signup_ButtonID);
        mySpinner = findViewById(R.id.typeSpinner);
        radioGroup = findViewById(R.id.radioGroup);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(Signup_Form.this,
                 android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.restaurant_Type));

        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);





        btnSignUp.setOnClickListener(new View.OnClickListener() {
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
                    mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(Signup_Form.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(Signup_Form.this,"SignUp unsuccessful, Please try again later", Toast.LENGTH_SHORT).show();
                            }

                            else{
                                startActivity(new Intent(Signup_Form.this, Login_Form.class));
                            }

                        }


                    });
                }

                else{
                    Toast.makeText(Signup_Form.this,"Error occured!", Toast.LENGTH_SHORT).show();

                }



            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intToLogin = new Intent(Signup_Form.this, Login_Form.class);
                startActivity(intToLogin);
            }
        });

    }

    public void checkButton(View view) {
        int radioId = radioGroup.getCheckedRadioButtonId();

        radioButton=findViewById(radioId);

        Toast.makeText(this, "Selected Radio Button: "+ radioButton.getText(), Toast.LENGTH_SHORT).show();
    }
};
