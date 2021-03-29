package com.example.hardbank;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Date;

public class TrackOrderActivity extends AppCompatActivity {

    ImageView productImage;
    TextView textViewProductName, textViewProductPrice,textViewQuantity,textViewOrderedDate ;
    TextView textViewDeliveryAddress, textViewPaymentMode,textViewTotalAmount,textViewOrderProgress;

    RelativeLayout process2Circle, process2Bar, process2Image;
    RelativeLayout process3Circle, process3Bar, process3Image;

    RelativeLayout process1Layout,process2Layout,process3Layout,process4Layout,process5Layout;

    RelativeLayout estimatedDeliveryLayout;

    String orderID;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    String orderStatus;

    Button cancelBtn,returnBtn;

    ImageView scanBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);


        //FireBase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();


        productImage =  findViewById(R.id.productImage);

        textViewProductName =  findViewById(R.id.textViewProductName);
        textViewProductPrice =  findViewById(R.id.textViewProductPrice);
        textViewQuantity=  findViewById(R.id.textViewQuantity);
        textViewOrderedDate =  findViewById(R.id.textViewOrderedDate);
        textViewDeliveryAddress =  findViewById(R.id.textViewDeliveryAddress);
        textViewPaymentMode = findViewById(R.id.textViewPaymentMode);
        textViewTotalAmount  =  findViewById(R.id.textViewTotalAmount);
        textViewOrderProgress =  findViewById(R.id.textViewOrderProgress);


        process2Circle =  findViewById(R.id.process2Circle);
        process2Bar =  findViewById(R.id.process2Bar);
        process2Image =  findViewById(R.id.process2Image);

        process3Circle =  findViewById(R.id.process3Circle);
        process3Bar =  findViewById(R.id.process3Bar);
        process3Image   =  findViewById(R.id.process3Image);

        estimatedDeliveryLayout =  findViewById(R.id.estimatedDeliveryLayout);


        process1Layout =  findViewById(R.id.process1Layout);
        process2Layout =  findViewById(R.id.process2Layout);
        process3Layout =  findViewById(R.id.process3Layout);
        process4Layout =  findViewById(R.id.process4Layout);
        process5Layout =  findViewById(R.id.process5Layout);

        cancelBtn =  findViewById(R.id.cancelBtn);
        returnBtn =  findViewById(R.id.returnBtn);


        scanBtn = findViewById(R.id.scanBtn);

        Intent intent =  getIntent();
        if(intent.hasExtra("orderid")){

            orderID =  intent.getExtras().getString("orderid");
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

                    if(orderStatus.equals("Ordered")){
                        process2Bar.setBackgroundResource(R.drawable.bank_circle);
                        process2Circle.setBackgroundResource(R.drawable.bank_circle);
                        process2Image.setBackgroundResource(R.drawable.bank_circle);

                        process3Bar.setBackgroundResource(R.drawable.bank_circle);
                        process3Circle.setBackgroundResource(R.drawable.bank_circle);
                        process3Image.setBackgroundResource(R.drawable.bank_circle);

                        estimatedDeliveryLayout.setVisibility(View.VISIBLE);

                        cancelBtn.setVisibility(View.VISIBLE);
                    }
                    else if(orderStatus.equals("Shipped")){
                        process3Bar.setBackgroundResource(R.drawable.bank_circle);
                        process3Circle.setBackgroundResource(R.drawable.bank_circle);
                        process3Image.setBackgroundResource(R.drawable.bank_circle);
                        estimatedDeliveryLayout.setVisibility(View.VISIBLE);

                        cancelBtn.setVisibility(View.VISIBLE);

                        scanBtn.setVisibility(View.VISIBLE);

                    }
                    else  if(orderStatus.equals("Cancelled")){

                        process2Layout.setVisibility(View.GONE);
                        process3Layout.setVisibility(View.GONE);
                        process4Layout.setVisibility(View.VISIBLE);
                    }
                    else  if(orderStatus.equals("Delivered")){
                        returnBtn.setVisibility(View.VISIBLE);
                    }
                    else if(orderStatus.equals("Returned")){
                        process2Layout.setVisibility(View.GONE);
                        process3Layout.setVisibility(View.GONE);
                        process5Layout.setVisibility(View.VISIBLE);
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

                }
            });

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog alertDialog = new AlertDialog.Builder(TrackOrderActivity.this).create();
                    alertDialog.setTitle("Cancel Order");
                    alertDialog.setMessage("Are you sure you want to cancel the order?");
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    db.collection("orders").document(orderID).update("status","Cancelled")
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
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

            scanBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   // Toast.makeText(TrackOrderActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                    IntentIntegrator integrator = new IntentIntegrator(TrackOrderActivity.this);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                    integrator.setPrompt("Scan");
                    integrator.setCameraId(0);
                    integrator.setBeepEnabled(false);
                    integrator.setBarcodeImageEnabled(false);
                    integrator.initiateScan();
                }
            });

            returnBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog alertDialog = new AlertDialog.Builder(TrackOrderActivity.this).create();
                    alertDialog.setTitle("Return Order");
                    alertDialog.setMessage("Are you sure you want to Return the order?");
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    db.collection("orders").document(orderID).update("status","Returned")
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.e("Scan*******", "Cancelled scan");

            } else {
                Log.e("Scan", "Scanned");

                //tv_qr_readTxt.setText(result.getContents());
                //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                if(result.getContents().equals(orderID)){
                  db.collection("orders").document(orderID).update("status","Delivered").addOnSuccessListener(new OnSuccessListener<Void>() {
                      @Override
                      public void onSuccess(Void aVoid) {
                          Toast.makeText(getApplicationContext(), "Your Order Has been Delivered", Toast.LENGTH_LONG).show();
                          Intent intent = getIntent();
                          intent.putExtra("orderid",orderID);
                          finish();
                          startActivity(intent);
                      }
                  });
                }
                else {
                    Toast.makeText(this, "Wrong QR Code", Toast.LENGTH_LONG).show();
                }

            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



}