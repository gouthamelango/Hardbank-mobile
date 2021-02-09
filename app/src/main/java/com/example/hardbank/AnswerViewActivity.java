package com.example.hardbank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AnswerViewActivity extends AppCompatActivity {
    private String mParam1;
    private String mParam2;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    private CollectionReference notebookRef;
    private  CollectionReference productsCollectionReference;

    private AnswersAdapter adapter;
    RecyclerView recyclerView;

    String questionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_view);

        Intent intent = getIntent();
        if(intent.hasExtra("id")){
            questionId = getIntent().getExtras().getString("id");
        }

        //Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        //Getting my products RecyclerView
        recyclerView = findViewById(R.id.answersRecyclerView);

        //Reference to user collection
        notebookRef = db.collection("communitysupport").document(questionId).collection("answers");

        setUpRecyclerView();
    }
    private void setUpRecyclerView() {
        Query query = notebookRef.whereNotEqualTo("answer","none");
        FirestoreRecyclerOptions<Answers> options = new FirestoreRecyclerOptions.Builder<Answers>()
                .setQuery(query, Answers.class)
                .build();
        adapter = new AnswersAdapter(options);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

}