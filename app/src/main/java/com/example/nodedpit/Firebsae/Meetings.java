package com.example.nodedpit.Firebsae;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Meetings {

    private static final String TAG = "Meetings";

    public void createEvent(String name, String description, String location, int yy, int mm, int dd, int hh, int min, String uid, ArrayList invitedPeople)
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

        FirebaseFirestore db = FirebaseFirestore.getInstance();

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
}
