package com.example.hardbank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.opengl.Visibility;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

public class CustomerChatActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    EditText editTextMessage;
    RelativeLayout sendMessageBtn;

    RecyclerView recyclerView;
    ChatMessageAdapter adapter;

    TextView infoText;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_chat);

        //FireBase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        infoText =  findViewById(R.id.infoText);
        db.collection("chats").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getString("status").equals("closed")){
                    infoText.setVisibility(View.VISIBLE);
                    infoText.setText("Your Conversation is closed!\nSend a message to start conversation again");
                }
            }
        });

        recyclerView =  findViewById(R.id.chatRecyclerView);
//        LinearLayoutManager manager = new LinearLayoutManager(this);
//        manager.setStackFromEnd(true);
//        manager.setReverseLayout(true);
//        recyclerView.setLayoutManager(manager);



        setUpRecyclerView();

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
                newMessageStatus();
                sendMessage(message);

            }
        });


    }

    private void setUpRecyclerView() {

        Query query = db.collection("chats").document(mAuth.getCurrentUser().getUid()).collection("messages").orderBy("time",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ChatMessage> options = new FirestoreRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class)
                .build();
        adapter = new ChatMessageAdapter(options);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter.startListening();
        scrollToBottom();
    }

    public  void sendMessage(String message){

        final Map<String, Object> chatData= new HashMap<>();
        chatData.put("senderid",mAuth.getCurrentUser().getUid());
        chatData.put("time", FieldValue.serverTimestamp());
        chatData.put("message",message);

        db.collection("chats").document(mAuth.getCurrentUser().getUid())
                .collection("messages")
                .document(UUID.randomUUID().toString()).set(chatData);

        editTextMessage.setText("");

    }

    public void newMessageStatus(){
        db.collection("chats").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getString("status").equals("created") || documentSnapshot.getString("status").equals("closed")){
                    db.collection("chats").document(mAuth.getCurrentUser().getUid()).update("status","requested");
                    infoText.setVisibility(View.GONE);
                }
            }
        });
    }

    public void scrollToBottom(){
        recyclerView.scrollToPosition(0);
    }

}