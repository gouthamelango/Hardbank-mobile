package com.example.hardbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

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
    int GOOGLE_PAY_REQUEST_CODE = 123;


    Button payBtn;

    String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";

    RadioGroup g1;

    RadioButton r1,r2;

    List<String> productsInCart =new ArrayList<>();

    String deliveryAddress;

    FirebaseStorage storage;
    StorageReference storageReference;


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


        g1 = (RadioGroup)findViewById(R.id.paymentOptionsLayout);
        r1 = (RadioButton)findViewById(R.id.radioButtonCashOnDelivery);
        r2 = (RadioButton)findViewById(R.id.radioButtonGooglePay);

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();



        Intent intent =  getIntent();
        if(intent.hasExtra("cart")){

            productsInCart = getIntent().getStringArrayListExtra("productsInCart");
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

                        deliveryAddress = doc.getString("name")+"\n"+doc.getString("address")+"\n"+doc.getString("phone");
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
                  //  payUsingUpi("KUMARESH R", "8951898448@okbizaxis", "Order", "1");
                    if( g1.getCheckedRadioButtonId() == -1){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ConfirmOrderActivity.this, "Please Choose the Options Available", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }
                    else {
                        int selectedId =  g1.getCheckedRadioButtonId();
                        switch (selectedId){
                            case R.id.radioButtonCashOnDelivery:
                                Toast.makeText(ConfirmOrderActivity.this, "Cash On Delivery", Toast.LENGTH_SHORT).show();
                                confirmOrder("cash");
                                break;
                            case  R.id.radioButtonGooglePay:
                                //Toast.makeText(ConfirmOrderActivity.this, "Google pay", Toast.LENGTH_SHORT).show();
                                payUsingUpi("KUMARESH R", "8951898448@okbizaxis", "Order", String.valueOf(totalAmount));
                                break;
                        }

                    }

                }
            });

        }
    }

    void payUsingUpi(  String name,String upiId, String note, String amount) {
        //Log.e("main ", "name "+name +"--up--"+upiId+"--"+ note+"--"+amount);
//        Uri uri = Uri.parse("upi://pay").buildUpon()
//                .appendQueryParameter("pa", upiId)
//                .appendQueryParameter("pn", name)
//                .appendQueryParameter("mc", "BCR2DN6T2OP3NEZG")
//                //.appendQueryParameter("tid", "02125412")
//                .appendQueryParameter("tr", "25584584")
//                .appendQueryParameter("tn", note)
//                .appendQueryParameter("am", amount)
//                .appendQueryParameter("cu", "INR")
//                //.appendQueryParameter("refUrl", "blueapp")
//                .build();
//        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
//        upiPayIntent.setData(uri);
//        // will always show a dialog to user to choose an app
//        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
//        // check if intent resolves
//        if(null != chooser.resolveActivity(getPackageManager())) {
//            startActivityForResult(chooser, UPI_PAYMENT);
//        } else {
//            Toast.makeText(ConfirmOrderActivity.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
//        }

        int digits = 8;
        int n = nDigitRandomNo(digits);
        String trRef =  String.valueOf(n);
        Uri uri =
                new Uri.Builder()
                        .scheme("upi")
                        .authority("pay")
                        .appendQueryParameter("pa", upiId)
                        .appendQueryParameter("pn", name)
                       .appendQueryParameter("mc", "BCR2DN6T2OP3NEZG")
                       .appendQueryParameter("tr", trRef)
                        .appendQueryParameter("tn", note)
                        .appendQueryParameter("am", amount)
                        .appendQueryParameter("cu", "INR")
                       // .appendQueryParameter("url", "frenchbakers.in")
                        .build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);
        startActivityForResult(intent,GOOGLE_PAY_REQUEST_CODE );
    }

    private int nDigitRandomNo(int digits){
        int max = (int) Math.pow(10,(digits)) - 1; //for digits =7, max will be 9999999
        int min = (int) Math.pow(10, digits-1); //for digits = 7, min will be 1000000
        int range = max-min; //This is 8999999
        Random r = new Random();
        int x = r.nextInt(range);// This will generate random integers in range 0 - 8999999
        int nDigitRandomNo = x+min; //Our random rumber will be any random number x + min
        return nDigitRandomNo;
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
            if(requestCode == GOOGLE_PAY_REQUEST_CODE){
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.e("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.e("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    //when user simply back without payment
                    Log.e("UPI", "onActivityResult: " + "Return data is null");
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
            }

        }

    }

    public  void updateAddress(String id){
        db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("addresses").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                textViewName.setText(documentSnapshot.getString("name"));
                textViewAddress.setText(documentSnapshot.getString("address"));
                textViewPhone.setText(documentSnapshot.getString("phone"));
                deliveryAddress = documentSnapshot.getString("name")+"\n"+documentSnapshot.getString("address")+"\n"+documentSnapshot.getString("phone");
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

    public  void confirmOrder(final String type){

        db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("cart")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot queryDocumentSnapshots = task.getResult();
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (int i = 0; i < list.size(); i++) {
                        final String orderID =  UUID.randomUUID().toString();

                        try {
                            Bitmap bitmap = encodeAsBitmap(orderID);

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();

                            final StorageReference ur_firebase_reference  = storageReference.child("images/QR/").child( UUID.randomUUID().toString()+ ".jpg");
                            UploadTask image_path = ur_firebase_reference.putBytes(data);


                            Task<Uri> urlTask = image_path.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }
                                    // Continue with the task to get the download URL
                                    return ur_firebase_reference.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = task.getResult();
                                        System.out.println("Upload " + downloadUri);
                                        // Toast.makeText(getApplicationContext(), "Successfully uploaded", Toast.LENGTH_SHORT).show();
                                        if (downloadUri != null) {

                                            String photoStringLink = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                                            //Toast.makeText(getApplicationContext(),photoStringLink,Toast.LENGTH_LONG).show();

                                           // changeDoc(photoStringLink);
                                            db.collection("orders").document(orderID).update("qr",photoStringLink);
                                        }

                                    } else {
                                        // Handle failures
                                        // ...
                                    }
                                }
                            });



                        } catch (WriterException e) {
                            e.printStackTrace();
                        }

                        DocumentSnapshot doc=list.get(i);
                      //  Toast.makeText(getApplicationContext(),"id"+doc.getId()+": "+doc.getString("quantity"),Toast.LENGTH_SHORT).show();
                        final String quantity = doc.getString("quantity");
                        final Map<String, Object> orderData = new HashMap<>();
                        final String productID  = doc.getId();
                        orderData.put("customerid",mAuth.getCurrentUser().getUid());
                        orderData.put("productid",doc.getId());
                        orderData.put("quantity",doc.getString("quantity"));
                        orderData.put("orderid",orderID);
                        orderData.put("deliveryaddress",deliveryAddress);
                        orderData.put("status","Ordered");
                        orderData.put("type",type);
                        db.collection("products").document(doc.getId()).collection("sellers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    QuerySnapshot queryDocumentSnapshots = task.getResult();
                                    List<DocumentSnapshot> list1 = queryDocumentSnapshots.getDocuments();
                                    DocumentSnapshot doc=list1.get(0);
                                    //Toast.makeText(getApplicationContext(),doc.getId(),Toast.LENGTH_SHORT).show();

                                    orderData.put("sellerid",doc.getId());
                                    orderData.put("date", FieldValue.serverTimestamp());
                                    final String sellerID = doc.getId();
                                    db.collection("users").document(doc.getId()).collection("products")
                                            .document(productID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            int  stock =  Integer.parseInt(documentSnapshot.getString("stock"));
                                            String newStock =  String.valueOf(stock-Integer.parseInt(quantity));
                                            db.collection("users").document(sellerID).collection("products")
                                                    .document(productID).update("stock",newStock);
                                        }
                                    });

                                    db.collection("orders").document(orderID).set(orderData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Map<String, Object> orderData1 = new HashMap<>();
                                            orderData1.put("orderid",orderID);
                                            db.collection("users").document(mAuth.getCurrentUser().getUid())
                                                    .collection("orders").document(orderID).set(orderData1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    db.collection("users").document(mAuth.getCurrentUser().getUid())
                                                        .collection("cart").document(productID).delete();
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        });

                    }


                    db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("projectscart")
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                QuerySnapshot queryDocumentSnapshots = task.getResult();
                                List<DocumentSnapshot> list1 = queryDocumentSnapshots.getDocuments();
                                for (int i = 0; i < list1.size(); i++){
                                    DocumentSnapshot doc=list1.get(i);
                                    final String  projectID =  doc.getId();

                                    db.collection("sampleprojects").document(doc.getId()).collection("components")
                                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){

                                                QuerySnapshot queryDocumentSnapshots = task.getResult();
                                                List<DocumentSnapshot> list2 = queryDocumentSnapshots.getDocuments();
                                                for (int i = 0; i < list2.size(); i++){
                                                    final String orderID =  UUID.randomUUID().toString();

                                                    //Start
                                                    try {
                                                        Bitmap bitmap = encodeAsBitmap(orderID);

                                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                                        byte[] data = baos.toByteArray();

                                                        final StorageReference ur_firebase_reference  = storageReference.child("images/QR/").child( UUID.randomUUID().toString()+ ".jpg");
                                                        UploadTask image_path = ur_firebase_reference.putBytes(data);


                                                        Task<Uri> urlTask = image_path.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                                            @Override
                                                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                                if (!task.isSuccessful()) {
                                                                    throw task.getException();
                                                                }
                                                                // Continue with the task to get the download URL
                                                                return ur_firebase_reference.getDownloadUrl();
                                                            }
                                                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Uri> task) {
                                                                if (task.isSuccessful()) {
                                                                    Uri downloadUri = task.getResult();
                                                                    System.out.println("Upload " + downloadUri);
                                                                    // Toast.makeText(getApplicationContext(), "Successfully uploaded", Toast.LENGTH_SHORT).show();
                                                                    if (downloadUri != null) {

                                                                        String photoStringLink = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                                                                        //Toast.makeText(getApplicationContext(),photoStringLink,Toast.LENGTH_LONG).show();

                                                                        // changeDoc(photoStringLink);
                                                                        db.collection("orders").document(orderID).update("qr",photoStringLink);
                                                                    }

                                                                } else {
                                                                    // Handle failures
                                                                    // ...
                                                                }
                                                            }
                                                        });



                                                    } catch (WriterException e) {
                                                        e.printStackTrace();
                                                    }
                                                    //end

                                                    DocumentSnapshot doc=list2.get(i);
                                                  //  Toast.makeText(getApplicationContext(),"id"+doc.getId(),Toast.LENGTH_SHORT).show();
                                                    final String productID  = doc.getId();
                                                    final Map<String, Object> orderData = new HashMap<>();
                                                    orderData.put("customerid",mAuth.getCurrentUser().getUid());
                                                    orderData.put("productid",doc.getId());
                                                    orderData.put("quantity","1");
                                                    orderData.put("orderid",orderID);
                                                    orderData.put("deliveryaddress",deliveryAddress);
                                                    orderData.put("status","Ordered");
                                                    orderData.put("type",type);
                                                    db.collection("products").document(doc.getId()).collection("sellers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if(task.isSuccessful()){
                                                                QuerySnapshot queryDocumentSnapshots = task.getResult();
                                                                List<DocumentSnapshot> list1 = queryDocumentSnapshots.getDocuments();
                                                                DocumentSnapshot doc=list1.get(0);
                                                                //Toast.makeText(getApplicationContext(),doc.getId(),Toast.LENGTH_SHORT).show();

                                                                orderData.put("sellerid",doc.getId());
                                                                orderData.put("date", FieldValue.serverTimestamp());

                                                                final String sellerID = doc.getId();
                                                                db.collection("users").document(doc.getId()).collection("products")
                                                                        .document(productID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                        int  stock =  Integer.parseInt(documentSnapshot.getString("stock"));
                                                                        String newStock =  String.valueOf(stock-Integer.parseInt("1"));
                                                                        db.collection("users").document(sellerID).collection("products")
                                                                                .document(productID).update("stock",newStock);
                                                                    }
                                                                });


                                                                db.collection("orders").document(orderID).set(orderData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Map<String, Object> orderData1 = new HashMap<>();
                                                                        orderData1.put("orderid",orderID);
                                                                        db.collection("users").document(mAuth.getCurrentUser().getUid())
                                                                                .collection("orders").document(orderID).set(orderData1);
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    });

                                                    if(i+1==list2.size()){
                                                       // Toast.makeText(getApplicationContext(),"Last",Toast.LENGTH_SHORT).show();
                                                        db.collection("users").document(mAuth.getCurrentUser().getUid())
                                                                .collection("projectscart").document(projectID).delete();
                                                    }
                                                    //End

                                                }
                                            }
                                        }
                                    });


                                }
                            }
                        }
                    });

                    Intent intent =  new Intent(getApplicationContext(),OrderPlacedActivity.class);
                    startActivity(intent);

                }
            }
        });
    }

    public Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        Bitmap bitmap=null;
        int WIDTH = 400;
        try
        {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);

            int w = result.getWidth();
            int h = result.getHeight();
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++) {
                int offset = y * w;
                for (int x = 0; x < w; x++) {
                    pixels[offset + x] = result.get(x, y) ? Color.BLACK: Color.WHITE;
                }
            }
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, WIDTH, 0, 0, w, h);
        } catch (Exception iae) {
            iae.printStackTrace();
            return null;
        }
        return bitmap;
    }
}