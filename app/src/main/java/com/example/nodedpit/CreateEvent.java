package com.example.nodedpit;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nodedpit.Firebsae.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;

public class CreateEvent extends AppCompatActivity {

    private static final String TAG = "CreateEvent";
    public static final int PICK_IMAGE = 10101;


    EditText mName ;
    EditText mDescription;
    EditText mLocation;

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TextView mDisplayHour;
    private TimePickerDialog.OnTimeSetListener mHourSetListener;
    int eventYear, eventMonth, eventDay, eventHour, eventMinute;

    Bitmap mCover = null;

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
                eventYear = year;
                eventMonth = month;
                eventDay = day;
            };
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: date: " + day + "/" + month + "/" + year);

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
                eventHour = mHour;
                eventMinute = mMin;
            };
        });
    }

    public void chooseCover(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"pick an image"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d(TAG, "onActivityResult: start");
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE)
        {
            try {
                InputStream inputStream = getContentResolver()
                        .openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                mCover = bitmap;
            }
            catch(FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }


    private void handleUpload(Bitmap bitmap){
        Log.d(TAG, "handleUpload: start");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);

        try {
            final StorageReference reference = FirebaseStorage.getInstance()
                    .getReference()
                    .child("Event-Covers")
                    .child(mName.getText().toString()+".jpeg");

            reference.putBytes(baos.toByteArray())
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
        catch(NullPointerException e){
            e.printStackTrace();
            Toast.makeText(this, "Error Firebase Unreached", Toast.LENGTH_SHORT).show();
        }

        Log.d(TAG, "handleUpload: end");
    }


    public void createEvent(View view)
    {
        mName =(EditText) findViewById(R.id.editTextTextPersonName5);
        mDescription =(EditText) findViewById(R.id.editTextTextPersonName8);
        mLocation = (EditText) findViewById(R.id.editTextTextPersonName6);

        String name = mName.getText().toString();
        String desc = mDescription.getText().toString();
        String loc = mLocation.getText().toString();

        if(!name.equals("") && !desc.equals("") && !loc.equals(""))
        {
        Event e = new Event();
        e.createEvent(name,desc,loc,eventYear,eventMonth,eventDay,eventHour,eventMinute);
        handleUpload(mCover);
        finish();
        }
    }

}