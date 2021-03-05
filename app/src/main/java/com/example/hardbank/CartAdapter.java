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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

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
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        holder.productNameTextView.setText(list.get(position).getProductname());
        holder.productPrice.setText(list.get(position).getProductprice());
        holder.productBrand.setText(list.get(position).getProductbrand());
        Glide.with(holder.productImage.getContext())
                .load(list.get(position).getImage())
                .into(holder.productImage);

        holder.price =  Integer.parseInt(list.get(position).getProductprice());

        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("cart").document(list.get(position).getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                holder.estimatedPrice.setText(modifyPrice(holder.price,Integer.parseInt(documentSnapshot.getString("quantity"))));
                holder.textViewQuantity.setText(documentSnapshot.getString("quantity"));
            }
        });


        holder.selectQuantityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {


                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .collection("cart").document(list.get(position).getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getRootView().getContext());
                        alertDialog.setTitle("Select Quantity");
                        String[] items = {"1","2","3","4","5"};
                        int checkedItem = Integer.parseInt( documentSnapshot.getString("quantity"))-1;
                        alertDialog.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        holder.estimatedPrice.setText(modifyPrice(holder.price,1));
                                        holder.textViewQuantity.setText("1");
                                        writeQuantity(1,position);
                                        break;
                                    case 1:
                                        holder.estimatedPrice.setText(modifyPrice(holder.price,2));
                                        holder.textViewQuantity.setText("2");
                                        writeQuantity(2,position);
                                        break;
                                    case 2:
                                        holder.estimatedPrice.setText(modifyPrice(holder.price,3));
                                        holder.textViewQuantity.setText("3");
                                        writeQuantity(3,position);
                                        break;
                                    case 3:
                                        holder.estimatedPrice.setText(modifyPrice(holder.price,4));
                                        holder.textViewQuantity.setText("4");
                                        writeQuantity(4,position);
                                        break;
                                    case 4:
                                        holder.estimatedPrice.setText(modifyPrice(holder.price,5));
                                        holder.textViewQuantity.setText("5");
                                        writeQuantity(5,position);
                                        break;
                                }
                            }
                        });
                        AlertDialog alert = alertDialog.create();
                        alert.show();
                      }
                });
            }
        });


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

    public String modifyPrice(int price, int quantity){

        return  String.valueOf(price*quantity);
    }
    public  void writeQuantity(int quantity, int position){
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("cart").document(list.get(position).getId()).update("quantity",String.valueOf(quantity));

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
        TextView estimatedPrice;
        TextView textViewQuantity;
        int price;

        RelativeLayout selectQuantityBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productName);
            productImage = itemView.findViewById(R.id.productImage);
            productPrice = itemView.findViewById(R.id.productPrice);
            productBrand = itemView.findViewById(R.id.productBrand);
            removeBtn  =  itemView.findViewById(R.id.removeBtn);
            estimatedPrice =  itemView.findViewById(R.id.estimatedPrice);
            selectQuantityBtn = itemView.findViewById(R.id.selectQuantityBtn);
            textViewQuantity =  itemView.findViewById(R.id.textViewQuantity);
        }
    }
}
