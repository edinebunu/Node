package com.example.nodedpit;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nodedpit.Firebase.UserProfile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView profile;
    String Uid;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference nameRef;
    TextView nameView;
    TextView description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Uid = user.getUid();

        UserProfile e = new UserProfile();

        nameRef = db.collection("Users").document(Uid);

        profile = findViewById(R.id.profile_img);
        nameView = findViewById(R.id.NameView);
        description = findViewById(R.id.descriptionText);

        try {
            e.getProfileImg(Uid, profile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }


        nameRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String firstName = documentSnapshot.getString("Name");
                        String lastName = documentSnapshot.getString("LastName");

                        nameView.setText(firstName + " " + lastName);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "u fed up m8", Toast.LENGTH_SHORT).show();
                    }
                });



    }
    }
