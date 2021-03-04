package com.example.hardbank;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewChatAdapter extends FirestoreRecyclerAdapter<ChatsModel,ViewChatAdapter.MyViewHolder> {



    public ViewChatAdapter(@NonNull FirestoreRecyclerOptions<ChatsModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final MyViewHolder holder, int position, @NonNull final ChatsModel model) {
        FirebaseFirestore.getInstance().collection("users").document(model.getCustomerid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                holder.textViewCustomerName.setText(documentSnapshot.getString("fullname"));
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(view.getContext(),ConsultantChatActivity.class);
                intent.putExtra("customerid",model.getCustomerid());
                view.getContext().startActivity(intent);
            }
        });

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_chat_single_view,parent,false);
        return new ViewChatAdapter.MyViewHolder(V);
    }

    class  MyViewHolder extends RecyclerView.ViewHolder{

        TextView textViewCustomerName;
        public MyViewHolder(View itemView){
            super(itemView);
            textViewCustomerName =  itemView.findViewById(R.id.textViewCustomerName);
        }
    }
}
