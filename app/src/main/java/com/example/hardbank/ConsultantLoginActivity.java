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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

public class ConsultantLoginActivity extends AppCompatActivity {

    TextView signUpBtn,resetPasswordTextSeller;

    Button loginButton;

    EditText editTextEmail, editTextPassword;

    String email, password;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultant_login);

        //FireBase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        loginButton =  findViewById(R.id.loginButton);
        editTextEmail =  findViewById(R.id.emailEditText);
        editTextPassword =  findViewById(R.id.passwordEditText);


        signUpBtn  = findViewById(R.id.signUpText);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUpIntent = new Intent(getApplicationContext(),ConsultantSignUpActivity.class);
                startActivity(signUpIntent);
            }
        });

        //Reset Password
        resetPasswordTextSeller = findViewById(R.id.resetPasswordText);
        resetPasswordTextSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  =  new Intent(getApplicationContext(),PasswordResetActivity.class);
                startActivity(intent);
            }
        });

        //Login Btn;
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email =  editTextEmail.getText().toString();
                password  = editTextPassword.getText().toString();

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
                if (!PASSWORD_PATTERN.matcher(password).matches() ){
                    editTextPassword.setError("Password Must Contain one digit from 0-9, one lowercase character, one uppercase character, one special symbols [@#$%] ");
                    editTextPassword.requestFocus();
                    return;
                }
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
                            if(documentSnapshot.getString("type").equals("consultant")){
                                finish();
                                Intent intent = new Intent(getApplicationContext(), ConsultantHomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(),"Signed In",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getApplicationContext(),"You're Not Our Service Executive",Toast.LENGTH_SHORT).show();
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
}