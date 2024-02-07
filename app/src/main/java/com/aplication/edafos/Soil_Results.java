package com.aplication.edafos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Soil_Results extends AppCompatActivity {

    Button retHome, soil_info;
    ImageView resultImage;
    TextView resultText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.soil_results);

        resultImage = (ImageView) findViewById(R.id.checked);
        resultText = (TextView) findViewById(R.id.result);

        getSupportActionBar().hide();

        retHome = (Button) findViewById(R.id.testbtn);
        soil_info = (Button) findViewById(R.id.webbtn);

        ForegroundColorSpan red = new ForegroundColorSpan(Color.parseColor("#FF0000"));
        ForegroundColorSpan green = new ForegroundColorSpan(Color.parseColor("#7AC043"));

        // String results = getIntent().getStringExtra("result");

        SharedPreferences sprefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String soil_results = sprefs.getString("result", "");

        if(new String("Bad").equals(soil_results)){


            String bad_result = "BAD";

            soil_info.setVisibility(View.VISIBLE);

            SpannableString spanString = new SpannableString(bad_result);


            spanString.setSpan(red, 0, 3, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

            resultText.append(spanString);

        }else{


            String good_result = "GOOD";
            //resultText.append("GOOD");

            resultImage.setImageResource(R.drawable.image2);

            soil_info.setVisibility(View.GONE);

            RelativeLayout.LayoutParams params  = (RelativeLayout.LayoutParams) retHome.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.result);
            params.setMargins(0, 20, 0, 0);

            retHome.setLayoutParams(params);


            SpannableString spanString = new SpannableString(good_result);


            spanString.setSpan(green, 0, 4, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

            resultText.append(spanString);


        }

        retHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Soil_Results.this, Homepage.class);
                startActivity(intent);
                finish();
            }
        });

        soil_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Soil_Results.this, ButtonWeb.class);
                startActivity(intent);
            }
        });


    }
}