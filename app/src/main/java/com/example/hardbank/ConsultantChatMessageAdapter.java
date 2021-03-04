package com.example.hardbank;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;

public class ConsultantChatMessageAdapter  extends FirestoreRecyclerAdapter<ChatMessage,ConsultantChatMessageAdapter.MyViewHolder> {


    String customerID;
    public ConsultantChatMessageAdapter(@NonNull FirestoreRecyclerOptions<ChatMessage> options,String customerID) {
        super(options);
        this.customerID =  customerID;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull ChatMessage model) {
        if(!customerID.equals(model.senderid)){
            holder.textViewReceivedMessage.setVisibility(View.GONE);
            holder.textViewSentMessage.setVisibility(View.VISIBLE);
            holder.textViewSentMessage.setText(model.message);
        }
        else {
            holder.textViewSentMessage.setVisibility(View.GONE);
            holder.textViewReceivedMessage.setVisibility(View.VISIBLE);
            holder.textViewReceivedMessage.setText(model.message);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_single_view,parent,false);
        return new ConsultantChatMessageAdapter.MyViewHolder(V);
    }

    class  MyViewHolder extends RecyclerView.ViewHolder{

        TextView textViewReceivedMessage;
        TextView textViewSentMessage;
        public MyViewHolder(View itemView){
            super(itemView);
            textViewReceivedMessage =  itemView.findViewById(R.id.textViewReceivedMessage);
            textViewSentMessage =  itemView.findViewById(R.id.textViewSentMessage);
        }
    }
}
