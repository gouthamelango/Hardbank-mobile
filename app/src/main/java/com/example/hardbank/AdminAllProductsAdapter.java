package com.example.hardbank;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminAllProductsAdapter extends FirestoreRecyclerAdapter<Product,AdminAllProductsAdapter.MyAllProductHolder> {

    public AdminAllProductsAdapter(@NonNull FirestoreRecyclerOptions<Product> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final MyAllProductHolder holder, int position, @NonNull Product model) {
        holder.productNameTextView.setText(model.getProductname());
        FirebaseFirestore.getInstance().collection("products").document(model.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Glide.with(holder.productImage.getContext())
                        .load(documentSnapshot.getString("image"))
                        .into(holder.productImage);

                holder.textViewPrice.setText(String.valueOf(documentSnapshot.getLong("productprice").intValue()));
            }
        });
    }

    @NonNull
    @Override
    public AdminAllProductsAdapter.MyAllProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_all_products_single_view,parent,false);
        return new AdminAllProductsAdapter.MyAllProductHolder(V);
    }

    class  MyAllProductHolder extends RecyclerView.ViewHolder{
        TextView productNameTextView;
        ImageView productImage;
        TextView textViewPrice;
        public MyAllProductHolder(View itemView){
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productImage = itemView.findViewById(R.id.productImage);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
        }
    }
}
