package com.example.nodedpit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class TaskListActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    String meetingName;
    TextView meetTitle;
    Button create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        InvitedArray.clear();

        meetTitle = findViewById(R.id.test);
        create = findViewById(R.id.button12);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        Intent intent = getIntent();
        meetingName = intent.getStringExtra("EventName");
        meetTitle.setText(meetingName);

        DocumentReference docRef = db.collection("Meetings").document(meetingName);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(!Objects.equals(documentSnapshot.getString("HostId"), currentUser.getUid()))
                {
                    create.setVisibility(View.GONE);
                }
            }
        });
        getTask();
        getDone();
    }

    public void createTask(View view){
        Intent intent = new Intent( this, CreateTaskActivity.class);
        intent.putExtra("MeetingName", meetingName);
        startActivity(intent);
    }

    private void getTask()
    {
        final ArrayList<String> ids = new ArrayList<>();

        db.collection("TaskList").document(meetingName).collection("Tasks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (!document.getBoolean("isDone")) {
                                    ids.add(document.getId());
                                }
                            }
                            initRecyclerView(ids);
                        }
                    }
                });
    }

    private void getDone()
    {
        final ArrayList<String> ids = new ArrayList<>();

        db.collection("TaskList").document(meetingName).collection("Tasks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getBoolean("isDone")) {
                                    ids.add(document.getId());
                                }
                            }
                            initDoneRecyclerView(ids);
                        }
                    }
                });
    }

    private void initRecyclerView(ArrayList<String> ids)
    {
        RecyclerView todo = findViewById(R.id.recyclerView3);
        TaskListAdapter adapter = new TaskListAdapter(ids, meetingName, this);
        todo.setAdapter(adapter);
        todo.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initDoneRecyclerView(ArrayList<String> ids)
    {
        RecyclerView todo = findViewById(R.id.doneitems);
        TaskDoneListAdapter adapter = new TaskDoneListAdapter(ids, meetingName, this);
        todo.setAdapter(adapter);
        todo.setLayoutManager(new LinearLayoutManager(this));
    }

}