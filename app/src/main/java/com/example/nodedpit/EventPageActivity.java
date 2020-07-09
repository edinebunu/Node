package com.example.nodedpit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nodedpit.Firebsae.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;

public class EventPageActivity extends AppCompatActivity {

    private static final String TAG = "EventPageActivity";
    TextView mTitle;
    TextView mDate;
    TextView mTime;
    TextView mDescription;

    ImageView coverImg;

    String mDocumentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);
        final Event e = new Event();

        Intent intent = getIntent();
        mDocumentName = intent.getStringExtra("EventName");
        final String[] myId = new String[1];

        coverImg =(ImageView) findViewById(R.id.imageView4);
        mTitle = findViewById(R.id.textView6);
        mDate = findViewById(R.id.textView9);
        mDescription = findViewById(R.id.textView8);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

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

    }
}