package com.example.hardbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CustomerCartActivity extends AppCompatActivity {

    RelativeLayout backNav;
    Button continueShopping;
    RelativeLayout ifCartIsEmptyLayout,notEmptyLayout;
    Context context;
    List<Product> products =new ArrayList<>();
    private CartAdapter adapter;
    RecyclerView recyclerView;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_cart);

        //Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        //Back Nav
        backNav = findViewById(R.id.CartActivityBackNav);
        backNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //ContinueShopping
        continueShopping =  findViewById(R.id.continueShoppingFromCart);
        continueShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        recyclerView =  findViewById(R.id.productsRecyclerView);
//        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
//        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        //If Empty
        ifCartIsEmptyLayout =  findViewById(R.id.ifCartIsEmptyLayout);
        notEmptyLayout =  findViewById(R.id.notEmptyLayout);
       if(mAuth.getCurrentUser().getUid()!=null){
           db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("cart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
               @Override
               public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        QuerySnapshot queryDocumentSnapshots = task.getResult();
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        if(list.isEmpty()){
                            ifCartIsEmptyLayout.setVisibility(View.VISIBLE);
                            notEmptyLayout.setVisibility(View.GONE);
                        }
                        else {
                            for (int i = 0; i < list.size(); i++) {
                                DocumentSnapshot doc=list.get(i);
                                db.collection("products").document(doc.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        String productname = documentSnapshot.getString("productname");
                                        int productprice = documentSnapshot.getLong("productprice").intValue();
                                        //Toast.makeText(getApplicationContext(),productname,Toast.LENGTH_SHORT).show();
                                        String category = documentSnapshot.getString("category");
                                        String id = documentSnapshot.getString("id");
                                        String image = documentSnapshot.getString("image");
                                        String productbrand = documentSnapshot.getString("productbrand");
                                        String productdeliveryprice = documentSnapshot.getString("productdeliveryprice");
                                        String productdescription = documentSnapshot.getString("productdescription");
                                        String  reason = documentSnapshot.getString("reason");
                                        String verified = documentSnapshot.getString("verified");
                                        Product product = new Product(productname, productprice,category,id, image,productbrand,
                                                productdeliveryprice, productdescription, verified, reason);
                                        products.add(product);
                                        adapter = new CartAdapter(context,products);
                                        //Toast.makeText(getApplicationContext(),productname,Toast.LENGTH_SHORT).show();
                                        recyclerView.setAdapter(adapter);
                                    }
                                });
                            }
                        }
                    }
               }
           });
       }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.nothing_ani, R.anim.bottom_down);
    }
}