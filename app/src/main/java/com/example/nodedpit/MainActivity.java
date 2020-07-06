package com.example.nodedpit;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nodedpit.Firebsae.UserProfile;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private long backPressedTime;
    private Toast backToast;
    TextView ttt;
    CircleImageView imgv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserProfile p = new UserProfile();

        imgv = (CircleImageView) findViewById(R.id.profile_image);

        try {
            p.getProfileImg(FirebaseAuth.getInstance().getCurrentUser().getUid(),imgv);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {

        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        }
        else{
            backToast = Toast.makeText(MainActivity.this, "Press back again to exit",
                    Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }





}