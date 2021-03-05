package com.example.hardbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddAddressActivity extends AppCompatActivity {

    Button saveAddressBtn;

    EditText nameEditText, phoneEditText,addressEditText;

    String name, phone, address;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    String editID;

    TextView addressesTitleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        //FireBase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText =  findViewById(R.id.phoneEditText);
        addressEditText =  findViewById(R.id.addressEditText);
        addressesTitleText =  findViewById(R.id.addressesTitleText);

        saveAddressBtn =  findViewById(R.id.saveAddressBtn);

        Intent intent =  getIntent();
        if(intent.hasExtra("editid")){
            editID =  intent.getExtras().getString("editid");

            saveAddressBtn.setText("Update Address");
            addressesTitleText.setText("Update Address");

            db.collection("users").document(mAuth.getCurrentUser().getUid())
                    .collection("addresses")
                    .document(editID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    addressEditText.setText(documentSnapshot.getString("address"));
                    phoneEditText.setText(documentSnapshot.getString("phone"));
                    nameEditText.setText(documentSnapshot.getString("name"));
                }
            });

            saveAddressBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    name =  nameEditText.getText().toString();
                    phone = phoneEditText.getText().toString();
                    address =  addressEditText.getText().toString();

                    if(name.isEmpty()){
                        nameEditText.setError("Cannot be empty");
                        nameEditText.requestFocus();
                        return;
                    }
                    if(name.length()<2){
                        nameEditText.setError("3 Characters are must");
                        nameEditText.requestFocus();
                        return;
                    }
                    if(phone.length() != 10){
                        phoneEditText.setError("Mobile number should be 10");
                        phoneEditText.requestFocus();
                        return;
                    }
                    if(!Patterns.PHONE.matcher(phone).matches()){
                        phoneEditText.setError("Enter Valid phone number");
                        phoneEditText.requestFocus();
                        return;
                    }

                    if(address.isEmpty()){
                        addressEditText.setError("Cannot be empty");
                        addressEditText.requestFocus();
                        return;
                    }

                    updateAddress(name, phone, address);
                }
            });


        }
        else {
            saveAddressBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    name =  nameEditText.getText().toString();
                    phone = phoneEditText.getText().toString();
                    address =  addressEditText.getText().toString();

                    if(name.isEmpty()){
                        nameEditText.setError("Cannot be empty");
                        nameEditText.requestFocus();
                        return;
                    }
                    if(name.length()<2){
                        nameEditText.setError("3 Characters are must");
                        nameEditText.requestFocus();
                        return;
                    }
                    if(phone.length() != 10){
                        phoneEditText.setError("Mobile number should be 10");
                        phoneEditText.requestFocus();
                        return;
                    }
                    if(!Patterns.PHONE.matcher(phone).matches()){
                        phoneEditText.setError("Enter Valid phone number");
                        phoneEditText.requestFocus();
                        return;
                    }

                    if(address.isEmpty()){
                        addressEditText.setError("Cannot be empty");
                        addressEditText.requestFocus();
                        return;
                    }

                    saveAddress(name, phone, address);
                }
            });
        }
    }

    public  void updateAddress(String name, String phone, String address){
        Map<String,Object> addressData =  new HashMap<>();
        addressData.put("name",name);
        addressData.put("phone",phone);
        addressData.put("address",address);

        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .collection("addresses")
                .document(editID).update(addressData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Address Updated",Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        });

    }
    public  void saveAddress(String name, String phone, String address){

        Map<String,Object> addressData =  new HashMap<>();
        addressData.put("name",name);
        addressData.put("phone",phone);
        addressData.put("address",address);


        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .collection("addresses")
                .document(UUID.randomUUID().toString()).set(addressData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Address Added",Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        });
    }
}