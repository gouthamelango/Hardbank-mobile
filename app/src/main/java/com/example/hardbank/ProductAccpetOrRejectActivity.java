package com.example.hardbank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProductAccpetOrRejectActivity extends AppCompatActivity {

    //Views
    TextView textViewProductNameNavBar,productName,productBrand,productDescription,productCategoryTextView,productDeliveryPrice,productPrice;
    ImageView  productImage;

    String productId;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    Button acceptBtn, rejectBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_accpet_or_reject);

        //FireBase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        //Initialization
        textViewProductNameNavBar = findViewById(R.id.textViewProductNameNavBar);
        productName = findViewById(R.id.productName);
        productBrand = findViewById(R.id.productBrand);
        productDescription = findViewById(R.id.productDescription);
        productCategoryTextView = findViewById(R.id.productCategoryTextView);
        productDeliveryPrice  = findViewById(R.id.productDeliveryPrice);
        productPrice = findViewById(R.id.productPrice);
        productImage = findViewById(R.id.productImage);

        //Button
        acceptBtn = findViewById(R.id.acceptBtn);
        rejectBtn = findViewById(R.id.rejectBtn);

        //Accept
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Accepted",Toast.LENGTH_SHORT).show();
            }
        });

        //Reject
        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Rejected",Toast.LENGTH_SHORT).show();
            }
        });

        Intent intent = getIntent();
        if(intent.hasExtra("id")){
            productId = getIntent().getExtras().getString("id");
        }

        db.collection("products").document(productId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Product product = documentSnapshot.toObject(Product.class);
                textViewProductNameNavBar.setText(product.getProductname());
                productName.setText(product.getProductname());
                productBrand.setText(product.getProductbrand());
                productDescription.setText(product.getProductdescription());
                productCategoryTextView.setText(product.getCategory());
                productDeliveryPrice.setText(product.getProductdeliveryprice());
                productPrice.setText(product.getProductprice());
                Glide.with(getApplicationContext())
                        .load(product.getImage())
                        .into(productImage);
            }
        });
    }
}