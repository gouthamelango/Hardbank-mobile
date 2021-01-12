package com.example.hardbank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {

    RelativeLayout backNavBtn;
    Button logoutBtn;
    TextView accountHolderEmail;

    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //initialization
        backNavBtn = (RelativeLayout)findViewById(R.id.profileActivityNavLayout);
        logoutBtn = (Button)findViewById(R.id.logoutButton);
        accountHolderEmail = (TextView)findViewById(R.id.accountHolderEmail);
        mAuth = FirebaseAuth.getInstance();


        //Displaying Data
        accountHolderEmail.setText(mAuth.getCurrentUser().getEmail());

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