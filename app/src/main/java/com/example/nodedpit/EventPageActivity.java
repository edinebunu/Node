package com.example.nodedpit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nodedpit.Firebsae.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EventPageActivity extends AppCompatActivity {

    private static final String TAG = "EventPageActivity";
    TextView mTitle;
    TextView mDate;
    TextView mTime;
    TextView mDescription;

    ImageView coverImg;

    String mDocumentName;

    RecyclerView chat;

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
        chat = findViewById(R.id.eventChatRecyclerView);

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
        getChat();
    }

    final ArrayList<String> smt = new ArrayList<>();

    private void getChat() {

        smt.clear();

        db.collection("EventChat").document(mDocumentName).collection("Chat")
                .orderBy("Timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        assert queryDocumentSnapshots != null;
                        smt.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            smt.add(document.getId());
                        }
                        updateChat(smt);
                        if(smt.size()>2)
                            chat.smoothScrollToPosition(smt.size() - 1);


                        Toast.makeText(EventPageActivity.this, "idfk", Toast.LENGTH_SHORT).show();
                    }});


//        db.collection("EventChat").document(mDocumentName).collection("Chat")
//                .orderBy("Timestamp", Query.Direction.ASCENDING)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                smt.add(document.getId());
//                            }
//                            updateChat(smt);
//                            if(smt.size()>2)
//                            chat.smoothScrollToPosition(smt.size()-1);
//                            Toast.makeText(EventPageActivity.this, "imm", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//
//                    }
//                });

    }

    public void updateChat(ArrayList<String> smt){
        EventChatListAdapter adapter = new EventChatListAdapter(smt, mDocumentName, this);
        chat.setAdapter(adapter);
        chat.setLayoutManager(new LinearLayoutManager(this));
    }

//    float x1,x2,y1,y2;
//    public boolean onTouchEvent(MotionEvent touchEvent){
//        switch(touchEvent.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                x1 = touchEvent.getX();
//                y1 = touchEvent.getY();
//                break;
//            case MotionEvent.ACTION_UP:
//                x2 = touchEvent.getX();
//                y2 = touchEvent.getY();
//                if(x1 < x2){
//                    Intent i = new Intent(EventPageActivity.this, MeetingsActivity.class);
//                    startActivity(i);
//                }else if(x1 > x2){
//                    //other direction
//                    Intent i = new Intent(EventPageActivity.this, MeetingsActivity.class);
//                    startActivity(i);
//                }
//                break;
//        }
//        return false;
//    }

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

    public void scrolToBot(ArrayList<String> smt)
    {
        chat.smoothScrollToPosition(smt.size()-1);
    }

    public void sendMessage(View view)
    {
        ConstraintLayout mainLayout;

        mainLayout = findViewById(R.id.my_activity_page);

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);


        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        EditText mtMessage;
        mtMessage = findViewById(R.id.editTextTextPersonName10);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> messageEntry = new HashMap<>();
        messageEntry.put("Host", currentUser.getUid());
        messageEntry.put("Message", mtMessage.getText().toString());
        messageEntry.put("Timestamp",new Date());


        db.collection("EventChat").document(mDocumentName).collection("Chat")
                .add(messageEntry)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        chat.smoothScrollToPosition(smt.size()-1);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

}