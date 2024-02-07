package com.aplication.edafos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.SQLOutput;

public class Loading extends AppCompatActivity {


    ProgressBar progBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);


        SharedPreferences sprefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String soil_results = sprefs.getString("result", "");

        getSupportActionBar().hide();


        progBar = (ProgressBar) findViewById(R.id.pBar);

        progBar.setVisibility(View.VISIBLE);

        //String result  = getIntent().getStringExtra("result");
        //System.out.print(result);

        if(new String("Not").equals(soil_results)){


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent intent = new Intent(Loading.this, Homepage.class);
                    startActivity(intent);
                    finish();


                }
            }, 3000);


        }else{

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent intent = new Intent(Loading.this, Soil_Results.class);
                    //intent.putExtra("error", "Not a Soil");
                    startActivity(intent);
                    finish();


                }
            }, 3000);
        }


    }
}