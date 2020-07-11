package com.example.nodedpit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nodedpit.Firebsae.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;

public class EventPageActivity extends AppCompatActivity {

    private static final String TAG = "EventPageActivity";
    TextView mTitle;
    TextView mDate;
    TextView mTime;
    TextView mDescription;

    ImageView coverImg;

    String mDocumentName;

    private ArrayList<String> mIds = new ArrayList<>();
    private ArrayList<String> mIntIds = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);
        final Event e = new Event();

        Intent intent = getIntent();
        mDocumentName = intent.getStringExtra("EventName");

        coverImg =(ImageView) findViewById(R.id.imageView4);
        mTitle = findViewById(R.id.textView6);
        mDate = findViewById(R.id.textView9);
        mDescription = findViewById(R.id.textView8);

        DocumentReference docRef = db.collection("Events").document(mDocumentName);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        mTitle.setText(document.getString("Name"));
                        mDescription.setText(document.getString("Description"));

                        try {
                            e.setCoverImg(document.getString("Name"),coverImg);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        getUsers();
        getInterestedUsers();
    }

    public void getUsers()
    {
        db.collection("Events").document(mDocumentName).collection("GoingUsers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                mIds.add(document.getId());
                            }
                            initGoingRecyclerView();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void getInterestedUsers()
    {
        db.collection("Events").document(mDocumentName).collection("InterestedUsers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                mIntIds.add(document.getId());
                            }
                            initInterestedRecyclerView();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void initGoingRecyclerView() {

        RecyclerView recyclerView = findViewById(R.id.GoingUsers);
        EventGoingListAdapter adapter = new EventGoingListAdapter(mIds, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initInterestedRecyclerView() {

        RecyclerView recyclerView = findViewById(R.id.InterestedUsers);
        EventInterestedListAdapter adapter = new EventInterestedListAdapter(mIntIds, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}