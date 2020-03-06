package com.example.nearme;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class mainlist extends AppCompatA ctivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainlist);

        Singleton singleton = Singleton.getInstance();

        ListView lsView = (ListView) findViewById(R.id.lsView);


        String emailFromIntent = getIntent().getStringExtra("EMAIL");
        TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setText(emailFromIntent);
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

        ImageView img = (ImageView)findViewById(R.id.imgView);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
