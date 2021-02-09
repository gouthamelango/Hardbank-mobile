package com.example.hardbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SellerSignUpActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword, editTextName, editTextShopName;
    Button signUpBtn;
    CheckBox termsCheckBox;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String userId;

    TextView signInText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_sign_up);

        //Initialization
        editTextEmail = (EditText) findViewById(R.id.signUpEmailEditText);
        editTextPassword = (EditText) findViewById(R.id.signUpPasswordEditText);
        editTextName = (EditText)findViewById(R.id.signUpNameEditText);
        editTextShopName = (EditText)findViewById(R.id.signUpShopNameEditText);
        signUpBtn  = (Button) findViewById(R.id.sellerSignUpButton);
        termsCheckBox =  (CheckBox)findViewById(R.id.termsCheckBox);


        //Firebase Auth initializing instance
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        //sign In
        signInText  = findViewById(R.id.signInText);
        signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent signInActivityIntent  = new Intent(getApplicationContext(),SellerLoginActivity.class);
                startActivity(signInActivityIntent);
            }
        });

        //sign Up
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(termsCheckBox.isChecked()){
                    registerUser();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Please accept our Terms and Condition",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void registerUser() {
        //Storing Edit Text values in string variables
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final String name  =  editTextName.getText().toString().trim();
        final String shopName  =  editTextShopName.getText().toString().trim();

        //Validation
        if(name.isEmpty()){
            editTextName.setError("Name is required");
            editTextName.requestFocus();
            return;
        }
        if(name.length()<2){
            editTextName.setError("Name should be at least 2 Characters");
            editTextName.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }
        if(shopName.isEmpty()){
            editTextShopName.setError("ShopName is required");
            editTextShopName.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 8) {
            editTextPassword.setError("Minimum length of password should be 6");
            editTextPassword.requestFocus();
            return;
        }

        //progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    userId  = mAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = db.collection("users").document(userId);
                    Map<String,Object> user =  new HashMap<>();
                    user.put("id",userId);
                    user.put("fullname",name);
                    user.put("email",email);
                    user.put("type","seller");
                    user.put("shopname",shopName);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("TAG","OnSuccess");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TAG","OnFailure");
                        }
                    });
                    finish();
                    Intent intent = new Intent(getApplicationContext(), SellerHome.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(),"Registered",Toast.LENGTH_SHORT).show();
                } else {

                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }
}