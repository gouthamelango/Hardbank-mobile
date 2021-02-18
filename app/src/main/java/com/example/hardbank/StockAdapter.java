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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class StockAdapter extends FirestoreRecyclerAdapter<ProductStock,StockAdapter.MyProductStockHolder> {

    private StockAdapter.OnItemClickListener listener;

    public StockAdapter(@NonNull FirestoreRecyclerOptions<ProductStock> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final MyProductStockHolder holder, int position, @NonNull final ProductStock model) {
        holder.productNameTextView.setText(model.getProductname());
        holder.textViewStock.setText(model.getStock());

        FirebaseFirestore.getInstance().collection("products").document(model.getProductid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Glide.with(holder.productImage.getContext())
                        .load(documentSnapshot.getString("image"))
                        .into(holder.productImage);

                holder.textViewPrice.setText(documentSnapshot.getString("productprice"));
            }
        });
    }

    @NonNull
    @Override
    public StockAdapter.MyProductStockHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_product_seller_single_view,parent,false);
        return new StockAdapter.MyProductStockHolder(V);
    }

    class  MyProductStockHolder extends RecyclerView.ViewHolder{
        TextView productNameTextView;
        TextView textViewStock;
        ImageView productImage;
        TextView textViewPrice;
        public MyProductStockHolder(View itemView){
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            textViewStock = itemView.findViewById(R.id.textViewStock);
            productImage = itemView.findViewById(R.id.productImage);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position  = getAdapterPosition();
                    if(position!= RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);
                    }
                }
            });
        }
    }
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void  setOnItemClickListener(StockAdapter.OnItemClickListener listener){
        this.listener = listener;
    }
}
