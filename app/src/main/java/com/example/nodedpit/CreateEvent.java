package com.example.nodedpit;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nodedpit.Firebsae.Event;

import java.util.Calendar;

public class CreateEvent extends AppCompatActivity {

    private static final String TAG = "CreateEvent";

    EditText mName ;
    EditText mDescription;

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TextView mDisplayHour;
    private TimePickerDialog.OnTimeSetListener mHourSetListener;

    String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        Intent intent = getIntent();
        mUid = intent.getStringExtra("UID");

        mDisplayDate = findViewById(R.id.Dateid);
        mDisplayDate.setPaintFlags(mDisplayDate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                final int year = cal.get(Calendar.YEAR);
                final int month = cal.get(Calendar.MONTH);
                final int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        CreateEvent.this,
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

                if(day <10 && month < 10) {
                    String date = "0" + day + "/" + "0" + month + "/" + year;
                    mDisplayDate.setText(date);
                }

                else if(day < 10) {
                    String date = "0" + day + "/" + month + "/" + year;
                    mDisplayDate.setText(date);
                }

                else if(month < 10) {
                    String date = day + "/" + "0" + month + "/" + year;
                    mDisplayDate.setText(date);
                }

                else {
                    String date = day + "/" + month + "/" + year;
                    mDisplayDate.setText(date);
                }
            }
        };

        mDisplayHour = findViewById(R.id.Hourid);
        mDisplayHour.setPaintFlags(mDisplayHour.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mDisplayHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                final int mHour = cal.get(Calendar.HOUR_OF_DAY);
                final int mMin = cal.get(Calendar.MINUTE);

                TimePickerDialog dialog = new TimePickerDialog(CreateEvent.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int min) {
                        Log.d(TAG, "onTimeSet: date: " + hour + ":" + min );

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


    public void createEvent(View view)
    {
        mName =(EditText) findViewById(R.id.editTextTextPersonName5);
        mDescription =(EditText) findViewById(R.id.editTextTextPersonName8);
        String name = mName.getText().toString();
        String desc = mDescription.getText().toString();

        Event e = new Event();
        e.createEvent(name,desc);
        finish();
    }

}