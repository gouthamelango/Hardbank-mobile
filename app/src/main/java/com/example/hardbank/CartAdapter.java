package com.example.hardbank;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder>  {

    Context context;
    List<Product> list;
    private MyInterface listener;
    int flag =0;
    int f = 0;
    public CartAdapter(Context context,List<Product> list, MyInterface listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
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
                        String[] items = {"1","2","3","4","5","6","7","8","9","10"};
                        int checkedItem = Integer.parseInt( documentSnapshot.getString("quantity"))-1;
                        alertDialog.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
//                                        holder.estimatedPrice.setText(modifyPrice(holder.price,1));
//                                        holder.textViewQuantity.setText("1");
//                                        writeQuantity(1,position);
                                        stockValidation(1,position,holder);

                                        break;
                                    case 1:
//                                        if(){
//
//                                        }else {
//                                            holder.estimatedPrice.setText(modifyPrice(holder.price,2));
//                                            holder.textViewQuantity.setText("2");
//                                            writeQuantity(2,position);
//                                        }
                                        stockValidation(2,position,holder);

                                        break;
                                    case 2:
//                                        holder.estimatedPrice.setText(modifyPrice(holder.price,3));
//                                        holder.textViewQuantity.setText("3");
//                                        writeQuantity(3,position);
                                        stockValidation(3,position,holder);
                                        break;
                                    case 3:
//                                        holder.estimatedPrice.setText(modifyPrice(holder.price,4));
//                                        holder.textViewQuantity.setText("4");
//                                        writeQuantity(4,position);
                                        stockValidation(4,position,holder);
                                        break;
                                    case 4:
//                                        holder.estimatedPrice.setText(modifyPrice(holder.price,5));
//                                        holder.textViewQuantity.setText("5");
//                                        writeQuantity(5,position);
                                        stockValidation(5,position,holder);
                                        break;
                                    case 5:
//                                        holder.estimatedPrice.setText(modifyPrice(holder.price,6));
//                                        holder.textViewQuantity.setText("6");
//                                        writeQuantity(6,position);
                                        stockValidation(6,position,holder);
                                        break;
                                    case 6:
//                                        holder.estimatedPrice.setText(modifyPrice(holder.price,7));
//                                        holder.textViewQuantity.setText("7");
//                                        writeQuantity(7,position);
                                        stockValidation(7,position,holder);
                                        break;
                                    case 7:
//                                        holder.estimatedPrice.setText(modifyPrice(holder.price,8));
//                                        holder.textViewQuantity.setText("8");
//                                        writeQuantity(8,position);
//
                                        stockValidation(8,position,holder);
                                        break;
                                    case 8:
//                                        holder.estimatedPrice.setText(modifyPrice(holder.price,9));
//                                        holder.textViewQuantity.setText("9");
//                                        writeQuantity(9,position);
                                        stockValidation(9,position,holder);
                                        break;
                                    case 9:
//                                        holder.estimatedPrice.setText(modifyPrice(holder.price,10));
//                                        holder.textViewQuantity.setText("10");
//                                        writeQuantity(10,position);
                                        stockValidation(10,position,holder);
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
                                        listener.updatePrice();
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
    public  void writeQuantity(final int quantity, final int position){
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("cart").document(list.get(position).getId()).update("quantity",String.valueOf(quantity)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.updatePrice();
//                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                        .collection("cart").document(list.get(position).getId()).update("seller",seller);
            }
        });

    }

    public void stockValidation(final int q, final int position, final MyViewHolder holder ){

        //start
//        FirebaseFirestore.getInstance().collection("products").document(list.get(position).getId()).collection("sellers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful()){
//                    QuerySnapshot queryDocumentSnapshots = task.getResult();
//                    List<DocumentSnapshot> list4 = queryDocumentSnapshots.getDocuments();
//                    final DocumentSnapshot doc=list4.get(0);
//                    String sellerID =  doc.getString("id");
//                    //  Toast.makeText(holder.textViewQuantity.getContext(),sellerID,Toast.LENGTH_SHORT).show();
//
//                    FirebaseFirestore.getInstance().collection("users").document(sellerID).collection("products").document(list.get(position).getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                        @Override
//                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//                            if(Integer.parseInt(documentSnapshot.getString("stock")) < q){
//                                Toast.makeText(holder.textViewQuantity.getContext(),"Not in stock",Toast.LENGTH_SHORT).show();
//                            }
//                            else {
//                                holder.estimatedPrice.setText(modifyPrice(holder.price,q));
//                                holder.textViewQuantity.setText(String.valueOf(q));
//                                writeQuantity(q,position);
//                            }
//                            // Toast.makeText(holder.textViewQuantity.getContext(),documentSnapshot.getString("stock"),Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            }
//        });

        //end


        FirebaseFirestore.getInstance().collection("products").document(list.get(position).getId()).collection("sellers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    flag = 0;
                    QuerySnapshot queryDocumentSnapshots = task.getResult();
                    final List<DocumentSnapshot> list4 = queryDocumentSnapshots.getDocuments();
                    for (int i =0;i<list4.size();i++){
                        final DocumentSnapshot doc=list4.get(i);
                        final String sellerID =  doc.getString("id");
                        //  Toast.makeText(holder.textViewQuantity.getContext(),sellerID,Toast.LENGTH_SHORT).show();
                        f = 0;
                        FirebaseFirestore.getInstance().collection("users").document(sellerID).collection("products").document(list.get(position).getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(Integer.parseInt(documentSnapshot.getString("stock")) < q){
                                    //Toast.makeText(holder.textViewQuantity.getContext(),"Not in stock",Toast.LENGTH_SHORT).show();
                                    flag++;
                                    if(flag==list4.size()){
                                        Toast.makeText(holder.textViewQuantity.getContext(),"Not in stock",Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    f = 1;
                                    holder.estimatedPrice.setText(modifyPrice(holder.price,q));
                                    holder.textViewQuantity.setText(String.valueOf(q));
                                    writeQuantity(q,position);
                                  //  Toast.makeText(holder.textViewQuantity.getContext(),"1",Toast.LENGTH_SHORT).show();


                                    return;
                                }
                                // Toast.makeText(holder.textViewQuantity.getContext(),documentSnapshot.getString("stock"),Toast.LENGTH_SHORT).show();
                            }
                        });
                        if(f!=0){
                            break;
                        }
                    }

                }
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
