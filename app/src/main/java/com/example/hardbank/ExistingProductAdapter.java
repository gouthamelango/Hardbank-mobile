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
import com.google.firebase.firestore.DocumentSnapshot;

public class ExistingProductAdapter extends FirestoreRecyclerAdapter<Product,ExistingProductAdapter.MyProductHolder> {

    private OnSellClickListener  listenerSell;

    public ExistingProductAdapter(@NonNull FirestoreRecyclerOptions<Product> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyProductHolder holder, int position, @NonNull Product model) {
        holder.textViewProductName.setText(model.getProductname());
        Glide.with(holder.imageViewProductImg.getContext())
                .load(model.getImage())
                .into(holder.imageViewProductImg);
        holder.textViewPrice.setText(model.getProductprice());
        holder.textViewDescription.setText(model.getProductdescription());
    }

    @NonNull
    @Override
    public ExistingProductAdapter.MyProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.existing_product_single_view,parent,false);
        return new ExistingProductAdapter.MyProductHolder(V);
    }

    class  MyProductHolder extends RecyclerView.ViewHolder{
        ImageView imageViewProductImg;
        TextView textViewProductName;
        TextView textViewPrice;
        TextView textViewDescription;
        RelativeLayout sellBtn;
        public MyProductHolder(View itemView){
            super(itemView);
            textViewProductName = itemView.findViewById(R.id.productNameTextView);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            imageViewProductImg  = itemView.findViewById(R.id.productImage);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            sellBtn = itemView.findViewById(R.id.sellBtn);

            sellBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position  = getAdapterPosition();
                    if(position!= RecyclerView.NO_POSITION && listenerSell != null){
                        listenerSell.onSellClick(getSnapshots().getSnapshot(position),position);
                    }
                }
            });
        }
    }

    //Sell
    public  interface  OnSellClickListener{
        void onSellClick(DocumentSnapshot documentSnapshot, int position);
    }
    public  void setOnSellClickListener(ExistingProductAdapter.OnSellClickListener listener){
        this.listenerSell = listener;
    }

}
