package com.nadza.anapp.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nadza.anapp.Models.Movie;
import com.nadza.anapp.Models.TVShow;
import com.nadza.anapp.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URL;

public class InfoActivity extends AppCompatActivity {

    ImageView backArrow, backdrop;
    TextView title, overview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        //get intent and all the data sent with the intent
        Intent intent= getIntent();
        String name=intent.getStringExtra("title");
        String img=intent.getStringExtra("img");
        String description=intent.getStringExtra("overview");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialise UI elements
        backArrow=(ImageView)findViewById(R.id.back_arrow);
        title=(TextView)findViewById(R.id.title);
        backdrop=(ImageView)findViewById(R.id.backdrop_img);
        overview=(TextView)findViewById(R.id.overview);
        //set the UI elements like title
        if(name==null){
            title.setText("Title unknown");
        } else {
            title.setText(name);
        }
        //backdrop image
        if (img == null) {
            backdrop.setImageResource(R.mipmap.no_image);
        } else {
            URL url = null;
            try {
                url = new URL("https://image.tmdb.org/t/p/w500/"+img);
            } catch (MalformedURLException e) {

            }
            Picasso.get().load(String.valueOf(url)).into(backdrop);
        }
        //overview
        if(description==null){
            overview.setText("Overview not available.");
        } else {
            overview.setText(description);
        }
        //toolbar back arrow click behaviour
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
