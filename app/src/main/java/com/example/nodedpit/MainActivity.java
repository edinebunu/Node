package com.example.nodedpit;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawLayout;
    private ActionBarDrawerToggle mToggle;
    private long backPressedTime;
    private Toast backToast;
    String mUid;

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mDesc = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //LogInActivity.PreferenceData.setUserLoggedInStatus(this,true);

        Intent intent = getIntent();
        mUid = intent.getStringExtra("UID");


        mNames.add("aa");
        mDesc.add("bb");

        mNames.add("cc");
        mDesc.add("dd");

        mNames.add("ee");
        mDesc.add("ff");

        mNames.add("gg");
        mDesc.add("hh");

        mNames.add("aa");
        mDesc.add("bb");

        mNames.add("cc");
        mDesc.add("dd");

        mNames.add("ee");
        mDesc.add("ff");

        mNames.add("gg");
        mDesc.add("hh");

        mNames.add("aa");
        mDesc.add("bb");

        mNames.add("cc");
        mDesc.add("dd");

        mNames.add("ee");
        mDesc.add("ff");

        mNames.add("gg");
        mDesc.add("hh");

        initRecyclerView();

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

    public void btnPressed(View view){
        Intent intent = new Intent(this, CreateEvent.class);
        intent.putExtra("UID",mUid);
        startActivity(intent);
    }

    private void initRecyclerView(){

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mNames, mDesc, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}























