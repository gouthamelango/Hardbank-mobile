package com.example.hardbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class PasswordResetActivity extends AppCompatActivity {

    EditText editTextEmailPasswordReset;
    Button passwordResetBtn;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        editTextEmailPasswordReset  = findViewById(R.id.editTextEmailForPasswordReset);
        passwordResetBtn = findViewById(R.id.passwordResetBtn);

        mAuth = FirebaseAuth.getInstance();

        passwordResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String  email =  editTextEmailPasswordReset.getText().toString();
                if(email.isEmpty()){
                    editTextEmailPasswordReset.setError("Email is required");
                    editTextEmailPasswordReset.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTextEmailPasswordReset.setError("Please enter a valid email");
                    editTextEmailPasswordReset.requestFocus();
                    return;
                }
                sendRestLink(email);
            }
        });
    }
    public  void sendRestLink(String email){
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"Password Rest Link has been sent to registered mail address",Toast.LENGTH_SHORT).show();
                        }
                        else {

                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}