package com.example.nodedpit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nodedpit.Firebase.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsChatListAdapter extends RecyclerView.Adapter<FriendsChatListAdapter.GoingViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    private final String mUid = currentUser.getUid();

    private ArrayList<String> ids;
    private ArrayList<String> messages;
    private ArrayList<String> messagesId;
    private Context mContext;

    final Event e = new Event();
    String eventName;

    public FriendsChatListAdapter(ArrayList<String> ids,ArrayList<String> messages,ArrayList<String> messagesId, String eventName, Context mContext) {
        this.mContext = mContext;
        this.ids = ids;
        this.messages = messages;
        this.messagesId = messagesId;
        this.eventName = eventName;
    }

    @NonNull
    @Override
    public FriendsChatListAdapter.GoingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_bubble,parent,false);
        return new FriendsChatListAdapter.GoingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FriendsChatListAdapter.GoingViewHolder holder, final int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if(messagesId.get(position).equals(mUid))
        {
            holder.mMsg.setText(messages.get(position));
            holder.mMsg.setVisibility(View.VISIBLE);
            holder.imgRight.setVisibility(View.VISIBLE);
            holder.msg.setVisibility(View.GONE);
            holder.imgLeft.setVisibility(View.GONE);
            try {
                e.setProfileImg(messagesId.get(position),holder.imgRight);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        else  {
            holder.msg.setText(messages.get(position));
            holder.msg.setVisibility(View.VISIBLE);
            holder.imgLeft.setVisibility(View.VISIBLE);
            holder.mMsg.setVisibility(View.GONE);
            holder.imgRight.setVisibility(View.GONE);
            try {
                e.setProfileImg(messagesId.get(position),holder.imgLeft);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }


    @Override
    public int getItemCount() {
        return this.ids.size();
    }

    public class GoingViewHolder extends RecyclerView.ViewHolder{

        TextView msg;
        TextView mMsg;
        ConstraintLayout parentLayout;
        CircleImageView imgLeft;
        CircleImageView imgRight;
        public GoingViewHolder(View itemView)
        {
            super(itemView);
            msg = itemView.findViewById(R.id.chat_msg);
            parentLayout = itemView.findViewById(R.id.chat_item);
            mMsg = itemView.findViewById(R.id.self_msg);
            imgLeft = itemView.findViewById(R.id.chat_left_img);
            imgRight = itemView.findViewById(R.id.chat_right_img);
        }
    }
}

