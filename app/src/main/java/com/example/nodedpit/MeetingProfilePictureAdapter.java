package com.example.nodedpit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nodedpit.Firebsae.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MeetingProfilePictureAdapter extends RecyclerView.Adapter<MeetingProfilePictureAdapter.GoingViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> ids;
    private Context mContext;

    private FirebaseAuth mAuth;

    final Event e = new Event();

    public MeetingProfilePictureAdapter(ArrayList<String> ids, Context mContext) {
        this.mContext = mContext;
        this.ids = ids;
    }

    @NonNull
    @Override
    public MeetingProfilePictureAdapter.GoingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_list_picture,parent,false);
        return new MeetingProfilePictureAdapter.GoingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MeetingProfilePictureAdapter.GoingViewHolder holder, final int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String myId = ids.get(position);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        try {
            e.setProfileImg(myId, holder.profilePicture);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return this.ids.size();
    }

    public class GoingViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profilePicture;
        ConstraintLayout parentLayout;


        public GoingViewHolder(View itemView)
        {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.ccpi);
            profilePicture = itemView.findViewById(R.id.meet_pf_img);
        }
    }
}
