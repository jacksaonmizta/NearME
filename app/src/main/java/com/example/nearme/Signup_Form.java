package com.example.nearme;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Signup_Form extends AppCompatActivity {
    public static final String TAG = "TAG";
    public EditText emailId, password;
    Button btnSignUp;
    TextView tvSignIn;
    FirebaseAuth mFirebaseAuth;
    FirebaseFirestore fstore;

    Spinner restaurantType;
    RadioGroup radioGroup;
    RadioButton radioButton;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup__form);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.tv_username);
        password = findViewById(R.id.tv_password);
        tvSignIn = findViewById(R.id.LoginSignUp_tv);
        btnSignUp = findViewById(R.id.Signup_ButtonID);

        restaurantType = findViewById(R.id.RtypeSpinner);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(Signup_Form.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.restaurant_Type));

        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        restaurantType.setAdapter(myAdapter);

        radioGroup = findViewById(R.id.radioGroup);

        int radioId = radioGroup.getCheckedRadioButtonId();

        radioButton=findViewById(radioId);

        fstore = FirebaseFirestore.getInstance();




        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailId.getText().toString();
                String pwd = password.getText().toString();
                final String RT = restaurantType.getSelectedItem().toString();
                final String BT = radioButton.getText().toString();

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
                            if(task.isSuccessful()){
                                Toast.makeText(Signup_Form.this,"User Created.", Toast.LENGTH_SHORT).show();
                                userID = mFirebaseAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = fstore.collection("users").document(userID);
                                Map<String, Object> user = new HashMap<>();
                                user.put("Email", email);
                                user.put("Restaurant Type", RT);
                                user.put("Budget level", BT);
                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                                        Intent intent = new Intent(Signup_Form.this, Login_Form.class);
                                        startActivity(intent);
                                    }

                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error adding document", e);
                                            }


                                });

                            }

                            else{
                                Toast.makeText(Signup_Form.this,"SignUp unsuccessful, Please try again later", Toast.LENGTH_SHORT).show();
                            }

                        }


                    });
                }

                else{
                    Toast.makeText(Signup_Form.this,"Error Occured!", Toast.LENGTH_SHORT).show();

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




}
