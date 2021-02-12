package com.example.hardbank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AskQuestionActivity extends AppCompatActivity {

    Button postBtn;
    EditText editTextQuestion;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question);


        //FireBase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        postBtn = findViewById(R.id.postQuestionBtn);
        editTextQuestion = findViewById(R.id.editTextQuestion);

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String question  =editTextQuestion.getText().toString().trim();
                if (question.isEmpty()) {
                   editTextQuestion.setError("Question Cannot be empty");
                    editTextQuestion.requestFocus();
                    return;
                }
                postQuestion(question);
            }
        });

    }
    public  void postQuestion(String question){
        Map<String, Object> questionData = new HashMap<>();
        questionData.put("question",question);
        questionData.put("userid",mAuth.getCurrentUser().getUid());
        questionData.put("time", FieldValue.serverTimestamp());
        db.collection("communitysupport").document(UUID.randomUUID().toString()).set(questionData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Question Posted",Toast.LENGTH_SHORT).show();
                Intent intent =  new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(intent);
            }
        });

    }
}