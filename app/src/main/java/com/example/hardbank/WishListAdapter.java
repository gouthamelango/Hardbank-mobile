package com.example.hardbank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.MyViewHolder>  {

    Context context;
    List<Product> list;

    public WishListAdapter(Context context,List<Product> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_product_single_view,parent,false);
        return new WishListAdapter.MyViewHolder(V);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.productNameTextView.setText(list.get(position).getProductname());
        holder.productPrice.setText(list.get(position).getProductprice());
        Glide.with(holder.productImage.getContext())
                .load(list.get(position).getImage())
                .into(holder.productImage);
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"Del",Toast.LENGTH_SHORT).show();
            }
        });
        holder.cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"Cart",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public  class MyViewHolder extends RecyclerView.ViewHolder{

        TextView productNameTextView;
        ImageView productImage;
        TextView productPrice;

        RelativeLayout deleteBtn, cartBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productName);
            productImage = itemView.findViewById(R.id.productImage);
            productPrice = itemView.findViewById(R.id.productPrice);

            deleteBtn =  itemView.findViewById(R.id.delBtn);
            cartBtn = itemView.findViewById(R.id.cartBtn);
        }
    }
}

