package com.example.nodedpit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QrCodeScannerActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scanner);
        openCamera();
    }

    public  void openCamera(){
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        if(intentResult != null)
        {
            if(intentResult.getContents() == null)
            {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Intent intent = new Intent( this, EventPageActivity.class);
                intent.putExtra("EventName", intentResult.getContents());
                startActivity(intent);
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }


}