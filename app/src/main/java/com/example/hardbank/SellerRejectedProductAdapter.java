package com.example.hardbank;

import android.app.Activity;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

//
//public class SellerRejectedProductAdapter extends FirestoreRecyclerAdapter<Product, SellerRejectedProductAdapter.MyProductHolder> {
//
//    private OnDeleteClickListener listenerDelete;
//    private  OnRequestClickListener listenerRequest;
//
//
//    public SellerRejectedProductAdapter(@NonNull FirestoreRecyclerOptions<Product> options) {
//        super(options);
//    }
//
//    @Override
//    protected void onBindViewHolder(@NonNull MyProductHolder holder, int position, @NonNull Product model) {
//        holder.textViewProductName.setText(model.getProductname());
//        Glide.with(holder.imageViewProductImg.getContext())
//                .load(model.getImage())
//                .into(holder.imageViewProductImg);
//        holder.textViewRejectReason.setText(model.getReason());
//    }
//
//    @NonNull
//    @Override
//    public SellerRejectedProductAdapter.MyProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.rejected_product_single,parent,false);
//        return new SellerRejectedProductAdapter.MyProductHolder(V);
//    }
//
//
//    class MyProductHolder extends  RecyclerView.ViewHolder{
//        ImageView imageViewProductImg;
//        TextView textViewProductName;
//        TextView textViewRejectReason;
//        RelativeLayout deleteProductBtn;
//        RelativeLayout requestAgainBtn;
//
//        public MyProductHolder(@NonNull View itemView) {
//            super(itemView);
//            textViewProductName = itemView.findViewById(R.id.productNameTextView);
//            textViewRejectReason = itemView.findViewById(R.id.reasonTextView);
//            imageViewProductImg  = itemView.findViewById(R.id.productImage);
//            deleteProductBtn = itemView.findViewById(R.id.deleteProductBtn);
//            requestAgainBtn = itemView.findViewById(R.id.requestAgainBtn);
//
//            deleteProductBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    int position  = getAdapterPosition();
//                    if(position!= RecyclerView.NO_POSITION && listenerDelete != null){
//                        listenerDelete.onDeleteClick(getSnapshots().getSnapshot(position),position);
//                    }
//                }
//            });
//
//            requestAgainBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    int position  = getAdapterPosition();
//                    if(position!= RecyclerView.NO_POSITION && listenerRequest != null){
//                        listenerRequest.onRequestClick(getSnapshots().getSnapshot(position),position);
//                    }
//                }
//            });
//        }
//    }
//    //Delete
//    public  interface  OnDeleteClickListener{
//        void onDeleteClick(DocumentSnapshot documentSnapshot, int position);
//    }
//    public  void setOnDeleteClickListener(SellerRejectedProductAdapter.OnDeleteClickListener listener){
//        this.listenerDelete = listener;
//    }
//
//    //Request again
//    public  interface  OnRequestClickListener{
//        void onRequestClick(DocumentSnapshot documentSnapshot, int position);
//    }
//    public  void setOnRequestClickListener(SellerRejectedProductAdapter.OnRequestClickListener listener){
//        this.listenerRequest = listener;
//    }
//
//}

public class SellerRejectedProductAdapter extends RecyclerView.Adapter<SellerRejectedProductAdapter.MyViewHolder> {

    Context context;
    List<Product> list;

    public SellerRejectedProductAdapter(Context context,List<Product> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SellerRejectedProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view= LayoutInflater.from(parent.getContext()).inflate(R.layout.rejected_product_single,parent,false);
        return new SellerRejectedProductAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.textViewProductName.setText(list.get(position).getProductname());
        Glide.with(holder.imageViewProductImg.getContext())
                .load(list.get(position).getImage())
                .into(holder.imageViewProductImg);
        holder.textViewRejectReason.setText(list.get(position).getReason());

        //Request Again Button
        holder.requestAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final String id  = list.get(position).getId();
                AlertDialog alertDialog = new AlertDialog.Builder(view.getRootView().getContext()).create();
                alertDialog.setTitle("Request Again");
                alertDialog.setMessage("Are you sure you want to request for verification again?");
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
                                FirebaseFirestore.getInstance().collection("products").document(id).update("verified","false");
                                FirebaseFirestore.getInstance().collection("products").document(id).update("reason","none");

                                notifyItemRemoved(position);
                                list.remove(position);
                               // notifyDataSetChanged();
                            }
                        });
                alertDialog.show();
               // Toast.makeText(view.getContext(),"Clicked",Toast.LENGTH_SHORT).show();
            }
        });

        //Delete Product Button
        holder.deleteProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final String id  = list.get(position).getId();
                AlertDialog alertDialog = new AlertDialog.Builder(view.getRootView().getContext()).create();
                alertDialog.setTitle("Delete Product");
                alertDialog.setMessage("Are you sure you want to Delete?");
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
                                FirebaseFirestore.getInstance().collection("products").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        String image = documentSnapshot.getString("image");
                                        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(image);
                                        storageReference.delete();
                                        FirebaseFirestore.getInstance().collection("products").document(id).delete();
                                        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("products").document(id).delete();
                                        Toast.makeText(view.getContext(),"Product Deleted",Toast.LENGTH_SHORT).show();
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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProductImg;
        TextView textViewProductName;
        TextView textViewRejectReason;
        RelativeLayout deleteProductBtn;
        RelativeLayout requestAgainBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewProductName = itemView.findViewById(R.id.productNameTextView);
            textViewRejectReason = itemView.findViewById(R.id.reasonTextView);
            imageViewProductImg  = itemView.findViewById(R.id.productImage);
            deleteProductBtn = itemView.findViewById(R.id.deleteProductBtn);
            requestAgainBtn = itemView.findViewById(R.id.requestAgainBtn);
        }
    }

}
