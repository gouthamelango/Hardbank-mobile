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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;
//
//public class MyPendingProductsSellerAdapter extends FirestoreRecyclerAdapter<Product,MyPendingProductsSellerAdapter.MyProductHolder> {
//
//
//    public MyPendingProductsSellerAdapter(@NonNull FirestoreRecyclerOptions<Product> options) {
//        super(options);
//    }
//    @Override
//    protected void onBindViewHolder(@NonNull MyProductHolder holder, int position, @NonNull Product model) {
//
//        holder.textViewProductName.setText(model.getProductname());
//        if(model.getVerified().equals("false")){
//            holder.textViewProductStatus.setText("Pending");
//        }
//        Glide.with(holder.imageViewProductImg.getContext())
//                .load(model.getImage())
//                .into(holder.imageViewProductImg);
//    }
//
//    @NonNull
//    @Override
//    public MyPendingProductsSellerAdapter.MyProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_myproduct_single_view,parent,false);
//        return new MyPendingProductsSellerAdapter.MyProductHolder(V);
//    }
//
//
//
//    class  MyProductHolder extends RecyclerView.ViewHolder{
//        ImageView imageViewProductImg;
//        TextView textViewProductName;
//        TextView textViewProductStatus;
//
//        public MyProductHolder(View itemView){
//            super(itemView);
//            textViewProductName = itemView.findViewById(R.id.productNameTextView);
//            textViewProductStatus = itemView.findViewById(R.id.productStatus);
//            imageViewProductImg  = itemView.findViewById(R.id.productImage);
//
//        }
//    }
//}


public class MyPendingProductsSellerAdapter extends RecyclerView.Adapter<MyPendingProductsSellerAdapter.MyViewHolder> {

    Context context;
    List<Product> list;

    public MyPendingProductsSellerAdapter(Context context,List<Product> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyPendingProductsSellerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view= LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_myproduct_single_view,parent,false);
        return new MyPendingProductsSellerAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.textViewProductName.setText(list.get(position).getProductname());
        if(list.get(position).getVerified().equals("false")){
            holder.textViewProductStatus.setText("Pending");
        }
        Glide.with(holder.imageViewProductImg.getContext())
                .load(list.get(position).getImage())
                .into(holder.imageViewProductImg);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProductImg;
          TextView textViewProductName;
        TextView textViewProductStatus;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewProductName = itemView.findViewById(R.id.productNameTextView);
            textViewProductStatus = itemView.findViewById(R.id.productStatus);
            imageViewProductImg  = itemView.findViewById(R.id.productImage);
        }
    }
}
