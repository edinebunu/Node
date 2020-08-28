package com.example.nodedpit;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nodedpit.Firebase.Meetings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;

public class CreateMeeting extends AppCompatActivity {

    private static final String TAG = "CreateMeeting";

    RecyclerView recyclerView;
    EditText mName;

    private ArrayList<String> mIds = new ArrayList<>();

    private FirebaseAuth mAuth;

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TextView mDisplayHour;
    private TimePickerDialog.OnTimeSetListener mHourSetListener;
    int eventYear = -1, eventMonth = -1, eventDay = -1, eventHour = -1, eventMinute = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meeting);

        InvitedArray.clear();

        recyclerView = findViewById(R.id.inviteFriends);
        mName = findViewById(R.id.editTextTextPersonName7);

        getMeetingsBuffer();

        mDisplayDate = findViewById(R.id.Dateid);
        ///mDisplayDate.setPaintFlags(mDisplayDate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                final int year = cal.get(Calendar.YEAR);
                final int month = cal.get(Calendar.MONTH);
                final int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        CreateMeeting.this,
                        android.R.style.Theme_Holo_Dialog,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            };
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: date: " + day + "/" + month + "/" + year);

                eventYear = year;
                eventMonth = month;
                eventDay = day;

                if(day <10 && month < 10) {
                    String date = "Date: " + "0" + day + "/" + "0" + month + "/" + year;
                    mDisplayDate.setText(date);
                }

                else if(day < 10) {
                    String date = "Date: " + "0" + day + "/" + month + "/" + year;
                    mDisplayDate.setText(date);
                }

                else if(month < 10) {
                    String date = "Date: " + day + "/" + "0" + month + "/" + year;
                    mDisplayDate.setText(date);
                }

                else {
                    String date = "Date: " + day + "/" + month + "/" + year;
                    mDisplayDate.setText(date);
                }
            }
        };

        mDisplayHour = findViewById(R.id.Hourid);
        ///mDisplayHour.setPaintFlags(mDisplayHour.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mDisplayHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                final int mHour = cal.get(Calendar.HOUR_OF_DAY);
                final int mMin = cal.get(Calendar.MINUTE);

                TimePickerDialog dialog = new TimePickerDialog(CreateMeeting.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int min) {
                        Log.d(TAG, "onTimeSet: date: " + hour + ":" + min );

                        eventHour = hour;
                        eventMinute = min;

                        if(hour < 10 && min < 10) {
                            String date = "Time: " + "0" + hour + ":" + "0" + min;
                            mDisplayHour.setText(date);
                        }
                        else if(hour < 10 && min > 10) {
                            String date = "Time: " + "0" + hour + ":" + min;
                            mDisplayHour.setText(date);
                        }
                        else if(hour > 10 && min < 10) {
                            String date = "Time: " + hour + ":" + "0" + min;
                            mDisplayHour.setText(date);
                        }
                        else{
                            String date = "Time: " + hour + ":" + min;
                            mDisplayHour.setText(date);
                        }
                    }
                },mHour,mMin, true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
                dialog.show();
            };
        });

    }


    public void getMeetingsBuffer()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users")
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

    private void initRecyclerView() {
        InviteFriendsListAdapter adapter = new InviteFriendsListAdapter(mIds, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void createButton(View view)
    {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        ArrayList<String> invited = InvitedArray.getmInvited();

        Meetings m = new Meetings();

        if(eventYear != -1 && eventMonth != -1 && eventDay != -1 && eventHour != -1 && eventMinute!= -1)
        {
            m.createMeet(mName.getText().toString(), invited, currentUser.getUid(),eventYear,eventMonth,eventDay,eventHour,eventMinute);
            finish();
        }
        InvitedArray.clear();
    }

}













