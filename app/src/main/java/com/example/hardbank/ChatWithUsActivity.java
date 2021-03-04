package com.example.hardbank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ChatWithUsActivity extends AppCompatActivity {

    Button chatBtn;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_with_us);


        //FireBase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();


        chatBtn =  findViewById(R.id.chatBtn);
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CustomerChatActivity.class);
                startActivity(intent);
            }
        });


        db.collection("chats").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                  //  Toast.makeText(getApplicationContext(),"Yes",Toast.LENGTH_SHORT).show();
                }
                else {
                   // Toast.makeText(getApplicationContext(),"No",Toast.LENGTH_SHORT).show();
                    final Map<String, Object> chatData= new HashMap<>();
                    chatData.put("customerid",mAuth.getCurrentUser().getUid());
                    chatData.put("consultantid","");
                    chatData.put("status","created");
                    db.collection("chats").document(mAuth.getCurrentUser().getUid()).set(chatData);

                }
            }
        });

    }
}