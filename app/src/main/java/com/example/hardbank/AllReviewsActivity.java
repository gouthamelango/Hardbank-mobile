package com.example.hardbank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AllReviewsActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    AllReviewsAdapter adapter;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reviews);

        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        recyclerView =  findViewById(R.id.reviewRecyclerView);

        Intent intent =  getIntent();
        if(intent.hasExtra("id")){
            id = getIntent().getExtras().getString("id");
            setUpRecyclerView();
        }


    }
    private void setUpRecyclerView() {
        Query query =  db.collection("products").document(id).collection("reviews");
        FirestoreRecyclerOptions<ReviewModel> options = new FirestoreRecyclerOptions.Builder<ReviewModel>()
                .setQuery(query, ReviewModel.class)
                .build();
        adapter = new AllReviewsAdapter(options);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);

        adapter.startListening();
    }
}