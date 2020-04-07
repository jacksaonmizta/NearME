package com.example.nearme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.nearme.History.HistoryData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class history extends AppCompatActivity {

    private RecyclerView mHistoryRec;

    private List<HistoryData> mHistoryList;

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mSharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        // Initialize and Assign Value
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        mHistoryRec = findViewById(R.id.rec_history);

        //Set Recommend selected
        bottomNavigationView.setSelectedItemId(R.id.History);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){

                    case R.id.Search:
                        startActivity(new Intent(getApplicationContext(), search.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.Profile:
                        startActivity(new Intent(getApplicationContext(), profile.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.Recommend:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.History:
                        return true;
                }
                return false;
            }
        });

        getAllList();
    }


    private void setHistoryAdapter() {
        HistoryAdapter historyAdapter = new HistoryAdapter(this, mHistoryList);
        mHistoryRec.setLayoutManager(new LinearLayoutManager(this));
        mHistoryRec.setAdapter(historyAdapter);
    }


    private void getAllList() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "Loading, please wait...");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("history")
                .whereEqualTo("userId", mSharedPreferences.getString("USER_ID", ""))
                .whereEqualTo("addToHistory", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            mHistoryList = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("task", document.getId() + " => " + document.getData());
                                HistoryData historyData = new HistoryData();
                                historyData.setId(document.getId());
                                historyData.setAddress(document.getString("address"));
                                historyData.setTitle(document.getString("title"));
                                historyData.setRestaurantType(document.getString("restaurantType"));
                                historyData.setAddToHistory(document.getBoolean("addToHistory"));
                                historyData.setLatitude(document.getString("latitude"));
                                historyData.setLongitude(document.getString("longitude"));
                                historyData.setPrice(document.getString("price"));
                                historyData.setRating(document.getString("rating"));
                                historyData.setTimestemp(document.getDate("timestemp"));
                                historyData.setUserId(document.getString("userId"));
                                mHistoryList.add(historyData);
                            }

                            setHistoryAdapter();
                        } else {
                            Log.d("Error", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
