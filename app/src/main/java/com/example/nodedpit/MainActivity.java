package com.example.nodedpit;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nodedpit.Firebsae.UserProfile;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    TextView ttt;
    ImageView imgv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserProfile p = new UserProfile();

        imgv = (ImageView) findViewById(R.id.imageView);

        try {
            p.getProfileImg(FirebaseAuth.getInstance().getCurrentUser().getUid(),imgv);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }







}