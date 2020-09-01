package com.example.nodedpit;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nodedpit.Firebase.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MeetingsAdapter extends RecyclerView.Adapter<MeetingsAdapter.GoingViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ArrayList<String> ids;
    private ArrayList<String> invited = new ArrayList<>();
    private Context mContext;

    private FirebaseAuth mAuth;

    private String UserID;

    final Event e = new Event();

    public MeetingsAdapter(ArrayList<String> ids, Context mContext) {
        this.mContext = mContext;
        this.ids = ids;
    }

    @NonNull
    @Override
    public MeetingsAdapter.GoingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_list_item,parent,false);
        return new MeetingsAdapter.GoingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MeetingsAdapter.GoingViewHolder holder, final int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final String myId = ids.get(position);

        mAuth = FirebaseAuth.getInstance();

        final FirebaseUser currentUser = mAuth.getCurrentUser();
        UserID = mAuth.getCurrentUser().getUid();

        ButtonChangerGoing(holder.going, ids.get(position));
        ButtonChangerInterested(holder.notGoing, ids.get(position));

        db.collection("Meetings").document(ids.get(position)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                holder.name.setText(documentSnapshot.getString("Name"));
                setProfilePicture(documentSnapshot.getString("HostId"),holder.profile);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        holder.going.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goingButtonPressed(myId,currentUser.getUid(), holder.going);
            }
        });

        holder.notGoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notGoingButtonPressed(myId,currentUser.getUid(), holder.notGoing);
            }
        });

        holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MeetingsChatActivity.class);
                intent.putExtra("Id",ids.get(position));
                mContext.startActivity(intent);
            }
        });


        final ArrayList<String> goingIds = new ArrayList<>();

        db.collection("Meetings").document(ids.get(position)).collection("GoingUsers").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                goingIds.add(document.getId());
                            }
                            initRecyclerView(holder,goingIds);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent( mContext, TaskListActivity.class);
                intent.putExtra("EventName", ids.get(position));
                mContext.startActivity(intent);

            }
        });
    }

    private void initRecyclerView( final MeetingsAdapter.GoingViewHolder holder,ArrayList<String> gids) {

        MeetingProfilePictureAdapter adapter = new MeetingProfilePictureAdapter( gids, this.mContext);
        holder.recyclerView.setAdapter(adapter);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(this.mContext,LinearLayoutManager.HORIZONTAL,false));
    }


    public void goingButtonPressed(final String uid, final String userId, final Button going){

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Meetings").document(uid).collection("GoingUsers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentName = document.getId();
                                if(document.getId().equals(userId)){
                                    going.setBackgroundResource(R.drawable.going);
                                    db.collection("Meetings").document(uid)
                                            .collection("GoingUsers").document(userId).delete();
                                    return;
                                }
                            }
                            Map<String, Object> user = new HashMap<>();
                            user.put("first", "Aac");
                            going.setBackgroundResource(R.drawable.going_green);
                            db.collection("Meetings").document(uid)
                                    .collection("GoingUsers").document(userId).set(user);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void notGoingButtonPressed(final String uid, final String userId, final Button notgoing){

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Meetings").document(uid).collection("NotGoingUsers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentName = document.getId();
                                if(document.getId().equals(userId)){
                                    notgoing.setBackgroundResource(R.drawable.not_going);
                                    db.collection("Meetings").document(uid)
                                            .collection("NotGoingUsers").document(userId).delete();
                                    return;
                                }
                            }
                            Map<String, Object> user = new HashMap<>();
                            user.put("first", "Aac");
                            notgoing.setBackgroundResource(R.drawable.not_going_green);
                            db.collection("Meetings").document(uid)
                                    .collection("NotGoingUsers").document(userId).set(user);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }


    private void setProfilePicture(String uid, final CircleImageView coverImage)
    {
                        try {
                            e.setProfileImg(uid,coverImage);

                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
    }


    @Override
    public int getItemCount() {
        return this.ids.size();
    }

    public class GoingViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        CircleImageView profile;
        ConstraintLayout parentLayout;
        Button going;
        Button notGoing;
        Button chat;
        RecyclerView recyclerView;



        public GoingViewHolder(View itemView)
        {
            super(itemView);
            name = itemView.findViewById(R.id.textView18);
            parentLayout = itemView.findViewById(R.id.meetItem);
            profile = itemView.findViewById(R.id.metprofileimg);
            going = itemView.findViewById(R.id.button7);
            notGoing = itemView.findViewById(R.id.button8);
            recyclerView = itemView.findViewById(R.id.profile_pictures_list);
            chat = itemView.findViewById(R.id.chatBtn);
        }
    }

    public void ButtonChangerGoing(final Button going, final String ids){
        db.collection("Meetings").document(ids).collection("GoingUsers")
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
                            going.setBackgroundResource(R.drawable.going);
                        } else {
                            Log.w(TAG, "Error changing button", task.getException());
                        }
                    }
                });
    }

    public void ButtonChangerInterested (final Button notGoing, final String ids) {
        db.collection("Meetings").document(ids).collection("InterestedUsers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentName = document.getId();
                                if (document.getId().equals(UserID)) {
                                    notGoing.setBackgroundResource(R.drawable.not_going_green)
                                    ;
                                    return;
                                }
                            }
                            notGoing.setBackgroundResource(R.drawable.not_going);
                        } else {
                            Log.w(TAG, "Error changing button", task.getException());
                        }
                    }
                });
    }

}
