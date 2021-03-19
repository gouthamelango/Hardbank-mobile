package com.example.hardbank;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.Date;

public class OrdersAdapter extends FirestoreRecyclerAdapter<OrderModel,OrdersAdapter.MyViewHolder> {

    String activity;

    public OrdersAdapter(@NonNull FirestoreRecyclerOptions<OrderModel> options, String activity) {
        super(options);
        this.activity = activity;
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


        Date date = model.getDate().toDate();
        holder.orderedDate.setText(date.toString());

        if(model.getStatus().equals("Ordered")){
            holder.orderStatus.setTextColor(Color.parseColor("#F57F3B"));
        }

        if(model.getStatus().equals("Shipped")){
            holder.orderStatus.setTextColor(Color.parseColor("#F57F3B"));
        }

        if(model.getStatus().equals("Delivered")){
            holder.orderStatus.setTextColor(Color.parseColor("#2FB65B"));
        }

        if(activity.equals("sellerorderstab")){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(view.getContext(), "Clicked", Toast.LENGTH_SHORT).show();
                    Intent intent =  new Intent(view.getContext(),SellerOrderDetailsActivity.class);
                    intent.putExtra("orderid",model.getOrderid());
                    view.getContext().startActivity(intent);
                }
            });
        }
        if(activity.equals("myorders")){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(view.getContext(), "Clicked", Toast.LENGTH_SHORT).show();
                    Intent intent =  new Intent(view.getContext(),TrackOrderActivity.class);
                    intent.putExtra("orderid",model.getOrderid());
                    view.getContext().startActivity(intent);

                }
            });
        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V;
       if(activity.equals("myorders")){
            V = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_orders_single_view,parent,false);
       }
       else{
            V = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_order_single_view,parent,false);
       }
        return new OrdersAdapter.MyViewHolder(V);

    }

    class  MyViewHolder extends RecyclerView.ViewHolder{

        TextView productName;
        ImageView productImage;
        TextView orderStatus;
        TextView orderedDate;

        public MyViewHolder(View itemView){
            super(itemView);

            productName =  itemView.findViewById(R.id.productName);
            productImage =  itemView.findViewById(R.id.productImage);
            orderStatus  = itemView.findViewById(R.id.orderStatus);
            orderedDate =  itemView.findViewById(R.id.orderedDate);

        }
    }
}
