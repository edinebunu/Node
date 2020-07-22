package com.example.nodedpit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GestureDetector.OnGestureListener {

    private DrawerLayout mDrawLayout;

    private long backPressedTime;
    private Toast backToast;

    String mUid;
    String mDocumentName;
    RecyclerView recyclerView;
    CircleImageView drawerImage;

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mDesc = new ArrayList<>();
    private ArrayList<String> mIds = new ArrayList<>();

    //private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawLayout = findViewById(R.id.drawer);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawLayout, toolbar, R.string.open, R.string.close);
        mDrawLayout.addDrawerListener(toggle);
        toggle.syncState();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        mUid = mAuth.getCurrentUser().getUid();

        View hView =  navigationView.getHeaderView(0);

//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
//        itemTouchHelper.attachToRecyclerView(recyclerView);

       // this.gestureDetector = new GestureDetector(MainActivity.this, (GestureDetector.OnGestureListener) this);
    }

    public void swipeToRight(View view){
        Intent intent = new Intent(this,MeetingsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

    }
//    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT)
//    {
//        @Override
//        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//            return false;
//        }
//
//        @Override
//        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//
//            switch(direction)
//            {
//                case  ItemTouchHelper.LEFT:
//                    Intent i = new Intent(MainActivity.this, MeetingsActivity.class);
//                    startActivity(i);
//                    break;
//                case ItemTouchHelper.RIGHT:
//                    break;
//            }
//        }
//    };

    @Override
    protected void onStart() {
        super.onStart();

         mNames.removeAll(mNames);
         mDesc.removeAll(mDesc);
         mIds.removeAll(mIds);

         getEventBuffer();
    }

    public void getEventBuffer()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    private static final String TAG = "Event";
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                mNames.add(document.getString("Name"));
                                mDesc.add(document.getString("Description"));
                                mIds.add(document.getId());

                            }
                            initRecyclerView();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
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
            case R.id.Meetings:
                openMyEvents();
                break;
            case R.id.SignOut:
                SignOut();
                break;
        }

        mDrawLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openMyEvents(){
        Intent intent = new Intent(MainActivity.this, MyEvents.class);
        intent.putExtra("UID",mUid);
        startActivity(intent);
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

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mNames, mDesc, mIds, this);
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
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent( MainActivity.this, WelcomePage.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }
}