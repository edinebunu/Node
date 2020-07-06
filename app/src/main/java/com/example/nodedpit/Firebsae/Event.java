package com.example.nodedpit.Firebsae;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Event {
    private static final String TAG = "Event";

    public Event() {
    }

    public void createEvent(String name, String description)
    {
        final String ideaID = UUID.randomUUID().toString();

        Map<String, Object> info = new HashMap<>();
        info.put("Name",name);
        info.put("Description",description);
        info.put("Timestamp",new Date());

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
