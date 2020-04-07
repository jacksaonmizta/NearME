package com.example.nearme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.nearme.History.HistoryData;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private ListView mListView;

    private List<String> mRestaurantTypeList = new ArrayList<>();
    private List<HistoryData> mRecomandationList;

    private SharedPreferences mSharedPreferences;
    private GPSTracker mGpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        mGpsTracker = new GPSTracker(this);

       /* btnLogout = findViewById(R.id.signOutBtn);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, Login_Form.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        );
                startActivity(intent);
                finish();

            }
        });*/

       mListView = findViewById(R.id.lsView);

        // Initialize and Assign Value
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set Recommend selected
        bottomNavigationView.setSelectedItemId(R.id.Recommend);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.Search:
                        startActivity(new Intent(getApplicationContext(), search.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.History:
                        startActivity(new Intent(getApplicationContext(), history.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.Profile:
                        startActivity(new Intent(getApplicationContext(), profile.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.Recommend:
                        return true;
                }
                return false;
            }
        });
    }


    public <T> T mostCommon(List<T> list) {
        Map<T, Integer> map = new HashMap<>();

        for (T t : list) {
            Integer val = map.get(t);
            map.put(t, val == null ? 1 : val + 1);
        }

        Map.Entry<T, Integer> max = null;

        for (Map.Entry<T, Integer> e : map.entrySet()) {
            if (max == null || e.getValue() > max.getValue())
                max = e;
        }

        return max.getKey();
    }


    private String getUrl(double latitude, double longitude, String resturant) {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/search/json?");

        googlePlaceUrl.append("location=" + latitude + "," + longitude);
        googlePlaceUrl.append("&radius=" + "5000");
        googlePlaceUrl.append("&type=restaurant");
        googlePlaceUrl.append("&keyword=" + resturant);

        googlePlaceUrl.append("&key=" + "AIzaSyDWlriOcs2d2_mr2i3aOpJ1q_0d8i9jjE0");


        return googlePlaceUrl.toString();
    }



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
                            mRecomandationList = new ArrayList<>();

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
                                mRecomandationList.add(historyData);
                                mRestaurantTypeList.add(historyData.getRestaurantType());
                            }

                            System.out.println("mostCommon---" + mostCommon(mRestaurantTypeList));

                            if (mRestaurantTypeList.size() > 0) {
                                Object dataTransfer[] = new Object[3];
                                RecommendationAsync recommendationAsync = new RecommendationAsync();

                                String url = getUrl(mGpsTracker.getLatitude(), mGpsTracker.getLongitude(), mostCommon(mRestaurantTypeList));
                                System.out.println("--- URL " + url);
                                dataTransfer[0] = null;
                                dataTransfer[1] = url;
                                dataTransfer[2] = mostCommon(mRestaurantTypeList);

                                Singleton singleton = Singleton.getInstance();

                                singleton.setContext(getBaseContext());
                                singleton.setTitle(mostCommon(mRestaurantTypeList));
                                recommendationAsync.execute(dataTransfer);
                            }

                        } else {
                            Log.d("Error", "Error getting documents: ", task.getException());
                        }
                    }
                });
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
                .addOnSuccessListener(new OnSuccessListener< Void >() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Added to history", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                    }
                });
    }

    class RecommendationAsync extends AsyncTask<Object, String, String> {

        private String googlePlacesData;
        private GoogleMap mMap;
        String url;
        String type;

        @Override
        protected String doInBackground(Object... objects){
            mMap = (GoogleMap)objects[0];
            url = (String)objects[1];
            type = (String) objects[2];

            DownloadURL downloadURL = new DownloadURL();
            try {
                googlePlacesData = downloadURL.readUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return googlePlacesData;
        }

        @Override
        protected void onPostExecute(String s){
            List<HashMap<String, String>> nearbyPlaceList;
            DataParser parser = new DataParser();
            nearbyPlaceList = parser.parse(s);

            showNearbyPlaces(nearbyPlaceList);

            Singleton singleton = Singleton.getInstance();
            PlaceAdapter placeAdapter = new PlaceAdapter(MainActivity.this, singleton.getLsData());
            mListView.setAdapter(placeAdapter);

            Collections.sort(mRecomandationList, new Comparator<HistoryData>() {
                public int compare(HistoryData o1, HistoryData o2) {
                    if (o1.getTimestemp() == null || o2.getTimestemp() == null)
                        return 0;
                    return o1.getTimestemp().compareTo(o2.getTimestemp());
                }
            });

            if (mRecomandationList.size() > 0 && !mRecomandationList.get(mRecomandationList.size() - 1).isAddToHistory()) {
                askForExperience(mRecomandationList.get(mRecomandationList.size() - 1));
            }
        }


        private void showNearbyPlaces(List<HashMap<String, String>> nearbyPlaceList){
            Singleton singleton = Singleton.getInstance();
            ArrayList<String> lsData = new ArrayList<>();
            for(int i = 0; i < nearbyPlaceList.size(); i++){
                MarkerOptions markerOptions = new MarkerOptions();
                HashMap<String, String> googlePlace = nearbyPlaceList.get(i);

                System.out.println("googlePlace---- " + googlePlace);

                String placeName = googlePlace.get("place_name");
                String vicinity = googlePlace.get("vicinity");
                String price_level = googlePlace.get("price_level");
                String rating = googlePlace.get("rating");
                double lat = Double.parseDouble( googlePlace.get("lat"));
                double lng = Double.parseDouble( googlePlace.get("lng"));

                LatLng latLng = new LatLng( lat, lng);
                markerOptions.position(latLng);
                markerOptions.title(placeName + " : "+ vicinity);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                lsData.add(placeName+ ":" + vicinity + ":" + String.valueOf(lat)  + ":" + String.valueOf(lng) + ":" + String.valueOf(price_level) + ":" + String.valueOf(rating) + ":" + type);
            }
            singleton.setLsData(lsData);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        getAllList();
    }
}
