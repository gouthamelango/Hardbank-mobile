package com.example.hardbank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConsultantChatActivity extends AppCompatActivity {

    String customerID;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    EditText editTextMessage;
    RelativeLayout sendMessageBtn;

    RecyclerView recyclerView;
    ConsultantChatMessageAdapter adapter;

    String message;

    TextView profileName;

    ImageView closeChat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultant_chat);

        //FireBase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        editTextMessage =  findViewById(R.id.editTextMessage);
        sendMessageBtn  = findViewById(R.id.sendMessageBtn);
        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                message = editTextMessage.getText().toString();
                if(message.isEmpty()){
                    editTextMessage.setError("Message Cannot be empty");
                    return;
                }
                sendMessage(message);

            }
        });

        closeChat =  findViewById(R.id.closeChat);
        closeChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(view.getRootView().getContext()).create();
                alertDialog.setTitle("Close Chat");
                alertDialog.setMessage("Are you sure you want to Close this chat?");
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
                                        .document(customerID)
                                        .update("consultantid", "");
                                FirebaseFirestore.getInstance().collection("chats")
                                        .document(customerID)
                                        .update("status","closed");
                                onBackPressed();
                            }
                        });
                alertDialog.show();
            }
        });


        recyclerView =  findViewById(R.id.chatRecyclerView);
        profileName =   findViewById(R.id.profileName);


        Intent intent =  getIntent();
        if(intent.hasExtra("customerid")){
            customerID =  intent.getExtras().getString("customerid");

           // Toast.makeText(getApplicationContext(),customerID,Toast.LENGTH_SHORT).show();

            db.collection("users").document(customerID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    profileName.setText(documentSnapshot.getString("fullname"));
                }
            });

            setUpRecyclerView();

        }

    }
    private void setUpRecyclerView() {

        Query query = db.collection("chats").document(customerID).collection("messages").orderBy("time",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ChatMessage> options = new FirestoreRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class)
                .build();
        adapter = new ConsultantChatMessageAdapter(options,customerID);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    public  void sendMessage(String message){

        final Map<String, Object> chatData= new HashMap<>();
        chatData.put("senderid",mAuth.getCurrentUser().getUid());
        chatData.put("time", FieldValue.serverTimestamp());
        chatData.put("message",message);

        db.collection("chats").document(customerID)
                .collection("messages")
                .document(UUID.randomUUID().toString()).set(chatData);

        editTextMessage.setText("");

    }
}

