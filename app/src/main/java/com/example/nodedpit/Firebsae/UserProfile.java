package com.example.nodedpit.Firebsae;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile {

    private static final String TAG = "UserProfile";

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReferenceFromUrl("gs://node-85fa5.appspot.com/");

    public void getProfileImg(String uid, final CircleImageView view) throws IOException {
        final Bitmap mImg;
        StorageReference mRef = storageReference.child("profile-images").child(uid+".jpeg");
        final File file = File.createTempFile("image","jpeg");
        mRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                view.setImageBitmap(bitmap);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    public void createSignUp(String name, String lastName, String UID, String city , String country, int dd, int mm, int yy)
    {
        Map<String, String> info = new HashMap<>();
        info.put("Name",name);
        info.put("LastName",lastName);
        info.put("City",city);
        info.put("Country",country);


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").document(UID)
                .set(info)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

}
