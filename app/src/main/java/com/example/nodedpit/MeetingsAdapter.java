package com.example.nodedpit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nodedpit.Firebsae.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MeetingsAdapter extends RecyclerView.Adapter<MeetingsAdapter.GoingViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> ids;
    private ArrayList<String> invited = new ArrayList<>();
    private Context mContext;

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


        public GoingViewHolder(View itemView)
        {
            super(itemView);
            name = itemView.findViewById(R.id.textView18);
            parentLayout = itemView.findViewById(R.id.meetItem);
            profile = itemView.findViewById(R.id.metprofileimg);
        }
    }
}
