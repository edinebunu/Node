package com.example.nodedpit;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nodedpit.Firebase.Event;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ArrayList<String> names;
    private ArrayList<String> desc;
    private ArrayList<String> ids;
    private Context mContext;

    private String UserID;

    final Event e = new Event();

    public RecyclerViewAdapter(ArrayList<String> names, ArrayList<String> desc, ArrayList<String> ids, Context mContext) {
        this.names = names;
        this.desc = desc;
        this.mContext = mContext;
        this.ids = ids;

    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item,parent,false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.desc.setText((CharSequence) this.desc.get(position));
        holder.name.setText((CharSequence)this.names.get(position));

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        UserID = mAuth.getCurrentUser().getUid();

        final String mDocumentName = this.ids.get(position);

        ButtonChangerGoing(holder.going, ids.get(position));
        ButtonChangerInterested(holder.interested, ids.get(position));

        holder.going.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    e.goingButtonPressed(ids.get(position),currentUser.getUid(), holder.going);
                }
            });

        holder.interested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                e.interestedButtonPressed(ids.get(position),currentUser.getUid(), holder.interested);
            }
        });



        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("Events").document(mDocumentName);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        try {
                            e.setCoverImg(document.getString("Name"),holder.coverImage);
                            setProfilePicture(mDocumentName,holder.profilePicture);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent( mContext, EventPageActivity.class);
                intent.putExtra("EventName", ids.get(position));
                mContext.startActivity(intent);

            }
        });

    }

    private void setProfilePicture(String documentId, final CircleImageView coverImage)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Events").document(documentId);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        try {
                            e.setProfileImg(document.getString("HostId"),coverImage);

                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView desc;
        ImageView coverImage;
        CircleImageView profilePicture;
        ConstraintLayout parentLayout;
        Button going;
        Button interested;

        public ViewHolder(View itemView)
        {
            super(itemView);
            name = itemView.findViewById(R.id.textView4);
            desc = itemView.findViewById(R.id.description);
            parentLayout = itemView.findViewById(R.id.parentLayout);
            parentLayout.setClipToOutline(true);
            coverImage = itemView.findViewById(R.id.imageView2);
            profilePicture = itemView.findViewById(R.id.profile_image);
            going = itemView.findViewById(R.id.going);
            interested = itemView.findViewById(R.id.interested);


        }
    }

    public void ButtonChangerInterested (final Button interested, final String ids) {
        db.collection("Events").document(ids).collection("InterestedUsers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentName = document.getId();
                                if (document.getId().equals(UserID)) {
                                    interested.setBackgroundResource(R.drawable.interested_green);
                                    return;
                                }
                            }
                            interested.setBackgroundResource(R.drawable.interested);
                        } else {
                            Log.w(TAG, "Error changing button", task.getException());
                        }
                    }
                });
    }


    public void ButtonChangerGoing(final Button going, final String ids){
        db.collection("Events").document(ids).collection("GoingUsers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentName = document.getId();
                                if (document.getId().equals(UserID)) {
                                    going.setBackgroundResource(R.drawable.going_green);
                                    return;
                                }
                            }
                            going.setBackgroundResource(R.drawable.evgo);
                        } else {
                            Log.w(TAG, "Error changing button", task.getException());
                        }
                    }
                });
    }

}
