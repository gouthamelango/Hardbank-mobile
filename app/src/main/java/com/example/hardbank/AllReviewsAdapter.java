package com.example.hardbank;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AllReviewsAdapter extends FirestoreRecyclerAdapter<ReviewModel,AllReviewsAdapter.MyViewHolder>  {


    public AllReviewsAdapter(@NonNull FirestoreRecyclerOptions<ReviewModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final MyViewHolder holder, int position, @NonNull ReviewModel model) {
        if(model.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            holder.customerNameTextView.setText("You");
        }
        else {
            FirebaseFirestore.getInstance().collection("users").document(model.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    holder.customerNameTextView.setText(documentSnapshot.getString("fullname"));
                }
            });
        }
        holder.textViewYourReview.setText(model.getReview());
        holder.ratingBar.setRating(Float.parseFloat(model.getRating()));
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_single_view,parent,false);
        return new AllReviewsAdapter.MyViewHolder(V);
    }

    class  MyViewHolder extends RecyclerView.ViewHolder {

        TextView customerNameTextView;
        TextView textViewYourReview;
        RatingBar ratingBar;

        public MyViewHolder(View itemView) {
            super(itemView);
            customerNameTextView = itemView.findViewById(R.id.customerNameTextView);
            textViewYourReview = itemView.findViewById(R.id.textViewYourReview);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }


}
