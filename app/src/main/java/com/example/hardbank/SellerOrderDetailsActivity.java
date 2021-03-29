package com.example.hardbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class SellerOrderDetailsActivity extends AppCompatActivity {

    ImageView productImage;
    TextView textViewProductName, textViewProductPrice,textViewQuantity,textViewOrderId,textViewCustomerName,textViewOrderedDate ;
    TextView textViewDeliveryAddress, textViewPaymentMode,textViewTotalAmount,textViewOrderProgress;
    ImageView qrImage;
    TextView processOrderBtn;

    String orderID;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    String orderStatus;

    RelativeLayout billingDetails, cancelRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_order_details);

        productImage =  findViewById(R.id.productImage);

        textViewProductName =  findViewById(R.id.textViewProductName);
        textViewProductPrice =  findViewById(R.id.textViewProductPrice);
        textViewQuantity=  findViewById(R.id.textViewQuantity);
        textViewOrderId = findViewById(R.id.textViewOrderId);
        textViewCustomerName =  findViewById(R.id.textViewCustomerName);
        textViewOrderedDate =  findViewById(R.id.textViewOrderedDate);
        textViewDeliveryAddress =  findViewById(R.id.textViewDeliveryAddress);
        textViewPaymentMode = findViewById(R.id.textViewPaymentMode);
        textViewTotalAmount  =  findViewById(R.id.textViewTotalAmount);
        textViewOrderProgress =  findViewById(R.id.textViewOrderProgress);
        processOrderBtn = findViewById(R.id.processOrderBtn);

        billingDetails =  findViewById(R.id.billingDetails);
        cancelRequest =  findViewById(R.id.cancelRequest);

        qrImage = findViewById(R.id.qrImage);

        //FireBase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();



        Intent intent =  getIntent();
        if(intent.hasExtra("orderid")){
            orderID =  intent.getExtras().getString("orderid");

            textViewOrderId.setText(orderID);

            db.collection("orders").document(orderID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(final DocumentSnapshot documentSnapshot) {
                    Date date = documentSnapshot.getTimestamp("date").toDate();
                    textViewOrderedDate.setText(date.toString());

                    textViewQuantity.setText(documentSnapshot.getString("quantity"));

                    final int  quantity = Integer.parseInt(documentSnapshot.getString("quantity"));

                    textViewDeliveryAddress.setText(documentSnapshot.getString("deliveryaddress"));

                    textViewPaymentMode.setText(documentSnapshot.getString("type"));

                    textViewOrderProgress.setText(documentSnapshot.getString("status"));

                    orderStatus =  documentSnapshot.getString("status");

                    Glide.with(getApplicationContext()).load(documentSnapshot.getString("qr"))
                            .into(qrImage);

                    if(orderStatus.equals("Cancelled")){
                        billingDetails.setVisibility(View.GONE);
                    }
                    if(orderStatus.equals("Returned")){
                        billingDetails.setVisibility(View.GONE);
                        cancelRequest.setVisibility(View.VISIBLE);
                    }

                    db.collection("products").document(documentSnapshot.getString("productid"))
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Glide.with(getApplicationContext()).load(documentSnapshot.getString("image"))
                                    .into(productImage);

                            textViewProductName.setText(documentSnapshot.getString("productname"));

                            textViewProductPrice.setText(documentSnapshot.get("productprice").toString());

                            int price = Integer.parseInt(documentSnapshot.get("productprice").toString());

                            int total =  quantity*price;

                            textViewTotalAmount.setText(String.valueOf(total));

                        }
                    });

                    db.collection("users").document(documentSnapshot.getString("customerid"))
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            textViewCustomerName.setText(documentSnapshot.getString("fullname"));


                        }
                    });
                }
            });


            processOrderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(orderStatus.equals("Ordered")){
                        processOrder("Shipped");
                    }
                    else {
                        AlertDialog alertDialog = new AlertDialog.Builder(SellerOrderDetailsActivity.this).create();
                        alertDialog.setTitle("Order Progress");
                        alertDialog.setMessage("Order Already Shipped");
                        alertDialog.setCancelable(false);
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                        alertDialog.show();
                    }

                }
            });

            cancelRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog alertDialog = new AlertDialog.Builder(SellerOrderDetailsActivity.this).create();
                    alertDialog.setTitle("Cancel Return Request");
                    alertDialog.setMessage("Are you sure you want to cancel return Request?");
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
                                    Toast.makeText(getApplicationContext(), "Shipped", Toast.LENGTH_SHORT).show();
                                    db.collection("orders").document(orderID).update("status","Delivered").addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            onBackPressed();
                                        }
                                    });
                                }
                            });
                    alertDialog.show();
                }
            });
        }
    }

    public  void processOrder(final String process){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Proceed Order");
        alertDialog.setMessage("Are you sure you want to ship the order?");
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
                        Toast.makeText(getApplicationContext(), "Shipped", Toast.LENGTH_SHORT).show();
                        db.collection("orders").document(orderID).update("status",process).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                onBackPressed();
                            }
                        });
                    }
                });
        alertDialog.show();
    }
}