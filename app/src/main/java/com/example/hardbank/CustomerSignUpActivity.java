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
import java.util.regex.Pattern;

public class CustomerSignUpActivity extends AppCompatActivity {

    RelativeLayout backNav;
    CheckBox termsCheckBox;

    EditText editTextEmail, editTextPassword, editTextName;
    Button signUpBtn;

    FirebaseFirestore db;
    String userId;
    private FirebaseAuth mAuth;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{4,}" +               //at least 4 characters
                    "$");

    //private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_sign_up);

        //Firebase Auth initializing instance
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        //Initialization
        editTextEmail = (EditText) findViewById(R.id.signUpEmailEditText);
        editTextPassword = (EditText) findViewById(R.id.signUpPasswordEditText);
        editTextName = (EditText)findViewById(R.id.signUpNameEditText);
        signUpBtn  = (Button) findViewById(R.id.signUpButton);
        backNav  =  (RelativeLayout)findViewById(R.id.signUpActivityNavLayout);
        termsCheckBox =  (CheckBox)findViewById(R.id.termsCheckBox);

        //SignUp Button listener which will invoke register user function
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
            editTextPassword.setError("Minimum length of password should be 8");
            editTextPassword.requestFocus();
            return;
        }
        if (!PASSWORD_PATTERN.matcher(password).matches() ){
            editTextPassword.setError("Password Must Contain one digit from 0-9, one lowercase character, one uppercase character, one special symbols [@#$%] ");
            editTextPassword.requestFocus();
            return;
        }


        //progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    finish();
                    Toast.makeText(getApplicationContext(),"Registered",Toast.LENGTH_SHORT).show();
                    userId  = mAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = db.collection("users").document(userId);
                    Map<String,Object> user =  new HashMap<>();
                    user.put("fullname",name);
                    user.put("email",email);
                    user.put("type","customer");
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
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
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