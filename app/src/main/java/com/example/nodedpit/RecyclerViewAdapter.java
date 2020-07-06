package com.example.nodedpit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> desc = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<String> names, ArrayList<String> desc, Context mContext) {
        this.names = names;
        this.desc = desc;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.desc.setText((CharSequence) this.desc.get(position));
        holder.name.setText((CharSequence)this.names.get(position));

//        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(mContext, "Clicked", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView desc;
        ConstraintLayout parentLayout;

        public ViewHolder(View itemView)
        {
            super(itemView);
            name = itemView.findViewById(R.id.materialTextView);
            desc = itemView.findViewById(R.id.textView4);

        }
    }
}
