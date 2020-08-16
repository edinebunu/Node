package com.example.nodedpit;

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
import java.util.Calendar;
import java.util.Objects;

public class PastMeetingsActivity extends AppCompatActivity {

    private ArrayList<String> mNames = new ArrayList<>();

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_meetings);
        recyclerView = findViewById(R.id.mPastMeetingsList);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mNames.removeAll(mNames);

        getEventBuffer();
    }

    public void getEventBuffer()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Meetings")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    private static final String TAG = "Meeting";
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int year = Calendar.getInstance().get(Calendar.YEAR);
                            int month = Calendar.getInstance().get(Calendar.MONTH);
                            int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                            int minute = Calendar.getInstance().get(Calendar.MINUTE);

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(
                                        Integer.parseInt(Objects.requireNonNull(document.get("Year").toString())) < year ||


                                                Integer.parseInt(Objects.requireNonNull(document.get("Year").toString())) == year &&
                                                        Integer.parseInt(Objects.requireNonNull(document.get("Month").toString())) < month ||


                                                Integer.parseInt(Objects.requireNonNull(document.get("Year").toString())) == year &&
                                                        Integer.parseInt(Objects.requireNonNull(document.get("Month").toString())) == month &&
                                                        Integer.parseInt(Objects.requireNonNull(document.get("Day").toString())) < day ||


                                                Integer.parseInt(Objects.requireNonNull(document.get("Year").toString())) == year &&
                                                        Integer.parseInt(Objects.requireNonNull(document.get("Month").toString())) == month &&
                                                        Integer.parseInt(Objects.requireNonNull(document.get("Day").toString())) == day &&
                                                        Integer.parseInt(Objects.requireNonNull(document.get("Hour").toString())) < hour ||


                                                Integer.parseInt(Objects.requireNonNull(document.get("Year").toString())) == year &&
                                                        Integer.parseInt(Objects.requireNonNull(document.get("Month").toString())) == month &&
                                                        Integer.parseInt(Objects.requireNonNull(document.get("Day").toString())) == day &&
                                                        Integer.parseInt(Objects.requireNonNull(document.get("Hour").toString())) == hour &&
                                                        Integer.parseInt(Objects.requireNonNull(document.get("Minute").toString())) < minute)

                                {
                                    mNames.add(document.getString("Name"));

                                }
                            }
                            initRecyclerView();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }


    private void initRecyclerView() {

        MeetingsAdapter adapter = new MeetingsAdapter(mNames, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}