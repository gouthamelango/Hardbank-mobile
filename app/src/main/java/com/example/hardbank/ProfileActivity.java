package com.example.hardbank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {

    RelativeLayout backNavBtn;
    Button logoutBtn;
    TextView accountHolderEmail,accountHolderName;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //initialization
        backNavBtn = (RelativeLayout)findViewById(R.id.profileActivityNavLayout);
        logoutBtn = (Button)findViewById(R.id.logoutButton);
        accountHolderEmail = (TextView)findViewById(R.id.accountHolderEmailTextProfileActivity);
        accountHolderName = (TextView)findViewById(R.id.accountHolderNameTextProfileActivity);

        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();



        //Displaying Data
        accountHolderEmail.setText(mAuth.getCurrentUser().getEmail());
        userId  = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String user_name = documentSnapshot.getString("fullname");
                Toast.makeText(getApplicationContext(),user_name,Toast.LENGTH_SHORT).show();
                accountHolderName.setText(user_name);
            }
        });
        //BackBtn Listener
        backNavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Logout Listener
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            }
        });


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.nothing_ani, R.anim.bottom_down);
    }
}