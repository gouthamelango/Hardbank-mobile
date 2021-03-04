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
import com.google.firebase.firestore.FirebaseFirestore;

public class ChatMessageAdapter extends FirestoreRecyclerAdapter<ChatMessage,ChatMessageAdapter.MyViewHolder> {

    public ChatMessageAdapter(@NonNull FirestoreRecyclerOptions<ChatMessage> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull ChatMessage model) {
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(model.senderid)){
            holder.textViewSentMessage.setVisibility(View.VISIBLE);
            holder.textViewSentMessage.setText(model.message);
        }
        else {
            holder.textViewReceivedMessage.setVisibility(View.VISIBLE);
            holder.textViewReceivedMessage.setText(model.message);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_single_view,parent,false);
        return new ChatMessageAdapter.MyViewHolder(V);
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
