package com.example.hardbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

public class ConfirmOrderActivity extends AppCompatActivity {

    int cartTotal= 0;
    int delivery = 0;
    int totalAmount = 0;

    TextView textViewCartTotal, textViewDeliveryAmount,textViewTotalAmount;

    TextView textViewName,textViewAddress,textViewPhone;

    TextView changeAddressBtn;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    static final int REQUEST_CODE = 0;


    String TAG ="main";
    final int UPI_PAYMENT = 1;

    Button payBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);


        textViewCartTotal = findViewById(R.id.textViewCartTotal);
        textViewDeliveryAmount =  findViewById(R.id.textViewDeliveryAmount);
        textViewTotalAmount =  findViewById(R.id.textViewTotalAmount);

        textViewName =  findViewById(R.id.textViewName);
        textViewAddress =  findViewById(R.id.textViewAddress);
        textViewPhone =  findViewById(R.id.textViewPhone);

        changeAddressBtn =  findViewById(R.id.changeAddressBtn);

        payBtn =  findViewById(R.id.payBtn);

        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        Intent intent =  getIntent();
        if(intent.hasExtra("cart")){
            cartTotal =  intent.getExtras().getInt("cart");
            delivery = intent.getExtras().getInt("delivery");
            totalAmount =  intent.getExtras().getInt("total");

            textViewCartTotal.setText(String.valueOf(cartTotal));
            textViewDeliveryAmount.setText(String.valueOf(delivery));
            textViewTotalAmount.setText(String.valueOf(totalAmount));

            db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("addresses").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        QuerySnapshot queryDocumentSnapshots = task.getResult();
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        DocumentSnapshot doc=list.get(0);
                        textViewName.setText(doc.getString("name"));
                        textViewAddress.setText(doc.getString("address"));
                        textViewPhone.setText(doc.getString("phone"));
                    }
                }
            });

            changeAddressBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent intent =  new Intent(getApplicationContext(),ChooseAddressActivity.class);
//                    startActivity(intent);
                    startActivityForResult(new Intent(ConfirmOrderActivity.this,ChooseAddressActivity.class), REQUEST_CODE);
                }
            });

            payBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  //  payUsingUpi("HardBank", "9514560507@paytm", "Order", "1");
                }
            });

        }
    }

    void payUsingUpi(  String name,String upiId, String note, String amount) {
        //Log.e("main ", "name "+name +"--up--"+upiId+"--"+ note+"--"+amount);
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                //.appendQueryParameter("mc", "")
                //.appendQueryParameter("tid", "02125412")
                //.appendQueryParameter("tr", "25584584")
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                //.appendQueryParameter("refUrl", "blueapp")
                .build();
        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);
        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
        // check if intent resolves
        if(null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(ConfirmOrderActivity.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
        }
    }


    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // A contact was picked.  Here we will just display it
                // to the user.
               // startActivity(new Intent(Intent.ACTION_VIEW, data));
                //Toast.makeText(getApplicationContext(),data.getExtras().getString("addressID"),Toast.LENGTH_SHORT).show();
                updateAddress(data.getExtras().getString("addressID"));
            }
//            if(requestCode == UPI_PAYMENT){
//                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
//                    if (data != null) {
//                        String trxt = data.getStringExtra("response");
//                        Log.e("UPI", "onActivityResult: " + trxt);
//                        ArrayList<String> dataList = new ArrayList<>();
//                        dataList.add(trxt);
//                        upiPaymentDataOperation(dataList);
//                    } else {
//                        Log.e("UPI", "onActivityResult: " + "Return data is null");
//                        ArrayList<String> dataList = new ArrayList<>();
//                        dataList.add("nothing");
//                        upiPaymentDataOperation(dataList);
//                    }
//                } else {
//                    //when user simply back without payment
//                    Log.e("UPI", "onActivityResult: " + "Return data is null");
//                    ArrayList<String> dataList = new ArrayList<>();
//                    dataList.add("nothing");
//                    upiPaymentDataOperation(dataList);
//                }
//            }

        }

    }

    public  void updateAddress(String id){
        db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("addresses").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                textViewName.setText(documentSnapshot.getString("name"));
                textViewAddress.setText(documentSnapshot.getString("address"));
                textViewPhone.setText(documentSnapshot.getString("phone"));
            }
        });
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(ConfirmOrderActivity.this)) {
            String str = data.get(0);
            Log.e("UPIPAY", "upiPaymentDataOperation: "+str);
            String paymentCancel = "";
            if(str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if(equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }
            if (status.equals("success")) {
                //Code to handle successful transaction here.
                Toast.makeText(ConfirmOrderActivity.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "payment successfull: "+approvalRefNo);
            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(ConfirmOrderActivity.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "Cancelled by user: "+approvalRefNo);
            }
            else {
                Toast.makeText(ConfirmOrderActivity.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "failed payment: "+approvalRefNo);
            }
        } else {
            Log.e("UPI", "Internet issue: ");
            Toast.makeText(ConfirmOrderActivity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }
}