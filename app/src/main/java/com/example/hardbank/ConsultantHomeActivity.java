package com.example.hardbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ConsultantHomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultant_home);


        //Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation_consultant);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer,
                    new ConsultantHomeFragment()).commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {

                        case R.id.nav_chats:
                            selectedFragment = new ConsultantHomeFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer,
                                    selectedFragment).commit();
                            break;

                        case R.id.nav_account:
                            selectedFragment = new ConsultantAccountFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer,
                                    selectedFragment).commit();
                            break;
                    }

                    return true;
                }
            };


}