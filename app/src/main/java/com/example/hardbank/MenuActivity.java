package com.example.hardbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    private CollectionReference notebookRef;
    private  CollectionReference categoryCollectionReference;

    private CategoryAdapter adapter;
    RecyclerView recyclerView;

    List<String> categoryID = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        //Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        //Getting my products RecyclerView
        recyclerView = findViewById(R.id.categoryRecyclerView);

        //Reference to user collection
        notebookRef = db.collection("category");

        setUpRecyclerView();
    }
    private void setUpRecyclerView() {

        //To get all the products sold by that particular seller
        categoryCollectionReference = db.collection("category");
        categoryCollectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        categoryID.add(document.getId());
                    }
                    Query query = notebookRef.whereIn("catname",categoryID);
                    FirestoreRecyclerOptions<Category> options = new FirestoreRecyclerOptions.Builder<Category>()
                            .setQuery(query, Category.class)
                            .build();
                    adapter = new CategoryAdapter(options);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                            String cat =  documentSnapshot.getId();
                            Intent intent = new Intent(getApplicationContext(),ProductListingActivity.class);
                            intent.putExtra("type",cat);
                            startActivity(intent);


                        }
                    });

                    adapter.startListening();
                }
            }
        });
    }
}