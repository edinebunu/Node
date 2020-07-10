package com.example.nodedpit.Firebsae;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class Event {
    private static final String TAG = "Event";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void createEvent(String name, String description, String location, int yy, int mm, int dd, int hh, int min, String uid)
    {
        final String ideaID = UUID.randomUUID().toString();

        Map<String, Object> info = new HashMap<>();
        info.put("Name",name);
        info.put("Description",description);
        info.put("Location", location);
        info.put("DateYear", yy);
        info.put("DateMonth", mm);
        info.put("DateDay",dd);
        info.put("DateHour", hh);
        info.put("DateMin", min);
        info.put("Timestamp",new Date());
        info.put("HostId",uid);

        //FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Events").document()
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

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReferenceFromUrl("gs://node-85fa5.appspot.com/");

    public void setCoverImg(String eventName, final ImageView view) throws IOException {

        StorageReference mRef = storageReference.child("Event-Covers").child(eventName+".jpeg");
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

    public void getEventBuffer(final ArrayList<String> name, final ArrayList<String> desc)
    {
        db.collection("Events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    private static final String TAG = "Event";
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                name.add(document.getString("Name"));
                                desc.add(document.getString("Description"));

                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }


    public void setProfileImg(String uid, final CircleImageView view) throws IOException {
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

    public void goingButtonPressed(final String uid, final String userId){

        db.collection("Events").document(uid).collection("GoingUsers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentName = document.getId();
                                if(document.getId().equals(userId)){
                                    db.collection("Events").document(uid)
                                            .collection("GoingUsers").document(userId).delete();
                                    return;
                                }
                            }
                            Map<String, Object> user = new HashMap<>();
                            user.put("first", "Alan");
                            db.collection("Events").document(uid)
                                    .collection("GoingUsers").document(userId).set(user);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void interestedButtonPressed(final String uid, final String userId){
        db.collection("Events").document(uid).collection("InterestedUsers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentName = document.getId();
                                if(document.getId().equals(userId)){
                                    db.collection("Events").document(uid)
                                            .collection("InterestedUsers").document(userId).delete();
                                    return;
                                }
                            }
                            Map<String, Object> user = new HashMap<>();
                            user.put("first", "Alan");
                            db.collection("Events").document(uid)
                                    .collection("InterestedUsers").document(userId).set(user);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }


}
