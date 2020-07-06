package com.example.nodedpit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nodedpit.Firebsae.Event;

public class CreateEvent extends AppCompatActivity {

    EditText mName ;
    EditText mDescription;

    String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        Intent intent = getIntent();
        mUid = intent.getStringExtra("UID");
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