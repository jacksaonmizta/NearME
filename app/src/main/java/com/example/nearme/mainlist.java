package com.example.nearme;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nearme.History.HistoryData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.WriteResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class mainlist extends AppCompatActivity {

    private SharedPreferences mSharedPreferences;

    private List<HistoryData> mHistoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainlist);

        mSharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        Singleton singleton = Singleton.getInstance();

        ListView lsView = (ListView) findViewById(R.id.lsView);

        String emailFromIntent = getIntent().getStringExtra("EMAIL");
      //  TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
     //   txtTitle.setText(emailFromIntent);
        PlaceAdapter placeAdapter = new PlaceAdapter(this,singleton.getLsData());

        lsView.setAdapter(placeAdapter);
        lsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               // int color = (int) parent.getAdapter().getItem(position);

               //Toast.makeText(this, tweets[position], Toast.LENGTH_SHORT).show();
              //  Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="+ singleton.getLsData()+","+singleton.getLsData() ));
            }
        });

      //  ImageView img = (ImageView)findViewById(R.id.imgView);
       // img.setOnClickListener(new View.OnClickListener() {
     //       @Override
    //        public void onClick(View v) {
    //            finish();
    }
    //    });
   // }


    private void getAllList() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "Loading, please wait...");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("history")
                .whereEqualTo("userId", mSharedPreferences.getString("USER_ID", ""))
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

                            Collections.sort(mHistoryList, new Comparator<HistoryData>() {
                                public int compare(HistoryData o1, HistoryData o2) {
                                    if (o1.getTimestemp() == null || o2.getTimestemp() == null)
                                        return 0;
                                    return o1.getTimestemp().compareTo(o2.getTimestemp());
                                }
                            });

                            System.out.println("--- " + mHistoryList.get(mHistoryList.size() - 1).getTitle());

                            if (mHistoryList.size() > 0 && !mHistoryList.get(mHistoryList.size() - 1).isAddToHistory()) {
                                askForExperience(mHistoryList.get(mHistoryList.size() - 1));
                            }
                        } else {
                            Log.d("Error", "Error getting documents: ", task.getException());
                        }
                    }
                });
                /*.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (!documentSnapshots.isEmpty()) {
                            System.out.println("documentSnapshots---- " + documentSnapshots.getDocuments());
                            List<HistoryData> history = documentSnapshots.toObjects(HistoryData.class);
                            System.out.println("history---" + history.size());

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });*/
    }


    private void askForExperience(final HistoryData historyData) {
        new AlertDialog.Builder(this)
                .setMessage("Did you like your previous Experience?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        addToHistory(historyData);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deleteHistory(historyData);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .show();
    }


    private void deleteHistory(HistoryData historyData) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("history").document(historyData.getId()).delete();
    }


    private void addToHistory(HistoryData historyData) {
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "Loading, please wait...");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        historyData.setAddToHistory(true);

        db.collection("history").document(historyData.getId()).set(historyData)
                .addOnSuccessListener(new OnSuccessListener < Void > () {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(mainlist.this, "Added to history", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                    }
                });
    }


    @Override
    protected void onResume() {
        super.onResume();

        getAllList();
    }
}
