package com.example.nodedpit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nodedpit.Firebase.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class InviteFriendsListAdapter extends RecyclerView.Adapter<InviteFriendsListAdapter.GoingViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> ids;
    private ArrayList<String> invited = new ArrayList<>();
    private Context mContext;

    final Event e = new Event();

    public InviteFriendsListAdapter(ArrayList<String> ids, Context mContext) {
        this.mContext = mContext;
        this.ids = ids;
    }

    @NonNull
    @Override
    public InviteFriendsListAdapter.GoingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invite_friends_item,parent,false);
        return new GoingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GoingViewHolder holder, final int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").document(ids.get(position)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                holder.name.setText(documentSnapshot.getString("Name"));
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

        String myId = ids.get(position);

        try {
            e.setProfileImg(myId, holder.profilePicture);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(holder.checkBox.isChecked()) {
                    invited.add(ids.get(position));
                }
                else{
                    invited.remove(ids.get(position));
                }
                setInvited();
            }
        });

    }

    public void setInvited(){
        InvitedArray.setmInvited(invited);
    }

    @Override
    public int getItemCount() {
        return this.ids.size();
    }

    public class GoingViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        CircleImageView profilePicture;
        ConstraintLayout parentLayout;
        CheckBox checkBox;


        public GoingViewHolder(View itemView)
        {
            super(itemView);
            name = itemView.findViewById(R.id.textView17);
            parentLayout = itemView.findViewById(R.id.myEventGoingMember);
            checkBox = itemView.findViewById(R.id.checkBox3);
            profilePicture = itemView.findViewById(R.id.inviteProfilePicture);
        }
    }
}
