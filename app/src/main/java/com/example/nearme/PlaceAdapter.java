package com.example.nearme;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.valueOf;

public class PlaceAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> names;

    static class ViewHolder {
        public TextView text;
        public ImageView image;
    }

    public PlaceAdapter(Activity context, ArrayList<String> names) {
        super(context, R.layout.placelist, names);
        this.context = context;
        this.names = names;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.placelist, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) rowView.findViewById(R.id.lblPlaceName);

            rowView.setTag(viewHolder);
        }

        String values[] = names.get(position).split(":");

        final TextView lblPlace = (TextView) rowView.findViewById(R.id.lblPlaceName);
        final TextView lblVicinity = (TextView) rowView.findViewById(R.id.lblVicinity);
        TextView lblLatitude = (TextView) rowView.findViewById(R.id.lblLatitude);
        final TextView lblRating = (TextView) rowView.findViewById(R.id.lblRating);
        TextView lblPrice = (TextView) rowView.findViewById(R.id.lblPrice);
        ImageView image = (ImageView) rowView.findViewById(R.id.imgDirection);

        lblPlace.setText(values[0]);
        lblVicinity.setText(values[1]);
        lblLatitude.setText(values[2].concat(",").concat(values[3]));


        final String pric = (values[4]);
        int price = valueOf(pric);
        if (price == 1) {
            String pricetext = "LOW";
            lblPrice.setText(pricetext);
            lblPrice.setTextColor(Color.GREEN);
        } else if (price == 4) {
            String pricetext = "HIGH";
            lblPrice.setText(pricetext);
            lblPrice.setTextColor(Color.RED);
        } else if (price == 3) {
            String pricetext = "HIGH";
            lblPrice.setText(pricetext);
            lblPrice.setTextColor(Color.RED);
        } else {
            String pricetext = "MEDIUM";
            lblPrice.setText(pricetext);
            lblPrice.setTextColor(Color.parseColor("#D4AF37"));
        }

        lblRating.setText(values[5]);
        final String test = values[2];
        final String test2 = values[3];
        final String restaurantType = values[6];

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Singleton singleton = Singleton.getInstance();
                String current_lat = singleton.getMylat();
                String current_longi = singleton.getMylong();

                addSearchToFirebase(lblPlace.getText().toString(), lblVicinity.getText().toString(), pric, current_lat, current_longi, test, test2, lblRating.getText().toString(), restaurantType);
            }
        });
        return rowView;
    }



    private void addSearchToFirebase(String title, String address, String price, final String latitude, final String longitude, final String address1, final String address2, String rating, String restaurantType) {
        final ProgressDialog progressDialog = ProgressDialog.show(context, null, "Loading, please wait...");
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> user = new HashMap<>();
                user.put("userId", sharedPreferences.getString("USER_ID", ""));
                user.put("title", title);
                user.put("restaurantType", restaurantType);
                user.put("address", address);
                user.put("price", price);
                user.put("latitude", address1);
                user.put("longitude", address2);
                user.put("rating", rating);
                user.put("addToHistory", false);
                user.put("timestemp", new Date());

                db.collection("history")
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                progressDialog.dismiss();
                                Log.d("SUCCESS", "DocumentSnapshot added with ID: " + documentReference.getId());

                                System.out.println("----------- " + "http://maps.google.com/maps?" + "saddr=" + latitude + "," + longitude+ "&daddr=" + address1 + "," + address2);

                                Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?" + "saddr=" + latitude + "," + longitude+ "&daddr=" + address1 + "," + address2);
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                getContext().startActivity(mapIntent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                e.printStackTrace();
                            }
                        });
    }
}
