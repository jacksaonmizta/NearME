package com.example.nearme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.nearme.History.HistoryData;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
 
    private List<HistoryData> mDatas;
    private Context mContext;
    private GPSTracker mGpsTracker;


 
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView lblPlaceName, lblVicinity, lblLatitude, lblRating, lblPrice;
        ImageView imgDirection;
 
        public MyViewHolder(View view) {
            super(view);
            lblPlaceName = (TextView) view.findViewById(R.id.lblPlaceName);
            lblVicinity = (TextView) view.findViewById(R.id.lblVicinity);
            lblLatitude = (TextView) view.findViewById(R.id.lblLatitude);
            lblRating = (TextView) view.findViewById(R.id.lblRating);
            lblPrice = (TextView) view.findViewById(R.id.lblPrice);

            imgDirection = view.findViewById(R.id.imgDirection);
        }
    }
 
 
    public HistoryAdapter(Context context, List<HistoryData> datas) {
        this.mContext = context;
        this.mDatas = datas;

        mGpsTracker = new GPSTracker(context);
    }
 
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.placelist, parent, false);
 
        return new MyViewHolder(itemView);
    }
 
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final HistoryData historyData = mDatas.get(position);

        holder.lblPlaceName.setText(historyData.getTitle());
        holder.lblVicinity.setText(historyData.getAddress());
        holder.lblLatitude.setText(historyData.getLongitude() + ", " + historyData.getLatitude() );
        holder.lblRating.setText(historyData.getRating());

        switch (historyData.getPrice()) {
            case "1": {
                String pricetext = "LOW";
                holder.lblPrice.setText(pricetext);
                holder.lblPrice.setTextColor(Color.GREEN);
                break;
            }
            case "4":
            case "3": {
                String pricetext = "HIGH";
                holder.lblPrice.setText(pricetext);
                holder.lblPrice.setTextColor(Color.RED);
                break;
            }
            default: {
                String pricetext = "MEDIUM";
                holder.lblPrice.setText(pricetext);
                holder.lblPrice.setTextColor(Color.parseColor("#D4AF37"));
                break;
            }
        }

        holder.imgDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?" + "saddr=" + mGpsTracker.getLatitude() + "," + mGpsTracker.getLongitude() + "&daddr=" + historyData.getLatitude() + "," + historyData.getLongitude());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                mContext.startActivity(mapIntent);
            }
        });
    }
 
    @Override
    public int getItemCount() {
        return mDatas.size();
    }
}