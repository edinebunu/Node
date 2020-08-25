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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nodedpit.Firebase.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {
    private static final String TAG = "FriendsAdapter";

    private ArrayList<String> mIds = new ArrayList<>();
    private Context mContext;
    Event e = new Event();


    public FriendsAdapter(ArrayList<String> mName, Context mContext) {
        this.mIds = mName;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_layout, parent, false);

        return new FriendsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        setProfilePicture(mIds.get(position), holder.image);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").document(mIds.get(position)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                holder.name.setText(documentSnapshot.getString("Name") + " "  + documentSnapshot.getString("LastName"));

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( mContext, FriendsChatActivity.class);
                intent.putExtra("User", mIds.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mIds.size();
    }

    private void setProfilePicture(String documentId, final CircleImageView coverImage)
    {

                        try {
                            e.setProfileImg(documentId, coverImage);

                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image;
        TextView name;
        CardView layout;
        Button chat;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.friendsImage);
            name = itemView.findViewById(R.id.friendsName);
            layout = itemView.findViewById(R.id.cardLayout);
            chat = itemView.findViewById(R.id.button14);
        }
    }
}
