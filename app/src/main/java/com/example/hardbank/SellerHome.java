package com.example.hardbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SellerHome extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);

        //Bottom Navigation
        bottomNavigationView = findViewById(R.id.seller_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

       //OnLoad Home Fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer,
                    new SellerDashBoardFragment()).commit();
        }
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.nav_seller_dashboard:
                            selectedFragment = new SellerDashBoardFragment();
                            break;
                        case R.id.nav_seller_account:
                            selectedFragment = new SellerAccountFragment();
                            break;
                        case R.id.nav_seller_product:
                            selectedFragment = new SellerProductFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer,
                            selectedFragment).commit();
                    return true;
                }
            };
}