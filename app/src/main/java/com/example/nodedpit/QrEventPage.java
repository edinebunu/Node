package com.example.nodedpit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class QrEventPage extends AppCompatActivity {

    String eventId;
    ImageView qrHolder;
    Button download;

    Bitmap bm;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReferenceFromUrl("gs://node-85fa5.appspot.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_event_page);

        Intent intent = getIntent();
        eventId = intent.getStringExtra("EventId");

        qrHolder = findViewById(R.id.imageView7);

        try {
            setImg();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setImg() throws IOException {

        StorageReference mRef = storageReference.child("qr-codes").child(eventId+".jpeg");
        final File file = File.createTempFile("eventId","jpeg");
        mRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                bm = bitmap;
                qrHolder.setImageBitmap(bitmap);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    public void downloadCode(View view)
    {
        MediaStore.Images.Media.insertImage(getContentResolver(), bm, eventId , "Smt");
    }

}