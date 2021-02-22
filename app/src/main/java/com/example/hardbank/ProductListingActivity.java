package com.example.hardbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductListingActivity extends AppCompatActivity {

    TextView requestedProductTypeText;
    RecyclerView recyclerView;
    private HomeProductAdapter adapter;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Context context;
    List<Product> products =new ArrayList<>();

    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_listing);

        //Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        context  = getApplicationContext();


        //RecyclerView
        requestedProductTypeText  =  findViewById(R.id.requestedProductTypeText);
        recyclerView = findViewById(R.id.productsRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);

        Intent intent =  getIntent();
       if(intent.hasExtra("type")){
           type = getIntent().getExtras().getString("type");
           setUpRecyclerView(type);
           requestedProductTypeText.setText(type);
       }
    }
    private void setUpRecyclerView(final String type){
        db.collection("products").orderBy("productprice").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot queryDocumentSnapshots = task.getResult();
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (int i = 0; i < list.size(); i++) {
                        DocumentSnapshot doc=list.get(i);
                        if(doc.getString("verified").equals("true")){
                            if(doc.getString("category").equals(type)){
                                //Toast.makeText(getActivity().getApplicationContext(),doc.getString("productname"),Toast.LENGTH_SHORT).show();
                                String productname = doc.getString("productname");
                                int productprice = doc.getLong("productprice").intValue();
                                //Toast.makeText(getApplicationContext(),productname,Toast.LENGTH_SHORT).show();
                                String category = doc.getString("category");
                                String id = doc.getString("id");
                                String image = doc.getString("image");
                                String productbrand = doc.getString("productbrand");
                                String productdeliveryprice = doc.getString("productdeliveryprice");
                                String productdescription = doc.getString("productdescription");
                                String  reason = doc.getString("reason");
                                String verified = doc.getString("verified");
                                Product product = new Product(productname, productprice,category,id, image,productbrand,
                                        productdeliveryprice, productdescription, verified, reason);
                                products.add(product);
                                adapter = new HomeProductAdapter(context,products);
                                //Toast.makeText(getActivity().getApplicationContext(),product.getImage(),Toast.LENGTH_SHORT).show();
                                recyclerView.setAdapter(adapter);
                            }
                        }

                    }

                }
            }
        });
    }

}