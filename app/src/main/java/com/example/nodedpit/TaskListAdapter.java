package com.example.nodedpit;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nodedpit.Firebase.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ArrayList<String> ids;
    private Context mContext;
    String meetingName;

    final Event e = new Event();

    public TaskListAdapter(ArrayList<String> ids, String name, Context mContext) {
        this.ids = ids;
        this.meetingName = name;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public TaskListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item,parent,false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        DocumentReference docRef = db.collection("TaskList").document(meetingName).collection("Tasks").document(ids.get(position));
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                holder.desc.setText(documentSnapshot.getString("Task"));
                ArrayList<String> i = (ArrayList<String>) documentSnapshot.get("Assigned To");

                holder.name.setText("");
                DocumentReference dRef = db.collection("Users").document(i.get(0));
                dRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                            holder.name.append(documentSnapshot.getString("Name"));
                    }
                });

                for(int index = 1 ; index < i.size(); index++) {
                    DocumentReference docRef = db.collection("Users").document(i.get(index));
                    final int ind = index;

                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                holder.name.append(", " + documentSnapshot.getString("Name"));
                        }
                    });

                }
            }
        });
    }

    private String getName(String id)
    {
        final String[] name = new String[1];
        DocumentReference docRef = db.collection("Users").document(id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                name[0] = documentSnapshot.getString("Name");
            }
        });
        return name[0];
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
        return ids.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView desc;
        CheckBox check;
        CircleImageView profilePicture;
        ConstraintLayout parentLayout;

        public ViewHolder(View itemView)
        {
            super(itemView);
            name = itemView.findViewById(R.id.textView22);
            desc = itemView.findViewById(R.id.textView23);
            profilePicture = itemView.findViewById(R.id.profile_image);
            check = itemView.findViewById(R.id.checkBox2);
        }
    }
}
