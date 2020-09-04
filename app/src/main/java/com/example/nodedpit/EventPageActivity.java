package com.example.nodedpit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nodedpit.Firebase.Event;
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

    final ArrayList<String> smt = new ArrayList<>();

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
        mTime = findViewById(R.id.textView10);
        mDescription = findViewById(R.id.textView8);
        chat = findViewById(R.id.eventChatRecyclerView);

        DocumentReference docRef = db.collection("Events").document(mDocumentName);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        mTitle.setText(document.getString("Name"));
                        mDescription.setText(document.getString("Description"));
                        mDate.setText(document.get("DateDay") + " / "  + document.get("DateMonth") + " / " + document.get("DateYear"));
                        mTime.setText(document.get("DateHour") + " : "  + document.get("DateHour"));

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

        mDescription.setMovementMethod(new ScrollingMovementMethod());
    }

    public void openQrActivity(View view)
    {
        Intent intent = new Intent(this,QrEventPage.class);
        intent.putExtra("EventId", mDocumentName);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getChat();

        db.collection("EventChat").document(mDocumentName).collection("Chat")
                .orderBy("Timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        final ArrayList<String> messages = new ArrayList<>();
                        final ArrayList<String> messageId = new ArrayList<>();
                        smt.clear();
                        assert queryDocumentSnapshots != null;
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            smt.add(document.getId());
                            messages.add(document.getString("Message"));
                            messageId.add(document.getString("Host"));
                        }
                        updateChat(smt,messages,messageId);
                    }});
}


    private void getChat() {
        final ArrayList<String> messages = new ArrayList<>();
        final ArrayList<String> messageId = new ArrayList<>();

        db.collection("EventChat").document(mDocumentName).collection("Chat")
                .orderBy("Timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            smt.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                smt.add(document.getId());
                                messages.add(document.getString("Message"));
                                messageId.add(document.getString("Host"));
                            }
                            updateChat(smt,messages,messageId);

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
    }

    public void updateChat(ArrayList<String> smt, ArrayList<String> messages, ArrayList<String> messageId){
        EventChatListAdapter adapter = new EventChatListAdapter(smt,messages,messageId, mDocumentName, this);
        chat.setAdapter(adapter);
        chat.setLayoutManager(new LinearLayoutManager(this));
        if(smt.size()>2)
            chat.smoothScrollToPosition(smt.size());
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
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
    }

    private void initInterestedRecyclerView() {

        RecyclerView recyclerView = findViewById(R.id.InterestedUsers);
        EventInterestedListAdapter adapter = new EventInterestedListAdapter(mIntIds, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
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

        mtMessage.setText("");
    }

}