package com.example.hardbank;

import android.content.Intent;
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

public class ProductSearchAdapter extends FirestoreRecyclerAdapter<Product,ProductSearchAdapter.MyViewHolder> {


    public ProductSearchAdapter(@NonNull FirestoreRecyclerOptions<Product> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull final Product model) {
        holder.textViewProductName.setText(model.getProductname());
        holder.textViewProductBrand.setText(model.getProductbrand());
        Glide.with(holder.imageViewProductImg.getContext())
                .load(model.getImage())
                .into(holder.imageViewProductImg);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(view.getContext(),"Clicked",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(),ProductDetailsActivity.class);
                intent.putExtra("id",model.getId());
                view.getContext().startActivity(intent);
            }
        });
        holder.textViewProductPrice.setText(model.getProductprice());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view= LayoutInflater.from(parent.getContext()).inflate(R.layout.home_catgeory_product_single_view,parent,false);
        return new ProductSearchAdapter.MyViewHolder(view);
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
