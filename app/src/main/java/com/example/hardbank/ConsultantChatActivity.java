package com.example.hardbank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class ConsultantChatActivity extends AppCompatActivity {

    String customerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultant_chat);

        Intent intent =  getIntent();
        if(intent.hasExtra("customerid")){
            customerID =  intent.getExtras().getString("customerid");

            Toast.makeText(getApplicationContext(),customerID,Toast.LENGTH_SHORT).show();
        }

    }
}