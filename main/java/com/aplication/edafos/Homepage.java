package com.aplication.edafos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aplication.edafos.ml.Model;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class Homepage extends AppCompatActivity {

    int imagesize = 32 ;

    Button camButton, gallery, lgoutbtn, okbtn ;
    Dialog dialog;

    private DatabaseReference reference;
    private FirebaseUser user;
    private String userID;
   FirebaseAuth mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        dialog = new Dialog(this);

        SharedPreferences sprefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String soil_results = sprefs.getString("result", "");

        getSupportActionBar().hide();

        setContentView(R.layout.activity_homepage);

        camButton = findViewById(R.id.tSoil_button);
        gallery = findViewById(R.id.gallbtn);
        lgoutbtn = findViewById(R.id.Logout_button);

       // String invalid = getIntent().getStringExtra("error");


        if(new String("Not").equals(soil_results)){


            showAlertDialog();

        }


        /* if (ContextCompat.checkSelfPermission(Homepage.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {



        }else{

             ActivityCompat.requestPermissions(Homepage.this,
                     new String[]{Manifest.permission.CAMERA}, 101);
         }


         */


        camButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

          //   ImagePicker.with(Homepage.this)
            //            .start(3);


                if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    startActivityForResult(intent, 3);

                }else{

                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
                }


            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //   ImagePicker.with(Homepage.this)
                //            .start(3);


                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(intent, 1);

            }
        });

        lgoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(Homepage.this, Login.class);

                startActivity(intent);
                finish();

            }
        });



        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        final TextView username = (TextView) findViewById(R.id.Username);

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User userProfile = snapshot.getValue(User.class);


                if(userProfile != null){

                    String user = userProfile.username;

                  //  Toast.makeText(Homepage.this, "Welcome " + user, Toast.LENGTH_LONG).show();

                    username.setText(user);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(Homepage.this, "Something wrong happened!", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void showAlertDialog() {


        dialog.setContentView(R.layout.error_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        dialog.show();


        okbtn = (AppCompatButton) dialog.findViewById(R.id.okbtn);


        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
    }

    public void logout(View view){

        FirebaseAuth.getInstance().signOut();

        startActivity(new Intent(getApplicationContext(), Login.class));

        finish();
    }

    public void classifyImage(Bitmap image){

        try {
            Model model = Model.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 32, 32, 3}, DataType.FLOAT32);

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imagesize * imagesize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int [] intValues = new int [imagesize * imagesize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;

            for (int i = 0; i < imagesize; i++){
                for(int j = 0; j < imagesize; j++){
                    int val = intValues[pixel++];
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f/1));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f/1));
                    byteBuffer.putFloat((val & 0xFF) * (1.f/1));
                }

            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidence = outputFeature0.getFloatArray();

            int maxPos = 0;
            float maxConfidence = 0;
            for(int i = 0; i < confidence.length; i++){

                if (confidence[i] > maxConfidence){
                    maxConfidence = confidence[i];
                    maxPos = i;
                }
            }
            Intent intent = new Intent(Homepage.this, Loading.class);

            SharedPreferences sprefs = getSharedPreferences("prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sprefs.edit();

            String[] classes = {"Bad", "Good", "Not"};
            String result = classes[maxPos];

           // String firstStart = sprefs.getString("result", result);

            editor.putString("result", result);
            editor.apply();

            startActivity(intent);
            finish();

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode == RESULT_OK){
            if((requestCode == 3)){

                Bitmap image = (Bitmap) data.getExtras().get("data");
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image,dimension,dimension);

                image = Bitmap.createScaledBitmap(image, imagesize, imagesize, false);
                classifyImage(image);
            }else{
                Uri dat = data.getData();
                Bitmap image = null;
                try {
                     image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
                } catch (IOException e) {
                    e.printStackTrace();

                }

                image = Bitmap.createScaledBitmap(image, imagesize, imagesize, false);
                classifyImage(image);




            }


        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

