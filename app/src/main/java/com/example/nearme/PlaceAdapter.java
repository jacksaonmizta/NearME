package com.example.nearme;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

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

        TextView lblPlace = (TextView) rowView.findViewById(R.id.lblPlaceName);
        TextView lblVicinity = (TextView) rowView.findViewById(R.id.lblVicinity);
        TextView lblLatitude = (TextView) rowView.findViewById(R.id.lblLatitude);
        TextView lblRating = (TextView) rowView.findViewById(R.id.lblRating);
        TextView lblPrice = (TextView) rowView.findViewById(R.id.lblPrice);
        ImageView image = (ImageView) rowView.findViewById(R.id.imgDirection);

        lblPlace.setText(values[0]);
        lblVicinity.setText(values[1]);
        lblLatitude.setText(values[2].concat(",").concat(values[3]));


        String pric = (values[4]);
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

        image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Singleton singleton = Singleton.getInstance();
                String current_lat = singleton.getMylat();
                String current_longi = singleton.getMylong();
             //  Toast.makeText(getContext(), current_lat,Toast.LENGTH_SHORT).show();


                Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?" + "saddr=" + current_lat + "," + current_longi + "&daddr=" + test + "," + test2);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                getContext().startActivity(mapIntent);

            }
        });


        return rowView;
    }
}
