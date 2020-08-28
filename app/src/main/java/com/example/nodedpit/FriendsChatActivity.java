package com.example.nodedpit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FriendsChatActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String mDocumentName;
    final ArrayList<String> smt = new ArrayList<>();
    RecyclerView chat;

    EditText mtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_chat);
        Intent intent = getIntent();
        mDocumentName = intent.getStringExtra("User");
        chat = findViewById(R.id.recyclerView4);
        mtMessage = findViewById(R.id.editTextTextPersonName13);
    }

    public void deleteMessage(View view){
        mtMessage.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        getChat();

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();

        db.collection("FriendsChat").document(mAuth.getUid()).collection("Chat").document(mDocumentName).collection("ChatRoom")
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

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();

        db.collection("FriendsChat").document(mAuth.getUid()).collection("Chat").document(mDocumentName).collection("ChatRoom")
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

    public void uploadMessage(View view) {
        ConstraintLayout mainLayout;

        mainLayout = findViewById(R.id.meetChat);

//        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (!mtMessage.getText().toString().equals("")) {
            Map<String, Object> messageEntry = new HashMap<>();
            messageEntry.put("Host", mAuth.getUid());
            messageEntry.put("Message", mtMessage.getText().toString());
            messageEntry.put("Timestamp", new Date());

            db.collection("FriendsChat").document(mDocumentName).collection("Chat").document(mAuth.getUid()).collection("ChatRoom")
                    .add(messageEntry)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            chat.smoothScrollToPosition(smt.size());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

            db.collection("FriendsChat").document(mAuth.getUid()).collection("Chat").document(mDocumentName).collection("ChatRoom")
                    .add(messageEntry)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            chat.smoothScrollToPosition(smt.size());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

            mtMessage.setText("");
        }
    }
}