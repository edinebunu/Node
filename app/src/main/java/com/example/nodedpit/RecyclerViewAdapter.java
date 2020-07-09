package com.example.nodedpit;

import android.content.Context;
import android.content.Intent;
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

    private ArrayList<String> names;
    private ArrayList<String> desc;
    private ArrayList<String> ids;
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<String> names, ArrayList<String> desc, ArrayList<String> ids, Context mContext) {
        this.names = names;
        this.desc = desc;
        this.mContext = mContext;
        this.ids = ids;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.desc.setText((CharSequence) this.desc.get(position));
        holder.name.setText((CharSequence)this.names.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent( mContext, EventPageActivity.class);
                intent.putExtra("EventName", ids.get(position));
                mContext.startActivity(intent);

            }
        });

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
            name = itemView.findViewById(R.id.textView4);
            desc = itemView.findViewById(R.id.description);
            parentLayout = itemView.findViewById(R.id.parentLayout);

        }
    }
}
