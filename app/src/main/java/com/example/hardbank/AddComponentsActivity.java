package com.example.hardbank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AddComponentsActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    private ProductCheckBoxAdapter adapter;
    RecyclerView recyclerView;
    ImageView saveBtn;
    String projectID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_components);

        //Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        if(intent.hasExtra("id")){
            projectID = intent.getExtras().getString("id");
          //  Toast.makeText(getApplicationContext(),projectID,Toast.LENGTH_SHORT).show();
        }

        //Getting my products RecyclerView
        recyclerView = findViewById(R.id.productsRecyclerView);
        setUpRecyclerView();

        saveBtn =  findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
    private void setUpRecyclerView(){
        Query query = db.collection("products").whereNotEqualTo("verified","false");
        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query, Product.class)
                .build();
        adapter = new ProductCheckBoxAdapter(options,projectID);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}