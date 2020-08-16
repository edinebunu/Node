package com.example.nodedpit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nodedpit.Firebsae.Event;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventGoingListAdapter extends RecyclerView.Adapter<EventGoingListAdapter.GoingViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> ids;
    private Context mContext;

    final Event e = new Event();

    public EventGoingListAdapter(ArrayList<String> ids, Context mContext) {
        this.mContext = mContext;
        this.ids = ids;
    }

    @NonNull
    @Override
    public EventGoingListAdapter.GoingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.interest_list_item,parent,false);
        return new GoingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GoingViewHolder holder, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String myId = ids.get(position);

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
            parentLayout = itemView.findViewById(R.id.myEventGoingMember);
            profilePicture = itemView.findViewById(R.id.miniProfileImage);
        }
    }
}
