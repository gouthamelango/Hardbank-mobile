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
import com.google.firebase.firestore.DocumentSnapshot;

public class SellerRejectedProductAdapter extends FirestoreRecyclerAdapter<Product, SellerRejectedProductAdapter.MyProductHolder> {

    private OnDeleteClickListener listenerDelete;
    private  OnRequestClickListener listenerRequest;


    public SellerRejectedProductAdapter(@NonNull FirestoreRecyclerOptions<Product> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyProductHolder holder, int position, @NonNull Product model) {
        holder.textViewProductName.setText(model.getProductname());
        Glide.with(holder.imageViewProductImg.getContext())
                .load(model.getImage())
                .into(holder.imageViewProductImg);
        holder.textViewRejectReason.setText(model.getReason());
    }

    @NonNull
    @Override
    public SellerRejectedProductAdapter.MyProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.rejected_product_single,parent,false);
        return new SellerRejectedProductAdapter.MyProductHolder(V);
    }


    class MyProductHolder extends  RecyclerView.ViewHolder{
        ImageView imageViewProductImg;
        TextView textViewProductName;
        TextView textViewRejectReason;
        RelativeLayout deleteProductBtn;
        RelativeLayout requestAgainBtn;

        public MyProductHolder(@NonNull View itemView) {
            super(itemView);
            textViewProductName = itemView.findViewById(R.id.productNameTextView);
            textViewRejectReason = itemView.findViewById(R.id.reasonTextView);
            imageViewProductImg  = itemView.findViewById(R.id.productImage);
            deleteProductBtn = itemView.findViewById(R.id.deleteProductBtn);
            requestAgainBtn = itemView.findViewById(R.id.requestAgainBtn);

            deleteProductBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position  = getAdapterPosition();
                    if(position!= RecyclerView.NO_POSITION && listenerDelete != null){
                        listenerDelete.onDeleteClick(getSnapshots().getSnapshot(position),position);
                    }
                }
            });

            requestAgainBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position  = getAdapterPosition();
                    if(position!= RecyclerView.NO_POSITION && listenerRequest != null){
                        listenerRequest.onRequestClick(getSnapshots().getSnapshot(position),position);
                    }
                }
            });
        }
    }
    //Delete
    public  interface  OnDeleteClickListener{
        void onDeleteClick(DocumentSnapshot documentSnapshot, int position);
    }
    public  void setOnDeleteClickListener(SellerRejectedProductAdapter.OnDeleteClickListener listener){
        this.listenerDelete = listener;
    }

    //Request again
    public  interface  OnRequestClickListener{
        void onRequestClick(DocumentSnapshot documentSnapshot, int position);
    }
    public  void setOnRequestClickListener(SellerRejectedProductAdapter.OnRequestClickListener listener){
        this.listenerRequest = listener;
    }

}
