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

public class ExistingProductAdapter extends FirestoreRecyclerAdapter<Product,ExistingProductAdapter.MyProductHolder> {



    public ExistingProductAdapter(@NonNull FirestoreRecyclerOptions<Product> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyProductHolder holder, int position, @NonNull Product model) {
        holder.textViewProductName.setText(model.getProductname());
        Glide.with(holder.imageViewProductImg.getContext())
                .load(model.getImage())
                .into(holder.imageViewProductImg);
        holder.textViewPrice.setText(model.getProductprice());
        holder.textViewDescription.setText(model.getProductdescription());
    }

    @NonNull
    @Override
    public ExistingProductAdapter.MyProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.existing_product_single_view,parent,false);
        return new ExistingProductAdapter.MyProductHolder(V);
    }

    class  MyProductHolder extends RecyclerView.ViewHolder{
        ImageView imageViewProductImg;
        TextView textViewProductName;
        TextView textViewPrice;
        TextView textViewDescription;

        public MyProductHolder(View itemView){
            super(itemView);
            textViewProductName = itemView.findViewById(R.id.productNameTextView);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            imageViewProductImg  = itemView.findViewById(R.id.productImage);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
        }
    }

}
