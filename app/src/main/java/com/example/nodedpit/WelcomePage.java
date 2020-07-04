package com.example.nodedpit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomePage extends AppCompatActivity {


    int a;
    private static final String TAG = "WelcomePage";
    Button SignUp;
    Button LogIn;
    Button test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);

        SignUp = (Button) findViewById(R.id.signup);

        SignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(WelcomePage.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        LogIn = (Button) findViewById(R.id.login);

        LogIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(WelcomePage.this, LogInActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


}
