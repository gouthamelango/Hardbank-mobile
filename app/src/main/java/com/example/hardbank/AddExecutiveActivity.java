package com.example.hardbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class AddExecutiveActivity extends AppCompatActivity {

    Button addEmailBtn;
    EditText editTextEmail, editTextName, editTextPassword;
    
    String email;
    String name;
    String password;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    RecyclerView recyclerView;
    ConsultantsDisplayAdapter adapter;

    private FirebaseAuth mAuth2;

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
        setContentView(R.layout.activity_add_executive);
        
        editTextEmail =  findViewById(R.id.emailEditText);
        editTextName =  findViewById(R.id.nameEditText);
        editTextPassword =  findViewById(R.id.passwordEditText);

        recyclerView =  findViewById(R.id.executiveRecyclerView);
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager1);


        //Firebase Auth initializing instance
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setDatabaseUrl("https://hardbank-e46b3-default-rtdb.firebaseio.com")
                .setApiKey("AIzaSyBut2ZbaFgVUl3Q5MRdcVhiVLS9i1NuVPA")
                .setApplicationId("hardbank-e46b3").build();

        try { FirebaseApp myApp = FirebaseApp.initializeApp(getApplicationContext(), firebaseOptions, "AnyAppName");
            mAuth2 = FirebaseAuth.getInstance(myApp);
        } catch (IllegalStateException e){
            mAuth2 = FirebaseAuth.getInstance(FirebaseApp.getInstance("AnyAppName"));
        }


        addEmailBtn =  findViewById(R.id.addEmailBtn);
        addEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email =  editTextEmail.getText().toString();
                name =  editTextName.getText().toString();
                password =  editTextPassword.getText().toString();

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



                AlertDialog alertDialog = new AlertDialog.Builder(view.getRootView().getContext()).create();
                alertDialog.setTitle("Add Executive");
                alertDialog.setMessage("Are you sure you want to add ?");
                alertDialog.setCancelable(false);
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Toast.makeText(getApplicationContext(), email, Toast.LENGTH_SHORT).show();
                                createAccount();
                            }
                        });
                alertDialog.show();
            }
        });
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {

        Query query = db.collection("users").whereEqualTo("type","consultant");
        FirestoreRecyclerOptions<Consultant> options = new FirestoreRecyclerOptions.Builder<Consultant>()
                .setQuery(query, Consultant.class)
                .build();
        adapter = new ConsultantsDisplayAdapter(options);
        recyclerView.setAdapter(adapter);

       adapter.startListening();

    }

    private void createAccount() {
        mAuth2.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    String ex = task.getException().toString();
                    Toast.makeText(getApplicationContext(), "Registration Failed"+ex,
                            Toast.LENGTH_LONG).show();
                }
                else
                {
                    String id  = mAuth2.getCurrentUser().getUid();
                    DocumentReference documentReference = db.collection("users").document(id);
                    Map<String,Object> user =  new HashMap<>();
                    user.put("id",id);
                    user.put("fullname",name);
                    user.put("email",email);
                    user.put("type","consultant");

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
                    Toast.makeText(getApplicationContext(), "Registration successful",
                            Toast.LENGTH_SHORT).show();
                    editTextEmail.setText("");
                    editTextPassword.setText("");
                    editTextName.setText("");
                    editTextEmail.requestFocus();
                    mAuth2.signOut();
                }

            }
        });

    }
}