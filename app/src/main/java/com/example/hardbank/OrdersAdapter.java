package com.example.hardbank;

import android.media.Image;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class OrdersAdapter extends FirestoreRecyclerAdapter<OrderModel,OrdersAdapter.MyViewHolder> {

    public OrdersAdapter(@NonNull FirestoreRecyclerOptions<OrderModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final MyViewHolder holder, int position, @NonNull final OrderModel model) {

        FirebaseFirestore.getInstance().collection("products").document(model.getProductid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                holder.productName.setText(documentSnapshot.getString("productname"));
                Glide.with(holder.productImage)
                        .load(documentSnapshot.getString("image"))
                        .into(holder.productImage);
            }
        });
        holder.orderStatus.setText(model.getStatus());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_orders_single_view,parent,false);
        return new OrdersAdapter.MyViewHolder(V);
    }

    class  MyViewHolder extends RecyclerView.ViewHolder{

        TextView productName;
        ImageView productImage;
        TextView orderStatus;

        public MyViewHolder(View itemView){
            super(itemView);

            productName =  itemView.findViewById(R.id.productName);
            productImage =  itemView.findViewById(R.id.productImage);
            orderStatus  = itemView.findViewById(R.id.orderStatus);

        }
    }
}
