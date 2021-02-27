package com.example.hardbank;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class DisplayProjectContentAdapter extends FirestoreRecyclerAdapter<ProjectContent,DisplayProjectContentAdapter.MyViewHolder> {

    public DisplayProjectContentAdapter(@NonNull FirestoreRecyclerOptions<ProjectContent> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull ProjectContent model) {
        holder.topic.setText(model.getHeader());
        holder.description.setText(model.getContent());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_project_content_single_view,parent,false);
        return new DisplayProjectContentAdapter.MyViewHolder(V);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView topic;
        TextView description;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            topic =  itemView.findViewById(R.id.topicText);
            description =  itemView.findViewById(R.id.contentDescription);
        }
    }
}
