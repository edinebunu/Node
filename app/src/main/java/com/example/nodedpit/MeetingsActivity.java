package com.example.nodedpit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MeetingsActivity extends AppCompatActivity {


    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mIds = new ArrayList<>();
    private ArrayList<String> mDesc = new ArrayList<>();

    private FirebaseAuth mAuth;

    private RecyclerView recyclerView;

    Button TaskButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetings);
        recyclerView = findViewById(R.id.meetingsList);



        getMeetingsBuffer();
    }

    public void swipeToLeft(View view){

        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }


    public void createMeeting(View view)
    {
        Intent intent = new Intent(this, CreateMeeting.class);
        startActivity(intent);
    }

    public void getMeetingsBuffer()
    {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Meetings")
                .whereArrayContains("Invited", currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    private static final String TAG = "Event";
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                mNames.add(document.getString("Name"));

                            }
                            initRecyclerView();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }


    private void initRecyclerView() {

        MeetingsAdapter adapter = new MeetingsAdapter( mNames, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


}