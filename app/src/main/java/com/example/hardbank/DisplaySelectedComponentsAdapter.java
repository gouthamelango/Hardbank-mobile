package com.example.hardbank;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class DisplaySelectedComponentsAdapter extends FirestoreRecyclerAdapter<SelectedComponent, DisplaySelectedComponentsAdapter.MyViewHolder> {

    public DisplaySelectedComponentsAdapter(@NonNull FirestoreRecyclerOptions<SelectedComponent> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull SelectedComponent model) {
        holder.productName.setText(model.getProductname());
        Glide.with(holder.productImage.getContext())
                .load(model.getImage())
                .into(holder.productImage);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_required_components_single_view,parent,false);
        return new DisplaySelectedComponentsAdapter.MyViewHolder(V);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        ImageView productImage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage =  itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
        }
    }
}
