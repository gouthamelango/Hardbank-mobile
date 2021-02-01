package com.example.hardbank;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.model.DocumentSet;

public class ProductAdapter extends FirestoreRecyclerAdapter<Product,ProductAdapter.ProductHolder> {

    private  OnItemClickListener listener;
    private  OnRejectClickListener listenerReject;
    private  OnAcceptClickListener listenerAccept;



    public ProductAdapter(@NonNull FirestoreRecyclerOptions<Product> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductHolder holder, int position, @NonNull Product model) {
        holder.textViewProductName.setText(model.getProductname());
        holder.textViewProductPrice.setText(model.getProductprice());
        Glide.with(holder.imageViewProductImg.getContext())
                .load(model.getImage())
                .into(holder.imageViewProductImg);
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_to_accept,parent,false);
        return new ProductHolder(V);
    }

    class  ProductHolder extends RecyclerView.ViewHolder{
        TextView textViewProductName;
        TextView textViewProductPrice;
        ImageView imageViewProductImg;
        RelativeLayout relativeLayoutAccept;
        RelativeLayout relativeLayoutReject;

        public ProductHolder(View itemView){
            super(itemView);
            textViewProductName = itemView.findViewById(R.id.productNameTextView);
            textViewProductPrice = itemView.findViewById(R.id.productPriceTextView);
            imageViewProductImg  = itemView.findViewById(R.id.productImage);
            relativeLayoutAccept = itemView.findViewById(R.id.acceptProduct);
            relativeLayoutReject = itemView.findViewById(R.id.rejectProduct);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position  = getAdapterPosition();
                    if(position!= RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);
                    }
                }
            });

            relativeLayoutReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position  = getAdapterPosition();
                    if(position!= RecyclerView.NO_POSITION && listenerReject != null){
                        listenerReject.onRejectClick(getSnapshots().getSnapshot(position),position);
                    }
                }
            });
            relativeLayoutAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position  = getAdapterPosition();
                    if(position!= RecyclerView.NO_POSITION && listenerReject != null){
                        listenerAccept.onAcceptClick(getSnapshots().getSnapshot(position),position);
                    }
                }
            });
        }
    }

    //Whole Item
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void  setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    //Reject
    public  interface  OnRejectClickListener{
        void onRejectClick(DocumentSnapshot documentSnapshot, int position);
    }
    public  void setOnRejectClickListener(OnRejectClickListener listener){
        this.listenerReject = listener;
    }

    //Accept
    public  interface  OnAcceptClickListener{
        void onAcceptClick(DocumentSnapshot documentSnapshot, int position);
    }
    public  void setOnAcceptClickListener(OnAcceptClickListener listener){
        this.listenerAccept = listener;
    }
}
