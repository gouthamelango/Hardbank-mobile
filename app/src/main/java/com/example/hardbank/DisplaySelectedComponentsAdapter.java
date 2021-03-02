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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DisplaySelectedComponentsAdapter extends FirestoreRecyclerAdapter<SelectedComponent, DisplaySelectedComponentsAdapter.MyViewHolder> {

    public DisplaySelectedComponentsAdapter(@NonNull FirestoreRecyclerOptions<SelectedComponent> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final MyViewHolder holder, int position, @NonNull SelectedComponent model) {
        holder.productName.setText(model.getProductname());
        Glide.with(holder.productImage.getContext())
                .load(model.getImage())
                .into(holder.productImage);
        FirebaseFirestore.getInstance().collection("products").document(model.getProductid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                holder.productPrice.setText(documentSnapshot.get("productprice").toString());
            }
        });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_required_components_single_view,parent,false);
        return new DisplaySelectedComponentsAdapter.MyViewHolder(V);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        TextView productPrice;
        ImageView productImage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage =  itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
        }
    }
}
