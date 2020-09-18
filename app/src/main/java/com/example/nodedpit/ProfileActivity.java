package com.example.nodedpit;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nodedpit.Firebase.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView profile;
    String Uid;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference nameRef;
    TextView nameView;
    TextView description;

    Button addDesc;
    EditText userDesc;
    Button descDb;
    Button changeDesc;

    TextView hobby;

    RecyclerView friend;
    TextView dateofbirth;
    TextView location;

    private FirebaseAuth mAuth;

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
        friend = findViewById(R.id.friends_view);
        dateofbirth = findViewById(R.id.DateOfBirth);
        location = findViewById(R.id.Location);

        hobby = findViewById(R.id.Hobby);

        addDesc = findViewById(R.id.button5);
        userDesc = findViewById(R.id.editTextTextPersonName14);
        descDb = findViewById(R.id.button16);
        changeDesc = findViewById(R.id.button17);

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
                        String country = documentSnapshot.getString("Country");
                        String city = documentSnapshot.getString("City");
                        String day = documentSnapshot.getString("DateDay");
                        String month = documentSnapshot.getString("DateMonth");
                        String year = documentSnapshot.getString("DateYear");
                        String currentHobby = documentSnapshot.getString("Hobby");

                        String desc = documentSnapshot.getString("Description");

                        nameView.setText(firstName + " " + lastName);
                        location.setText(city + ", " + country);
                        dateofbirth.setText(day + "/" + month + "/" + year);
                        hobby.setText("Hobby : " + currentHobby);

                        if(!desc.equals(""))
                        {
                            description.setText(desc);
                            description.setVisibility(View.VISIBLE);
                            addDesc.setVisibility(View.INVISIBLE);
                            descDb.setVisibility(View.INVISIBLE);
                            userDesc.setVisibility(View.INVISIBLE);
                            changeDesc.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            description.setVisibility(View.INVISIBLE);
                            addDesc.setVisibility(View.VISIBLE);
                            descDb.setVisibility(View.INVISIBLE);
                            userDesc.setVisibility(View.INVISIBLE);
                            changeDesc.setVisibility(View.INVISIBLE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "u fed up m8", Toast.LENGTH_SHORT).show();
                    }
                });
        
        initFriends();
    }

    private void initFriendsAdapter(ArrayList<String> mIds) {
        FriendsAdapter adapter = new FriendsAdapter(mIds, this);
        friend.setAdapter(adapter);
        friend.setLayoutManager(new LinearLayoutManager(this));
    }

    public void initFriends() {
        final ArrayList<String> mIds = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    private static final String TAG = "Event";

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(!document.getId().equals(currentUser.getUid()))
                                mIds.add(document.getId());

                            }
                            initFriendsAdapter(mIds);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void addDescription(View view)
    {
        addDesc.setVisibility(View.GONE);
        userDesc.setVisibility(View.VISIBLE);
        descDb.setVisibility(View.VISIBLE);
    }

    public void addDescFirestore(View view)
    {
        final String input = userDesc.getText().toString();

        DocumentReference washingtonRef = db.collection("Users").document(Uid);

        washingtonRef
                .update("Description", input)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        description.setText(input);
                        userDesc.setVisibility(View.INVISIBLE);
                        descDb.setVisibility(View.INVISIBLE);
                        description.setVisibility(View.VISIBLE);
                        changeDesc.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void changeCurrentDesc(View view)
    {
        addDesc.setVisibility(View.GONE);
        userDesc.setVisibility(View.VISIBLE);
        description.setVisibility(View.INVISIBLE);
        descDb.setVisibility(View.VISIBLE);
        changeDesc.setVisibility(View.INVISIBLE);
    }


}
