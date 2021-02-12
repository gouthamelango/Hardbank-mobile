package com.example.hardbank;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SellerInformationActivity extends AppCompatActivity {

    String sellerId;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    private CollectionReference notebookRef;

    RelativeLayout docLayout;
    ImageView docImage;

    TextView textViewSellerNameNavBar, sellerNameTextView,sellerEmailTextView,shopNameTextView,shopAddressTextView,docTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_information);

        Intent intent = getIntent();
        if(intent.hasExtra("id")){
            sellerId = getIntent().getExtras().getString("id");
            //Toast.makeText(getApplicationContext(),sellerId,Toast.LENGTH_SHORT).show();

            //Firebase initialization
            mAuth = FirebaseAuth.getInstance();
            db =  FirebaseFirestore.getInstance();

            //initialization
            textViewSellerNameNavBar  = findViewById(R.id.textViewSellerNameNavBar);
            sellerNameTextView  = findViewById(R.id.sellerNameTextView);
            sellerEmailTextView  = findViewById(R.id.sellerEmailTextView);
            shopNameTextView  = findViewById(R.id.shopNameTextView);
            shopAddressTextView  = findViewById(R.id.shopAddressTextView);
            docTextView  = findViewById(R.id.docTextView);

            docLayout = findViewById(R.id.docLayout);
            docImage = findViewById(R.id.docImg);



            db.collection("users").document(sellerId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    textViewSellerNameNavBar.setText(documentSnapshot.getString("fullname"));
                    sellerNameTextView.setText(documentSnapshot.getString("fullname"));
                    sellerEmailTextView.setText(documentSnapshot.getString("email"));
                    shopNameTextView.setText(documentSnapshot.getString("shopname"));
                    shopAddressTextView.setText(documentSnapshot.getString("address"));

                    Glide.with(getApplicationContext())
                            .load(documentSnapshot.getString("doc"))
                            .into(docImage);
                }
            });

            docTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(docTextView.getText().toString().equals("View Document")){
                        docLayout.setVisibility(View.VISIBLE);
                        docTextView.setText("Close Document");
                    }
                    else if(docTextView.getText().toString().equals("Close Document")){
                        docLayout.setVisibility(View.GONE);
                        docTextView.setText("View Document");
                    }
                }
            });

        }
    }
}