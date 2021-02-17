package com.example.hardbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SellerSearchProducts extends AppCompatActivity {

    String searchText;
    TextView searchedForTextView;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    private CollectionReference notebookRef;
    private  CollectionReference productsCollectionReference;

    private ExistingProductAdapter adapter;
    RecyclerView recyclerView;

    String userID;

    ArrayList<String> keys = new ArrayList<String>();

    String sample =  "Sample";
    char[] cArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_search_products);

        searchedForTextView =  findViewById(R.id.searchedForTextView);

        //Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        //Getting my products RecyclerView
        recyclerView = findViewById(R.id.searchResultsSellerRecyclerView);

        //Reference to user collection
        notebookRef = db.collection("products");

        //TO get Current seller ID
        userID = mAuth.getCurrentUser().getUid();

        Intent intent = getIntent();
        if(intent.hasExtra("searchText")){
            searchText = intent.getExtras().getString("searchText");
            //Toast.makeText(getApplicationContext(),searchText,Toast.LENGTH_SHORT).show();
            searchedForTextView.setText(searchText);
//          cArray = sample.toCharArray();
           // Toast.makeText(getApplicationContext(),cArray.toString(),Toast.LENGTH_SHORT).show();


//            System.out.println("Array"+cArray);
//            for (int i = 1;i<=searchText.length();i++){
//                keys.add(String.valueOf(searchText.substring(0,i)));
//            }
//            System.out.println("KEY"+keys);

            setUpRecyclerView();
        }
    }


    private void setUpRecyclerView() {
        String lowerCase = searchText.toLowerCase();
        Query query = notebookRef.whereArrayContains("keys",lowerCase);
        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query, Product.class)
                .build();
        adapter = new ExistingProductAdapter(options);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }
}