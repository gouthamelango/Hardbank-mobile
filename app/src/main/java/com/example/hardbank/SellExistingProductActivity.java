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

public class SellExistingProductActivity extends AppCompatActivity {


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_existing_product);

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
                    if(noOfStock.equals("0")){
                        productStockEditText.setError("Stock can't be zero");
                        productStockEditText.requestFocus();
                        return;
                    }

                    postProduct(noOfStock);
                }
            });


        }
    }

    public  void  postProduct(final String noOfStock){

       db.collection("products").document(productId).collection("sellers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
           @Override
           public void onComplete(@NonNull Task<QuerySnapshot> task) {
               if(task.isSuccessful()){
                   int flag=0;
                   for (QueryDocumentSnapshot document : task.getResult()) {
                       if(document.getId().equals(mAuth.getCurrentUser().getUid())){
                           flag = 1;
                       }
                   }
                   if(flag==1){
                       Toast.makeText(getApplicationContext(),"Sorry!. You're Already Posted this product",Toast.LENGTH_SHORT).show();
                   }
                   else{
                       db.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                           @Override
                           public void onSuccess(DocumentSnapshot documentSnapshot) {
                               String   shopName  = documentSnapshot.getString("shopname");

                               Map<String, Object> productData = new HashMap<>();
                               productData.put("id",mAuth.getCurrentUser().getUid());
                               productData.put("shopname",shopName);
                               db.collection("products").document(productId).collection("sellers").document(mAuth.getCurrentUser().getUid()).set(productData);

                               Map<String, Object> data = new HashMap<>();
                               data.put("category",category);
                               data.put("productname",productName);
                               data.put("productid",productId);
                               data.put("stock",noOfStock);
                               db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("products").document(productId).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                      if(task.isSuccessful()){
                                          Toast.makeText(getApplicationContext(),"Product Posted",Toast.LENGTH_SHORT).show();
                                          onBackPressed();
                                      }
                                   }
                               });
                           }
                       });


                   }
               }
           }
       });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}