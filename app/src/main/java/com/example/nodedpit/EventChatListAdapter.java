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

import java.util.ArrayList;

public class EventChatListAdapter extends RecyclerView.Adapter<EventChatListAdapter.GoingViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> ids;
    private Context mContext;

    final Event e = new Event();
    String eventName;

    public EventChatListAdapter(ArrayList<String> ids,String eventName, Context mContext) {
        this.mContext = mContext;
        this.ids = ids;
        this.eventName = eventName;
    }

    @NonNull
    @Override
    public EventChatListAdapter.GoingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_bubble,parent,false);
        return new EventChatListAdapter.GoingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventChatListAdapter.GoingViewHolder holder, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("EventChat").document(eventName).collection("Chat").document(ids.get(position))
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                holder.msg.setText(documentSnapshot.getString("Message"));
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }


    @Override
    public int getItemCount() {
        return this.ids.size();
    }

    public class GoingViewHolder extends RecyclerView.ViewHolder{

        TextView msg;
        ConstraintLayout parentLayout;


        public GoingViewHolder(View itemView)
        {
            super(itemView);
            msg = itemView.findViewById(R.id.chat_msg);
            parentLayout = itemView.findViewById(R.id.chat_item);
        }
    }
}
