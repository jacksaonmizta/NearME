package com.example.nearme;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class profile extends AppCompatActivity {
    Button btnLogout;

     private FirebaseAuth mFirebaseAuth;
     private FirebaseAuth.AuthStateListener mAuthStateListener;
     private String UserID;
     private FirebaseFirestore mFirestore;
     private TextView email, resType, BudgetL;
     private Spinner resTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(profile.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.restaurant_Type));

        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        resTypes.setAdapter(myAdapter);
        BudgetL = findViewById(R.id.BudgetLevelTV);
        mFirebaseAuth = FirebaseAuth.getInstance();
        UserID =mFirebaseAuth.getCurrentUser().getUid();
        mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection("users").document(UserID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String emailU= documentSnapshot.getString("Email");
                String restaurantTYPE = documentSnapshot.getString("Restaurant Type");
                String BudgetLvLDB = documentSnapshot.getString("Budget level");

                email.setText(emailU);
                ArrayAdapter myAdap = (ArrayAdapter) resTypes.getAdapter(); //cast to an ArrayAdapter
                int spinnerPosition = myAdap.getPosition(restaurantTYPE);

            //set the default according to value
                resTypes.setSelection(spinnerPosition);
               // resTypes.setSelection(Arrays.asList(R.array.restaurant_Type).indexOf(restaurantTYPE));
               // resTypes.setOnItemClickListener(restaurantTYPE);
                BudgetL.setText(BudgetLvLDB);
            }
        });
    }
}
