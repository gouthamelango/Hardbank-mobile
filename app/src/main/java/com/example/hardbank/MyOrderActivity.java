package com.example.hardbank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MyOrderActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    OrdersAdapter adapter;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);

        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        recyclerView =  findViewById(R.id.ordersRecyclerView);

        setUpRecyclerView();

    }
    private void setUpRecyclerView() {
        Query query =  db.collection("orders").whereEqualTo("customerid",mAuth.getCurrentUser().getUid()).orderBy("date",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<OrderModel> options = new FirestoreRecyclerOptions.Builder<OrderModel>()
                .setQuery(query, OrderModel.class)
                .build();
        adapter = new OrdersAdapter(options,"myorders");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);

        adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.stopListening();
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.stopListening();
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }
}