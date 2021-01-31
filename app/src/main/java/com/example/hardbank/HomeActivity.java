package com.example.hardbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {


    BottomNavigationView bottomNavigationView;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);




        //Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        //OnLoad Home Fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer,
                    new HomeFragment()).commit();
        }

    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer,
                                    selectedFragment).commit();
                            break;
                        case R.id.nav_account:
                            selectedFragment = new ChooseAccountFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer,
                                    selectedFragment).commit();
                            break;
                        case R.id.nav_cart:
                           // bottomNavigationView.setSelectedItemId(bottomNavigationView.getSelectedItemId());
                            Intent cartActivityIntent  =  new Intent(getApplicationContext(),CustomerCartActivity.class);
                            startActivity(cartActivityIntent);
                            overridePendingTransition(R.anim.bottom_up, R.anim.nothing_ani);
                            break;
                    }

                    return true;
                }
            };

}