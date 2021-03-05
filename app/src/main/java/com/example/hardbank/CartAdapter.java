package com.example.hardbank;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder>  {

    Context context;
    List<Product> list;

    public CartAdapter(Context context,List<Product> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_single_view,parent,false);
        return new CartAdapter.MyViewHolder(V);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.productNameTextView.setText(list.get(position).getProductname());
        holder.productPrice.setText(list.get(position).getProductprice());
        holder.productBrand.setText(list.get(position).getProductbrand());
        Glide.with(holder.productImage.getContext())
                .load(list.get(position).getImage())
                .into(holder.productImage);

        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                //Toast.makeText(view.getContext(),"Clicked",Toast.LENGTH_SHORT).show();
                final String id  = list.get(position).getId();
                AlertDialog alertDialog = new AlertDialog.Builder(view.getRootView().getContext()).create();
                alertDialog.setTitle("Remove Product");
                alertDialog.setMessage("Are you sure you want to Remove?");
                alertDialog.setCancelable(false);
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Toast.makeText(getActivity().getApplicationContext(),"Deleted",Toast.LENGTH_LONG).show();
                                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .collection("cart").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(view.getContext(),"Product Removed",Toast.LENGTH_SHORT).show();
                                        notifyItemRemoved(position);
                                        list.remove(position);
                                    }
                                });
                            }
                        });
                alertDialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder{

        TextView productNameTextView;
        ImageView productImage;
        TextView productPrice;
        TextView productBrand;
        TextView removeBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productName);
            productImage = itemView.findViewById(R.id.productImage);
            productPrice = itemView.findViewById(R.id.productPrice);
            productBrand = itemView.findViewById(R.id.productBrand);
            removeBtn  =  itemView.findViewById(R.id.removeBtn);
        }
    }
}
