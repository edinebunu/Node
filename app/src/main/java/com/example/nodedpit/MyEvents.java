package com.example.nodedpit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MyEvents extends AppCompatActivity {

    public String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);
        Intent intent = getIntent();
        mUid = intent.getStringExtra("UID");


    }

    @Override
    protected void onStart() {
        super.onStart();

        final ArrayList<String> mNames = new ArrayList<>();
        final ArrayList<String> mDesc = new ArrayList<>();
        final ArrayList<String> mIds = new ArrayList<>();

        getEventBuffer(mNames,mDesc,mIds);
    }

    public void getEventBuffer(final ArrayList mNames, final ArrayList mDesc, final ArrayList mIds)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Events")
                .whereEqualTo("HostId", mUid)
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
                            initRecyclerView(mNames,mDesc,mIds);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void initRecyclerView(final ArrayList mNames, final ArrayList mDesc, final ArrayList mIds) {
        RecyclerView recyclerView = findViewById(R.id.myEventsView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mNames, mDesc, mIds, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}