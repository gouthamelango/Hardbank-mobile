package com.example.hardbank;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SellerVerifyActivity extends AppCompatActivity {

    String sellerId;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    private CollectionReference notebookRef;

    RelativeLayout docLayout;
    ImageView docImage;

    Button verifyBtn, declineBtn;

    TextView textViewSellerNameNavBar, sellerNameTextView,sellerEmailTextView,shopNameTextView,shopAddressTextView,docTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_verify);

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

            verifyBtn = findViewById(R.id.verifyBtn);
            declineBtn = findViewById(R.id.declineBtn);


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

            verifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog alertDialog = new AlertDialog.Builder(SellerVerifyActivity.this).create();
                    alertDialog.setTitle("Verify Seller");
                    alertDialog.setMessage("Are you sure you want to verify this Seller?");
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    db.collection("users").document(sellerId).update("verified","true");
                                    Toast.makeText(getApplicationContext(),"Verified",Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                }
                            });
                    alertDialog.show();
                }
            });

            declineBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog alertDialog = new AlertDialog.Builder(SellerVerifyActivity.this).create();
                    alertDialog.setTitle("Verify Seller");
                    alertDialog.setMessage("Are you sure you want to Decline this Seller?");
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    db.collection("users").document(sellerId).update("comment","declined");
                                    Toast.makeText(getApplicationContext(),"Rejected",Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                }
                            });
                    alertDialog.show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}