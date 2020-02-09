package com.example.nearme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.List;

public class search extends AppCompatActivity {

    //Map Object
    private GoogleMap mMap;
    //Fetching current location of the device
    private FusedLocationProviderClient mFusedLocationProviderClient;
    //Loading suggestions
    private PlacesClient placesClient;
    //arrayList to save the autocomplete prediction
    private List<AutocompletePrediction> predictionList;
    // to save last known device location
    private Location mLastKnownLocation;
    // update user request
    private LocationCallback locationCallback;

    private MaterialSearchBar materialSearchBar;
    private View mapView;
    private Button btnFind;
   // private RippleBackground rippleBg;


    private final float Default_ZOOM = 18;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize and Assign Value
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set Recommend selected
        bottomNavigationView.setSelectedItemId(R.id.Search);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){

                    case R.id.History:
                        startActivity(new Intent(getApplicationContext(), history.class));
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
                    case R.id.Search:
                        return true;
                }
                return false;
            }
        });


        materialSearchBar = findViewById(R.id.searchBar);
        btnFind = findViewById(R.id.find_restaurant);
        //rippleBg = findViewById(R.id.ripple_bg);

        


    }

    //when map ready and loaded||to move map or enable buttons

}
