package com.example.hardbank;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class SellerAdapter extends FirestoreRecyclerAdapter<Seller,SellerAdapter.MySellerHolder> {

    private SellerAdapter.OnItemClickListener listener;


    public SellerAdapter(@NonNull FirestoreRecyclerOptions<Seller> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MySellerHolder holder, int position, @NonNull Seller model) {
        holder.sellerNameTextView.setText(model.getFullname());
        holder.textViewShopName.setText(model.getShopname());
        holder.textViewAddress.setText(model.getAddress());
    }

    @NonNull
    @Override
    public SellerAdapter.MySellerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_verification_single_view,parent,false);
        return new SellerAdapter.MySellerHolder(V);
    }

    class  MySellerHolder extends RecyclerView.ViewHolder{
        TextView textViewShopName;
        TextView sellerNameTextView;
        TextView textViewAddress;

        public MySellerHolder(View itemView){
            super(itemView);
            sellerNameTextView = itemView.findViewById(R.id.sellerNameTextView);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            textViewShopName  = itemView.findViewById(R.id.textViewShopName);

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
    public void  setOnItemClickListener(SellerAdapter.OnItemClickListener listener){
        this.listener = listener;
    }
}
