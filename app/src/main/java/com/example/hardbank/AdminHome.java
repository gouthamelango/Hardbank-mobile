package com.example.hardbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminHome extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        //Bottom Navigation
        bottomNavigationView = findViewById(R.id.admin_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        //OnLoad Home Fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.adminMainContainer,
                    new AdminDashBoardFragment()).commit();
        }

    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.nav_admin_dashboard:
                            selectedFragment = new AdminDashBoardFragment();
                            break;
                        case R.id.nav_admin_account:
                            selectedFragment = new AdminAccountFragment();
                            break;
                        case R.id.nav_admin_product:
                            selectedFragment = new AdminProductFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.adminMainContainer,
                            selectedFragment).commit();
                    return true;
                }
            };
}