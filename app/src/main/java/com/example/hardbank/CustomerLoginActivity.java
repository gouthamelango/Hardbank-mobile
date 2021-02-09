package com.example.hardbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomerLoginActivity extends AppCompatActivity {

    RelativeLayout backNav;
    TextView signUpBtn, continueAsGuest, resetPasswordText;

    EditText editTextEmail, editTextPassword;
    Button loginBtn;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Pattern pattern;
    Matcher matcher;

    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\\\S+$).{4,}$";;

    int total = 0, x = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);

        //FireBase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        //BackBtn Navigation
        backNav  =  (RelativeLayout)findViewById(R.id.loginCustomerActivityBackNav);
        backNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Continue as Guest
        continueAsGuest = (TextView)findViewById(R.id.continueAsGuestText);
        continueAsGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeIntent =  new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(homeIntent);
            }
        });

        //Reset Password
        resetPasswordText = findViewById(R.id.resetPasswordText);
        resetPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  =  new Intent(getApplicationContext(),PasswordResetActivity.class);
                startActivity(intent);
            }
        });
        //Create An Account button
        signUpBtn  = findViewById(R.id.createNewText);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUpActivity =  new Intent(getApplicationContext(),CustomerSignUpActivity.class);
                startActivity(signUpActivity);
                //overridePendingTransition(R.anim.bottom_up, R.anim.nothing_ani);
            }
        });

        //Login Btn
        loginBtn = (Button) findViewById(R.id.loginButton);
        editTextEmail = (EditText) findViewById(R.id.loginEmailEditText);
        editTextPassword = (EditText) findViewById(R.id.loginPasswordEditText);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = editTextEmail.getText().toString().trim();
                final String password = editTextPassword.getText().toString().trim();
                total = 0; x = 0;
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

                if (password.length() < 6) {
                    editTextPassword.setError("Minimum length of password should be 6");
                    editTextPassword.requestFocus();
                    return;
                }

//                pattern = Pattern.compile(PASSWORD_PATTERN);
//                matcher = pattern.matcher(password);
//
//                if(!matcher.matches()){
//                    editTextPassword.setError("Password Must Contain one digit from 0-9, one lowercase character, one uppercase character, one special symbols [@#$%] ");
//                    editTextPassword.requestFocus();
//                    return;
//                }

                doSignIn(email,password);
            }
        });
    }

    public void doSignIn(String email,String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    String  userId  = mAuth.getCurrentUser().getUid();
                    db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String reg_email = documentSnapshot.getString("type");
                                if(documentSnapshot.getString("type").equals("customer")){
                                    finish();
                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(),"Signed In",Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(),"You're Not a Customer",Toast.LENGTH_SHORT).show();
                                    mAuth.signOut();
                                }
                        }
                    });

                } else {

                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.nothing_ani, R.anim.bottom_down);
    }
}