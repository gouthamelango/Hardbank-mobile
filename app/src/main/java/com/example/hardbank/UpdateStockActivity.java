package com.example.hardbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class UpdateStockActivity extends AppCompatActivity {

    EditText productStockEditText;
    String productId;

    TextView productNameTextView, productBrandTextView,categoryTextView;
    TextView priceTextView,deliveryPriceTextView,descriptionTextView;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    ImageView productImage;

    String noOfStock;

    Button postBtn;

    String category;
    String productName;
    String stock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_stock);

        //FireBase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        //initialization
        productNameTextView = findViewById(R.id.productNameTextView);
        productBrandTextView = findViewById(R.id.productBrandTextView);
        categoryTextView = findViewById(R.id.categoryTextView);
        priceTextView = findViewById(R.id.priceTextView);
        deliveryPriceTextView =  findViewById(R.id.deliveryPriceTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        productImage = findViewById(R.id.productImage);


        productStockEditText = findViewById(R.id.productStockEditText);

        Intent intent = getIntent();
        if(intent.hasExtra("id")){
            productId = intent.getExtras().getString("id");

            db.collection("products").document(productId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Product product = documentSnapshot.toObject(Product.class);
                    productNameTextView.setText(product.getProductname());
                    productBrandTextView.setText(product.getProductbrand());
                    descriptionTextView.setText(product.getProductdescription());
                    categoryTextView.setText(product.getCategory());
                    deliveryPriceTextView.setText(product.getProductdeliveryprice());
                    priceTextView.setText(product.getProductprice());
                    category = product.getCategory();
                    productName = product.getProductname();
                    Glide.with(getApplicationContext())
                            .load(product.getImage())
                            .into(productImage);

                }
            });

            db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("products").document(productId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                   stock = documentSnapshot.getString("stock");
                    productStockEditText.setText(stock);
                }
            });

            postBtn = findViewById(R.id.postBtn);
            postBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    noOfStock = productStockEditText.getText().toString();
                    if(noOfStock.isEmpty()){
                        productStockEditText.setError("Stock is empty");
                        productStockEditText.requestFocus();
                        return;
                    }
                    if (noOfStock.equals(stock)){
                        productStockEditText.setError("Same as current stock");
                        productStockEditText.requestFocus();
                        return;
                    }

                    updateStock(noOfStock);
                }
            });


        }
    }
    public  void  updateStock(final String noOfStock){

        db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("products").document(productId).update("stock",noOfStock).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Current Stock is updated to "+ noOfStock,Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}