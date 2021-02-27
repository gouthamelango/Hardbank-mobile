package com.example.hardbank;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class AdminProjectAdapter extends FirestoreRecyclerAdapter<SampleProject,AdminProjectAdapter.MyViewHolder> {



    public AdminProjectAdapter(@NonNull FirestoreRecyclerOptions<SampleProject> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull SampleProject model) {
        holder.projectTitle.setText(model.getTitle());
        Glide.with(holder.projectImage.getContext())
                .load(model.getImage())
                .into(holder.projectImage);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_project_single_view,parent,false);
        return new AdminProjectAdapter.MyViewHolder(V);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView projectImage;
        TextView projectTitle;
        public MyViewHolder(View itemView){
            super(itemView);
            projectImage =  itemView.findViewById(R.id.projectImage);
            projectTitle =  itemView.findViewById(R.id.projectTitle);
        }

}

}
