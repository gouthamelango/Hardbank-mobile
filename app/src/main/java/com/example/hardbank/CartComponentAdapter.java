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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CartComponentAdapter  extends RecyclerView.Adapter<CartComponentAdapter.MyViewHolder>  {


    Context context;
    List<SelectedComponent> list;

    public CartComponentAdapter(Context context,List<SelectedComponent> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_component_single_view,parent,false);
        return new CartComponentAdapter.MyViewHolder(V);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.productName.setText(list.get(position).getProductname());
       // holder.productPrice.setText(list.get(position).getProductprice());
        Glide.with(holder.productImage.getContext())
                .load(list.get(position).getImage())
                .into(holder.productImage);
        FirebaseFirestore.getInstance().collection("products").document(list.get(position).getProductid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                holder.productPrice.setText(documentSnapshot.get("productprice").toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder{

        TextView productName;
        ImageView productImage;
        TextView productPrice;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productImage = itemView.findViewById(R.id.productImage);
            productPrice = itemView.findViewById(R.id.productPrice);
        }
    }
}
