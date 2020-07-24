package com.example.nodedpit.Firebsae;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Meetings {

    private static final String TAG = "Meetings";
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public void createMeet(String name, ArrayList<String> invitedPeople, String hostUid) {

            Map<String, Object> info = new HashMap<>();
            info.put("Invited", invitedPeople);
            info.put("Name", name);
            info.put("HostId", hostUid);

            db.collection("Meetings").document(name)
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
