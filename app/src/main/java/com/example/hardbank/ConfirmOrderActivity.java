package com.example.hardbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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
}