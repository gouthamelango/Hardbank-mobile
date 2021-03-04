package com.example.hardbank;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChatProfileAdapter extends FirestoreRecyclerAdapter<ChatsModel,ChatProfileAdapter.MyViewHolder> {


    public ChatProfileAdapter(@NonNull FirestoreRecyclerOptions<ChatsModel> options) {
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

        holder.acceptChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                AlertDialog alertDialog = new AlertDialog.Builder(view.getRootView().getContext()).create();
                alertDialog.setTitle("Accept Chat");
                alertDialog.setMessage("Are you sure you want to accept?");
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
                                FirebaseFirestore.getInstance().collection("chats")
                                        .document(model.getCustomerid())
                                        .update("consultantid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                FirebaseFirestore.getInstance().collection("chats")
                                        .document(model.getCustomerid())
                                        .update("status","opened");
                                Intent intent =  new Intent(view.getContext(),ConsultantChatActivity.class);
                                intent.putExtra("customerid",model.getCustomerid());
                                view.getContext().startActivity(intent);
                            }
                        });
                alertDialog.show();
            }
        });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_profile_single_view,parent,false);
        return new ChatProfileAdapter.MyViewHolder(V);
    }

    class  MyViewHolder extends RecyclerView.ViewHolder{

        TextView textViewCustomerName;
        RelativeLayout acceptChat;
        public MyViewHolder(View itemView){
            super(itemView);
            textViewCustomerName =  itemView.findViewById(R.id.textViewCustomerName);
            acceptChat =  itemView.findViewById(R.id.acceptChat);
        }
    }

}
