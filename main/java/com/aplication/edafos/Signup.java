package com.aplication.edafos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {


    EditText username, password, email, conpass;
    ProgressBar progBar;
    Button signUpbtn;
    ImageView edafos;
    private FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().hide();

        edafos = findViewById(R.id.edaimg);
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.bounce);
        progBar = (ProgressBar) findViewById(R.id.Signup_Progbar);

        fAuth = FirebaseAuth.getInstance();




        edafos = findViewById(R.id.edaimg);

        edafos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edafos.startAnimation(animation);
            }
        });

        username = findViewById(R.id.Create_Username);
        password = findViewById(R.id.Create_Password);
        email = findViewById(R.id.Mobile_Number);
        conpass = findViewById(R.id.Confirm_Password);

        signUpbtn = findViewById(R.id.signup_button);

     /*   if(fAuth.getCurrentUser() != null){

            startActivity(new Intent(getApplicationContext(), Homepage.class));
            finish();

        } */

        signUpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                registerUser();

            }

            private void registerUser() {


                String user = username.getText().toString().trim();
                String pass = password.getText().toString().trim();
                String mail = email.getText().toString().trim();
                String cPass = conpass.getText().toString().trim();

                if(user.isEmpty()){

                    username.setError("Username is Required!");
                    return;
                }
                if(user.length() < 4){

                    username.setError("Minimum Username Length is 5");
                    return;
                }
                if(mail.isEmpty()){

                    email.setError("Email Address is Required!");
                    email.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(mail).matches()){

                    email.setError("Invalid Email Address!");
                    email.requestFocus();
                    return;
                }
                if(pass.isEmpty()){

                    password.setError("Password is Required!");
                    return;

                }else if(pass.length() < 6){

                    password.setError("Minimum Password Length is 6");
                    return;
                }
                if(!cPass.equals(pass)){

                    conpass.setError("Password does not match!");
                    return;

                }

                progBar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if(task.isSuccessful()){

                            User user1 = new User(user, mail, pass); ;

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                Toast.makeText(Signup.this, "Registered successfully!", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(Signup.this, Homepage.class);
                                                startActivity(intent);
                                                progBar.setVisibility(View.GONE);

                                            }else{

                                                Toast.makeText(Signup.this,"Registration Failed!", Toast.LENGTH_LONG).show();
                                                progBar.setVisibility(View.GONE);
                                            }

                                        }
                                    });
                        }else{

                            Toast.makeText(Signup.this,"Registration Failed!", Toast.LENGTH_LONG).show();
                            progBar.setVisibility(View.GONE);
                        }


                    }
                });
            }


        });
    }





}