package com.example.hardbank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 2000;
    //ImageView imageView;
    RelativeLayout relativeLayout;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // imageView = findViewById(R.id.splashLogoImage);
        Animation animation= AnimationUtils.loadAnimation(MainActivity.this,R.anim.ani);
        //imageView.startAnimation(animation);
        relativeLayout = findViewById(R.id.logoContainerLayout);
        relativeLayout.startAnimation(animation);

        //FireBase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {


               if(mAuth.getCurrentUser()!=null){
                   String  userId  = mAuth.getCurrentUser().getUid();
                   db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                       @Override
                       public void onSuccess(DocumentSnapshot documentSnapshot) {
                           String reg_email = documentSnapshot.getString("type");
                           if(documentSnapshot.getString("type").equals("seller")){
                               Intent mainIntent = new Intent(MainActivity.this,SellerHome.class);
                               MainActivity.this.startActivity(mainIntent);
                               MainActivity.this.finish();
                           }
                           else {
                               Intent mainIntent = new Intent(MainActivity.this,HomeActivity.class);
                               MainActivity.this.startActivity(mainIntent);
                               MainActivity.this.finish();
                           }
                       }
                   });
               }
               else {
                   Intent mainIntent = new Intent(MainActivity.this,HomeActivity.class);
                   MainActivity.this.startActivity(mainIntent);
                   MainActivity.this.finish();
               }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}