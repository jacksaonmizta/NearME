package com.example.nearme;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Priyanka
 */

class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

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
        Log.d("nearbyplacesdata","called parse method");
        showNearbyPlaces(nearbyPlaceList);



        Singleton singleton = Singleton.getInstance();
        Intent intent = new Intent(singleton.getContext() , mainlist.class);

       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String foods = singleton.getTitle();
        String food = foods;
        intent.putExtra("EMAIL", food.toString().trim());

        singleton.getContext().startActivity(intent);
    }

    private void showNearbyPlaces(List<HashMap<String, String>> nearbyPlaceList)
    {
        Singleton singleton = Singleton.getInstance();
        ArrayList<String> lsData = new ArrayList<>();
        for(int i = 0; i < nearbyPlaceList.size(); i++)
        {
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
