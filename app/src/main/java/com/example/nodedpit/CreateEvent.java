package com.example.nodedpit;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nodedpit.Firebsae.Event;

import java.util.Calendar;

public class CreateEvent extends AppCompatActivity {

    EditText mName ;
    EditText mDescription;
    Button mDate;

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        Intent intent = getIntent();
        mUid = intent.getStringExtra("UID");


        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                //Log.d(TAG, "onDateSet: date: " + day + "/" + month + "/" + year);

                if(day <10 && month < 10) {
                    String date = "0" + day + "/" + "0" + month + "/" + year;
                    mDate.setText(date);
                }

                else if(day < 10) {
                    String date = "0" + day + "/" + month + "/" + year;
                    mDate.setText(date);
                }

                else if(month < 10) {
                    String date = day + "/" + "0" + month + "/" + year;
                    mDate.setText(date);
                }

                else {
                    String date = day + "/" + month + "/" + year;
                    mDate.setText(date);
                }

                if(year > Calendar.getInstance().get(Calendar.YEAR) ) {
                    Toast.makeText(CreateEvent.this, "Invalid birth date",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
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