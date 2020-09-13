package com.example.nodedpit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nodedpit.Firebase.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GestureDetector.OnGestureListener {

    private DrawerLayout mDrawLayout;

    private long backPressedTime;
    private Toast backToast;

    String mUid;

    RecyclerView recyclerView;

    UserProfile e = new UserProfile();

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mDesc = new ArrayList<>();
    private ArrayList<String> mIds = new ArrayList<>();
    private ArrayList<Boolean> isGoing = new ArrayList<>();

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

        View hView = navigationView.getHeaderView(0);

//        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = layoutInflater.inflate(R.layout.header, mDrawLayout);

        View headerView = navigationView.inflateHeaderView(R.layout.header);

        final CircleImageView drawerImage = (CircleImageView) headerView.findViewById(R.id.drawer_image);
        final TextView drawerName = (TextView) headerView.findViewById(R.id.drawer_name);



        try {
            e.getProfileImg(mUid, drawerImage);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").document(mUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                drawerName.setText(documentSnapshot.get("Name") + " " + documentSnapshot.get("LastName"));
            }
        });


    }




    public void openQr(View view){
        Intent intent = new Intent(this, QrCodeScannerActivity.class);
        startActivity(intent);
    }

    public void swipeToRight(View view){
        Intent intent = new Intent(this,MeetingsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

    }


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

                                int year = Calendar.getInstance().get(Calendar.YEAR);
                                int month = Calendar.getInstance().get(Calendar.MONTH);
                                int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                                int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                                int minute = Calendar.getInstance().get(Calendar.MINUTE);

                                if(  Integer.parseInt( Objects.requireNonNull(document.get("DateYear").toString())) > year ||

                                                Integer.parseInt(Objects.requireNonNull(document.get("DateYear").toString())) == year &&
                                                        Integer.parseInt(Objects.requireNonNull(document.get("DateMonth").toString())) > month ||

                                                Integer.parseInt(Objects.requireNonNull(document.get("DateYear").toString())) == year &&
                                                        Integer.parseInt(Objects.requireNonNull(document.get("DateMonth").toString())) == month &&
                                                        Integer.parseInt(Objects.requireNonNull(document.get("DateDay").toString())) > day ||

                                                Integer.parseInt(Objects.requireNonNull(document.get("DateYear").toString())) == year &&
                                                        Integer.parseInt(Objects.requireNonNull(document.get("DateMonth").toString())) == month &&
                                                        Integer.parseInt(Objects.requireNonNull(document.get("DateDay").toString())) == day &&
                                                        Integer.parseInt(Objects.requireNonNull(document.get("DateHour").toString())) > hour ||

                                                Integer.parseInt(Objects.requireNonNull(document.get("DateYear").toString())) == year &&
                                                        Integer.parseInt(Objects.requireNonNull(document.get("DateMonth").toString())) == month &&
                                                        Integer.parseInt(Objects.requireNonNull(document.get("DateDay").toString())) == day &&
                                                        Integer.parseInt(Objects.requireNonNull(document.get("DateHour").toString())) == hour &&
                                                        Integer.parseInt(Objects.requireNonNull(document.get("DateMin").toString())) > minute) {
                                    mNames.add(document.getString("Name"));
                                    mDesc.add(document.getString("Description"));
                                    mIds.add(document.getId());
                                }



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
            case R.id.PastMeetings:
                pastMeetings();
                break;
            case R.id.Past:
                past();
                break;

        }

        mDrawLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void past(){
        Intent intent = new Intent(MainActivity.this, PastMeetingsActivity.class);
        startActivity(intent);
    }

    private void pastMeetings(){
        Intent intent = new Intent(MainActivity.this, PastEventsActivity.class);
        startActivity(intent);
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


    public void scanButton(View view){
        //Intent intent = new Intent( MainActivity.this, QrCodeScannerActivity.class);
        //startActivity(intent);
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.initiateScan();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        if(intentResult != null)
        {
            if(intentResult.getContents() == null)
            {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Intent intent = new Intent( this, EventPageActivity.class);
                intent.putExtra("EventName", intentResult.getContents());
                startActivity(intent);
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
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