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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class StockAdapter extends RecyclerView.Adapter<StockAdapter.MyViewHolder> {

    Context context;
    List<ProductStock> list;

    public StockAdapter(Context context,List<ProductStock> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_product_seller_single_view,parent,false);
        return new StockAdapter.MyViewHolder(V);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.productNameTextView.setText(list.get(position).getProductname());
        holder.textViewStock.setText(list.get(position).getStock());
        FirebaseFirestore.getInstance().collection("products").document(list.get(position).getProductid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Glide.with(holder.productImage.getContext())
                        .load(documentSnapshot.getString("image"))
                        .into(holder.productImage);

                holder.textViewPrice.setText(String.valueOf(documentSnapshot.getLong("productprice").intValue()));
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(view.getContext(),"Clicked",Toast.LENGTH_SHORT).show();
                final String id  = list.get(position).getProductid();
                Intent intent =  new Intent(view.getContext(),UpdateStockActivity.class);
                intent.putExtra("id",id);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView textViewStock;
        ImageView productImage;
        TextView textViewPrice;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            textViewStock = itemView.findViewById(R.id.textViewStock);
            productImage = itemView.findViewById(R.id.productImage);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);

        }
    }
}


