package com.example.hardbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Menu toolBarMenu;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        //initialization
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView =  findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolBar);

        //Config Custom ToolBar
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle("Home");
        toolbar.inflateMenu(R.menu.toolbar_menu);

        //Navigation Drawer Menu
        navigationView.bringToFront();
        ActionBarDrawerToggle  toggle =  new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        this.toolBarMenu =  menu;
        //Initialization of Home Fragment
        showHomeFragment();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }
    //Method for Displaying Home Fragment
    public void showHomeFragment(){
        FragmentTransaction homeFragmentTransaction  = getSupportFragmentManager().beginTransaction();
        HomeFragment homeFragment = new HomeFragment();
        homeFragmentTransaction.replace(R.id.mainContainer,homeFragment);
        homeFragmentTransaction.commit();
        getSupportActionBar().setTitle("Home");
        toolBarMenu.findItem(R.id.toolBar_cart).setVisible(true);
        toolBarMenu.findItem(R.id.toolBar_notification).setVisible(true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:

                //Displaying Home Fragment into the container
                showHomeFragment();
                break;
            case R.id.nav_account:

                //if the user is signed in --> Account DashBoard Fragment if not--->> Login Fragment
                FragmentTransaction loginFragmentTransaction  = getSupportFragmentManager().beginTransaction();
                LoginFragment loginFragment = new LoginFragment();
                loginFragmentTransaction.replace(R.id.mainContainer,loginFragment);
                loginFragmentTransaction.commit();
                toolBarMenu.findItem(R.id.toolBar_cart).setVisible(false);
                toolBarMenu.findItem(R.id.toolBar_notification).setVisible(false);
                getSupportActionBar().setTitle("Login");
                break;
            case R.id.nav_order:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}