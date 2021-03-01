package com.example.hardbank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CartProjectAdapter extends RecyclerView.Adapter<CartProjectAdapter.MyViewHolder>  {

    Context context;
    List<SampleProject> list;

    public CartProjectAdapter(Context context,List<SampleProject> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_project_single_view,parent,false);
        return new CartProjectAdapter.MyViewHolder(V);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.projectName.setText(list.get(position).getTitle());
        Glide.with(holder.projectImage.getContext())
                .load(list.get(position).getImage())
                .into(holder.projectImage);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder{

        TextView projectName;
        ImageView projectImage;
        TextView projectPrice;
        RecyclerView recyclerView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            projectName = itemView.findViewById(R.id.projectTitle);
            projectImage = itemView.findViewById(R.id.projectImage);
            projectPrice = itemView.findViewById(R.id.projectPrice);
            recyclerView =  itemView.findViewById(R.id.componentRecyclerView);

        }
    }
}
