package com.example.nodedpit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateTaskActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView friends;
    EditText task;
    String meetingName;
    private ArrayList<String> mIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        InvitedArray.clear();

        friends = findViewById(R.id.friendstoselect);
        task = findViewById(R.id.editTextTextPersonName12);
        Intent intent = getIntent();
        meetingName = intent.getStringExtra("MeetingName");
        getUserBuffer();
        initTasks();
    }

    public void getUserBuffer()
    {
        db.collection("Meetings").document(meetingName).collection("GoingUsers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    private static final String TAG = "Event";
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                mIds.add(document.getId());
                            }
                            initRecyclerView();

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void createTask(View view)
    {
        String taskContent = task.getText().toString();
        mIds = InvitedArray.getmInvited();

        if(mIds.size() == 0 )
            Toast.makeText(this, "Please select someone", Toast.LENGTH_SHORT).show();
        else {
            Map<String, Object> taskEntry = new HashMap<>();
            taskEntry.put("Assigned To", mIds);
            taskEntry.put("Task", taskContent);
            taskEntry.put("isDone", false);

            db.collection("TaskList").document(meetingName).collection("Tasks")
                    .add(taskEntry)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            InvitedArray.clear();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
    }

    private void initRecyclerView() {
        InviteFriendsListAdapter adapter = new InviteFriendsListAdapter(mIds, this);
        friends.setAdapter(adapter);
        friends.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initTasks() {
        InviteFriendsListAdapter adapter = new InviteFriendsListAdapter(mIds, this);
        friends.setAdapter(adapter);
        friends.setLayoutManager(new LinearLayoutManager(this));
    }
}