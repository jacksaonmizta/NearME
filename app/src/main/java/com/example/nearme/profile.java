package com.example.nearme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class profile extends AppCompatActivity {
    Button btnLogout;

     private FirebaseAuth mFirebaseAuth;
     private FirebaseAuth.AuthStateListener mAuthStateListener;
     private String UserID;
     private FirebaseFirestore mFirestore;
     private TextView email, resType;
     private Spinner resTypes, BudgetL;

     private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mSharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        // Initialize and Assign Value
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set Recommend selected
        bottomNavigationView.setSelectedItemId(R.id.Profile);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.History:
                        startActivity(new Intent(getApplicationContext(), history.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.Search:
                        startActivity(new Intent(getApplicationContext(), search.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.Recommend:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.Profile:
                        return true;
                }
                return false;
            }
        });

        btnLogout = findViewById(R.id.signOutBtn);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(profile.this, Login_Form.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        );
// TEST
                startActivity(intent);
                finish();

            }
        });

        // retrieve data from firestore

        email = findViewById(R.id.emailTV);
        resTypes = findViewById(R.id.restaurantTV);
        BudgetL = findViewById(R.id.BudgetLevelTV);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(profile.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.restaurant_Type));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        resTypes.setAdapter(myAdapter);

        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<>(profile.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.budger_type));
        myAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        BudgetL.setAdapter(myAdapter2);

        mFirestore = FirebaseFirestore.getInstance();

        final ProgressDialog progressDialog = ProgressDialog.show(profile.this, null, "Loading, please wait...");

        mFirestore.collection("users")
                .document(mSharedPreferences.getString("USER_ID", ""))
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressDialog.dismiss();

                        String restaurantTYPE = documentSnapshot.getString("Restaurant Type");
                        String BudgetLvLDB = documentSnapshot.getString("Budget level");

                        email.setText(documentSnapshot.getString("Email"));

                        for (int i = 0; i < getResources().getStringArray(R.array.restaurant_Type).length; i++) {
                            if (getResources().getStringArray(R.array.restaurant_Type)[i].equalsIgnoreCase(restaurantTYPE)) {
                                resTypes.setSelection(i);
                                break;
                            }
                        }

                        for (int i = 0; i < getResources().getStringArray(R.array.budger_type).length; i++) {
                            if (getResources().getStringArray(R.array.budger_type)[i].equalsIgnoreCase(BudgetLvLDB)) {
                                BudgetL.setSelection(i);
                                break;
                            }
                        }
                    }
                });


        findViewById(R.id.signOutBtn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = ProgressDialog.show(profile.this, null, "Loading, please wait...");

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> user = new HashMap<>();
                user.put("Budget level", BudgetL.getSelectedItem().toString());
                user.put("Restaurant Type", resTypes.getSelectedItem().toString());
                user.put("Email", mSharedPreferences.getString("EMAIL", ""));

                db.collection("users")
                        .document(mSharedPreferences.getString("USER_ID", ""))
                        .set(user)
                        .addOnSuccessListener(new OnSuccessListener < Void > () {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                Toast.makeText(profile.this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                            }
                        });
            }
        });
    }

}
