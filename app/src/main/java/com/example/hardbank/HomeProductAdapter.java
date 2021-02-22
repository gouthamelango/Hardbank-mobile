package com.example.hardbank;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class HomeProductAdapter extends RecyclerView.Adapter<HomeProductAdapter.MyViewHolder> {

    Context context;
    List<Product> list;

    public HomeProductAdapter(Context context,List<Product> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view= LayoutInflater.from(parent.getContext()).inflate(R.layout.home_catgeory_product_single_view,parent,false);
        return new HomeProductAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.textViewProductName.setText(list.get(position).getProductname());
        holder.textViewProductBrand.setText(list.get(position).getProductbrand());
        Glide.with(holder.imageViewProductImg.getContext())
                .load(list.get(position).getImage())
                .into(holder.imageViewProductImg);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(view.getContext(),"Clicked",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(),ProductDetailsActivity.class);
                intent.putExtra("id",list.get(position).getId());
                view.getContext().startActivity(intent);
            }
        });
        holder.textViewProductPrice.setText(list.get(position).getProductprice());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public   class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageViewProductImg;
        TextView textViewProductName;
        TextView textViewProductBrand;
        TextView textViewProductPrice;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewProductName = itemView.findViewById(R.id.productName);
            textViewProductBrand = itemView.findViewById(R.id.productBrand);
            textViewProductPrice = itemView.findViewById(R.id.productPrice);

            imageViewProductImg  = itemView.findViewById(R.id.productImage);
        }
    }
}
