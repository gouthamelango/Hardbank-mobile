package com.example.hardbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProductDetailsActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    TextView productName, productBrand, productPrice, productDescription;
    ImageView productImage;

    RelativeLayout addToCart, addToFavorite;

    RelativeLayout reviewsBtn, reviewsLayout;
    ImageView arrowReviewBtn;
    Boolean visibility = false;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        //Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        productName = findViewById(R.id.productName);
        productBrand = findViewById(R.id.productBrand);
        productPrice = findViewById(R.id.productPrice);
        productDescription = findViewById(R.id.productDescription);
        productImage  =  findViewById(R.id.productImage);

        Intent intent =  getIntent();
        if(intent.hasExtra("id")){
            id = getIntent().getExtras().getString("id");
            setUpRecyclerView(id);
        }

        //Review Btn
        reviewsBtn  = findViewById(R.id.reviewsBtn);
        reviewsLayout =  findViewById(R.id.reviewsLayout);
        arrowReviewBtn = findViewById(R.id.arrowReviewBtn);

        reviewsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!visibility){
                    visibility = true;
                    reviewsLayout.setVisibility(View.VISIBLE);
                    arrowReviewBtn.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                }
                else {
                    visibility = false;
                    reviewsLayout.setVisibility(View.GONE);
                    arrowReviewBtn.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                }
            }
        });

    }
    public void setUpRecyclerView(String productId){
        db.collection("products").document(productId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Product product = documentSnapshot.toObject(Product.class);

                productName.setText(product.getProductname());
                productBrand.setText(product.getProductbrand());
                productPrice.setText(product.getProductprice());
                productDescription.setText(product.getProductdescription());

                Glide.with(getApplicationContext())
                        .load(product.getImage())
                        .into(productImage);
            }
        });
    }
}