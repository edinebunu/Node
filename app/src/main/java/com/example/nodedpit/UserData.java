package com.example.nodedpit;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nodedpit.Firebase.UserProfile;

import java.util.Calendar;

public class UserData extends AppCompatActivity {

    private static final String TAG = "UserData";

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    EditText mCountry;
    EditText mCity;
    EditText mHobby;
    Button signUp;

    String mUid;
    String mName;
    String mLastName;
    int mDay,mMonth,mYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);

        mDisplayDate = findViewById(R.id.Dateid);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                final int year = cal.get(Calendar.YEAR);
                final int month = cal.get(Calendar.MONTH);
                final int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        UserData.this,
                        android.R.style.Theme_Holo_Dialog,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        Log.d(TAG, "onDateSet: date: " + day + "/" + month + "/" + year);

                        mDay=day;
                        mMonth=month;
                        mYear=year;

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

                        if(year > Calendar.getInstance().get(Calendar.YEAR) ) {
                            Toast.makeText(UserData.this, "Invalid birth date",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                signUp = (Button) findViewById(R.id.signUpBtn);
                signUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mCity = (EditText) findViewById(R.id.Cityid);
                        String cityString = mCity.getText().toString();

                        mCountry = (EditText) findViewById(R.id.Countryid);
                        String countryString = mCountry.getText().toString();

                        UserProfile p = new UserProfile();
                        p.createSignUp(mName,mLastName,mUid,cityString,countryString,mDay,mMonth,mYear);

                        final Intent intent = new Intent(UserData.this, MainActivity.class);
                        intent.putExtra("UID", mUid);
                        startActivity(intent);
                    }
                });

                Intent intent = getIntent();
                mUid = intent.getStringExtra("UID");
                mName = intent.getStringExtra("Name");
                mLastName = intent.getStringExtra("LastName");
            }
        });

        mHobby = (EditText) findViewById(R.id.Hobbyid);

    }
}