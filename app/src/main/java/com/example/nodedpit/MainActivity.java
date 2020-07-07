package com.example.nodedpit;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawLayout;


    private long backPressedTime;
    private Toast backToast;
    String mUid;

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mDesc = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawLayout = findViewById(R.id.drawer);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawLayout, toolbar, R.string.open, R.string.close);
        mDrawLayout.addDrawerListener(toggle);
        toggle.syncState();


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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.AddEvent:
                openAddEvent();
                break;
            case R.id.profileButton:
                openProfile();
                break;
            case R.id.SignOut:
                SignOut();
                break;
        }

        mDrawLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {

        if (mDrawLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawLayout.closeDrawer(GravityCompat.START);
        } else {


            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                backToast.cancel();
                super.onBackPressed();
                return;
            } else {
                backToast = Toast.makeText(MainActivity.this, "Press back again to exit",
                        Toast.LENGTH_SHORT);
                backToast.show();
            }
            backPressedTime = System.currentTimeMillis();
        }
    }

    public void btnPressed(View view) {
        Intent intent = new Intent(this, CreateEvent.class);
        intent.putExtra("UID", mUid);
        startActivity(intent);
    }

    private void initRecyclerView() {

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mNames, mDesc, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void openAddEvent() {
        Intent intent = new Intent(MainActivity.this, CreateEvent.class);
        intent.putExtra("UID",mUid);
        startActivity(intent);
    }

    public void openProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void SignOut() {
        Intent intent = new Intent(this, WelcomePage.class);
        startActivity(intent);
    }


}