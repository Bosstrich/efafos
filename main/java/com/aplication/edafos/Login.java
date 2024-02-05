package com.aplication.edafos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Login extends AppCompatActivity {

    TextView textView;
    Button loginbtn;
    EditText email, password;
    ProgressBar progBar;
    FirebaseAuth fAuth;
    ImageView edafos;
    private ViewPager slideViewPager;
    private LinearLayout mDotLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progBar = (ProgressBar) findViewById(R.id.Login_Progbar);


        getSupportActionBar().hide();

        textView = (TextView) findViewById(R.id.Signup_text);
        loginbtn = findViewById(R.id.button);
        email = findViewById(R.id.Enter_Username);
        password = findViewById(R.id.Enter_Password);
        fAuth = FirebaseAuth.getInstance();

        edafos = findViewById(R.id.edaimg);
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.bounce);


        edafos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edafos.startAnimation(animation);
            }
        });



        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginUser();

            }
        });


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Login.this, Signup.class);
                startActivity(intent);


            }
        });
    }

    private void loginUser() {

        String pass = password.getText().toString().trim();
        String mail = email.getText().toString().trim();

        if(mail.isEmpty()){

            email.setError("Username is Required!");
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

        progBar.setVisibility(View.VISIBLE);

        fAuth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if(user.isEmailVerified()) {

                        startActivity(new Intent(Login.this, Homepage.class));
                        Toast.makeText(Login.this, "Successfully Logged in!", Toast.LENGTH_LONG).show();
                        progBar.setVisibility(View.GONE);
                        finish();
                    }else{

                        user.sendEmailVerification();
                        Toast.makeText(Login.this, "Check your Email to verify your account", Toast.LENGTH_LONG).show();

                    }


                }else{

                    Toast.makeText(Login.this, "Login Failed! ", Toast.LENGTH_LONG).show();
                    progBar.setVisibility(View.GONE);

                }


            }
        });



    }
}