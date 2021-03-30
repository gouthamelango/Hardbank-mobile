package com.example.hardbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    TextView productName, productBrand, productPrice, productDescription;
    TextView addToCartText;
    ImageView productImage;
    TextView productStatus;

    RelativeLayout addToCart, addToFavorite;

    RelativeLayout reviewsBtn, reviewsLayout;
    ImageView arrowReviewBtn;
    Boolean visibility = false;

    Toolbar toolbar;
    Menu menu;
    String id;

    ImageView heartIcon;

    int flag = 0;

    RatingBar ratingBar;
    EditText reviewEditText;
    Float rating;
    String review;
    RelativeLayout reviewedLayout;
    TextInputLayout reviewEditTextLayout;
    TextView textViewYourReview;
    Button postReviewBtn;
    RelativeLayout viewReviewsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        //Toolbar
        toolbar =  findViewById(R.id.productlistingtoolbarLayout);
        toolbar.inflateMenu(R.menu.productlistingmenu);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Tapped",Toast.LENGTH_LONG).show();
            }
        });
        menu = toolbar.getMenu();





        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_search:
                        Toast.makeText(getApplicationContext(),"Search",Toast.LENGTH_LONG).show();
                        break;
                    case R.id.nav_wish_list:
                        //Toast.makeText(getApplicationContext(),"Fav",Toast.LENGTH_LONG).show();
                       if(mAuth.getCurrentUser()!=null){
                           Intent intent = new Intent(getApplicationContext(),WishListActivity.class);
                           startActivity(intent);
                       }
                       else {
                           Intent customerLoginActivity = new Intent(getApplicationContext(),CustomerLoginActivity.class);
                           startActivity(customerLoginActivity);
                           overridePendingTransition(R.anim.bottom_up, R.anim.nothing_ani);
                       }
                        break;
                    case R.id.nav_cart:
                        //Toast.makeText(getApplicationContext(),"Cart",Toast.LENGTH_LONG).show();
                       if(mAuth.getCurrentUser()!= null){
                           Intent cartActivityIntent = new Intent(getApplicationContext(), CustomerCartActivity.class);
                           startActivity(cartActivityIntent);
                           overridePendingTransition(R.anim.bottom_up, R.anim.nothing_ani);
                       }
                       else {
                           Intent customerLoginActivity = new Intent(getApplicationContext(),CustomerLoginActivity.class);
                           startActivity(customerLoginActivity);
                           overridePendingTransition(R.anim.bottom_up, R.anim.nothing_ani);
                       }
                        break;

                }
                return false;
            }
        });


        //Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        // Initialization
        productName = findViewById(R.id.productName);
        productBrand = findViewById(R.id.productBrand);
        productPrice = findViewById(R.id.productPrice);
        productDescription = findViewById(R.id.productDescription);
        productImage  =  findViewById(R.id.productImage);

        reviewEditText =  findViewById(R.id.reviewEditText);
        postReviewBtn =  findViewById(R.id.postReviewBtn);
        reviewEditTextLayout = findViewById(R.id.reviewEditTextLayout);
        reviewedLayout =  findViewById(R.id.reviewedLayout);
        textViewYourReview =  findViewById(R.id.textViewYourReview);
        ratingBar = findViewById(R.id.ratingBar);
        viewReviewsBtn =  findViewById(R.id.viewReviewsBtn);

        productStatus = findViewById(R.id.productStatus);

        addToCart =  findViewById(R.id.addToCartBtn);
        addToCartText = findViewById(R.id.addToCartText);

        //Intent
        Intent intent =  getIntent();
        if(intent.hasExtra("id")){
            id = getIntent().getExtras().getString("id");
            setUpRecyclerView(id);



        //Review Btn
        reviewsBtn  = findViewById(R.id.reviewsBtn);
        reviewsLayout =  findViewById(R.id.reviewsLayout);
        arrowReviewBtn = findViewById(R.id.arrowReviewBtn);

        reviewsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!visibility){
                    visibility = true;
                    reviewsLayout.setVisibility(View.VISIBLE);
                    arrowReviewBtn.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                }
                else {
                    visibility = false;
                    reviewsLayout.setVisibility(View.GONE);
                    arrowReviewBtn.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                }
            }
        });

        //Actions: Add to fav
        addToFavorite = findViewById(R.id.addToFavBtn);
        heartIcon =  findViewById(R.id.heartIcon);
        addToFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mAuth.getCurrentUser()!=null){

                    db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("wishlist").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                int flag = 0;
                                QuerySnapshot queryDocumentSnapshots = task.getResult();
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                for (int i = 0; i < list.size(); i++) {
                                    DocumentSnapshot doc=list.get(i);
                                    if(doc.getId().equals(id)){
                                        //Toast.makeText(getApplicationContext(),"Already",Toast.LENGTH_SHORT).show();
                                        db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("wishlist").document(doc.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getApplicationContext(),"Removed from WishList",Toast.LENGTH_SHORT).show();
                                                heartIcon.setImageResource(R.drawable.ic_baseline_favorite_border_black);
                                            }
                                        });
                                        flag=1;
                                        break;
                                    }
                                }
                                if(flag ==0){
                                    Map<String, Object> productData = new HashMap<>();
                                    productData.put("id",id);
                                    db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("wishlist").document(id).set(productData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(),"Added to WishList",Toast.LENGTH_SHORT).show();
                                            heartIcon.setImageResource(R.drawable.ic_baseline_favorite_24);
                                        }
                                    });
                                }
                            }
                        }
                    });

                }
                else {
                    Intent customerLoginActivity = new Intent(getApplicationContext(),CustomerLoginActivity.class);
                    startActivity(customerLoginActivity);
                    overridePendingTransition(R.anim.bottom_up, R.anim.nothing_ani);
                }
            }
        });

        //Actions: Add to Cart

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAuth.getCurrentUser()!=null){

                    db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("cart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                int flag = 0;
                                QuerySnapshot queryDocumentSnapshots = task.getResult();
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                for (int i = 0; i < list.size(); i++) {
                                    DocumentSnapshot doc=list.get(i);
                                    if(doc.getId().equals(id)){
                                        //Toast.makeText(getApplicationContext(),"Already",Toast.LENGTH_SHORT).show();
                                        db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("cart").document(doc.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getApplicationContext(),"Removed from Cart",Toast.LENGTH_SHORT).show();
                                                //heartIcon.setImageResource(R.drawable.ic_baseline_favorite_border_black);
                                                addToCartText.setText("Add to cart");
                                            }
                                        });
                                        flag=1;
                                        break;
                                    }
                                }
                                if(flag ==0){
                                    final Map<String, Object> productData = new HashMap<>();
                                    productData.put("id",id);
                                    productData.put("quantity","1");
                                    db.collection("products").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            productData.put("price",documentSnapshot.get("productprice").toString());
                                            db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("cart").document(id).set(productData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getApplicationContext(),"Added to Cart",Toast.LENGTH_SHORT).show();
                                                    //heartIcon.setImageResource(R.drawable.ic_baseline_favorite_24);
                                                    addToCartText.setText("Remove from cart");
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        }
                    });

                }
                else {
                    Intent customerLoginActivity = new Intent(getApplicationContext(),CustomerLoginActivity.class);
                    startActivity(customerLoginActivity);
                    overridePendingTransition(R.anim.bottom_up, R.anim.nothing_ani);
                }
            }
        });


            ratingBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                    rating = ratingBar.getRating();
                    //Toast.makeText(getApplicationContext(),String.valueOf(rating),Toast.LENGTH_SHORT).show();
                }
            });



            postReviewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mAuth.getCurrentUser()!=null){
                        review =  reviewEditText.getText().toString();

                        Map<String, Object> reviewData = new HashMap<>();
                        reviewData.put("id",mAuth.getCurrentUser().getUid());
                        reviewData.put("rating",rating);
                        reviewData.put("review",review);
                        db.collection("products").document(id)
                                .collection("reviews").document(mAuth.getCurrentUser().getUid())
                                .set(reviewData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    reviewEditText.setVisibility(View.GONE);
                                    reviewEditTextLayout.setVisibility(View.GONE);
                                    postReviewBtn.setVisibility(View.GONE);
                                    reviewedLayout.setVisibility(View.VISIBLE);
                                    textViewYourReview.setText(review);
                                }
                            }
                        });
                    }
                    else {
                        Intent customerLoginActivity = new Intent(getApplicationContext(),CustomerLoginActivity.class);
                        startActivity(customerLoginActivity);
                        overridePendingTransition(R.anim.bottom_up, R.anim.nothing_ani);
                    }
                }
            });

            viewReviewsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   if(mAuth.getCurrentUser()!=null){
                       Intent intent =  new Intent(getApplicationContext(),AllReviewsActivity.class);
                       intent.putExtra("id",id);
                       startActivity(intent);
                   }
                   else {
                       Intent customerLoginActivity = new Intent(getApplicationContext(),CustomerLoginActivity.class);
                       startActivity(customerLoginActivity);
                       overridePendingTransition(R.anim.bottom_up, R.anim.nothing_ani);
                   }
                }
            });
        }
    }


    public void setUpRecyclerView(final String productId) {
        db.collection("products").document(productId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Product product = documentSnapshot.toObject(Product.class);

                productName.setText(product.getProductname());
                productBrand.setText(product.getProductbrand());
                productPrice.setText(product.getProductprice());
                productDescription.setText(product.getProductdescription());

                Glide.with(getApplicationContext())
                        .load(product.getImage())
                        .into(productImage);
            }
        });

        if (mAuth.getCurrentUser() != null) {
            db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("wishlist").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot queryDocumentSnapshots = task.getResult();
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (int i = 0; i < list.size(); i++) {
                            DocumentSnapshot doc = list.get(i);
                            if (doc.getId().equals(productId)) {
                                //Toast.makeText(getApplicationContext(),"Already",Toast.LENGTH_SHORT).show();
                                heartIcon.setImageResource(R.drawable.ic_baseline_favorite_24);
                            }
                        }
                    }
                }
            });

            db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("cart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot queryDocumentSnapshots = task.getResult();
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (int i = 0; i < list.size(); i++) {
                            DocumentSnapshot doc = list.get(i);
                            if (doc.getId().equals(productId)) {
                                //Toast.makeText(getApplicationContext(),"Already",Toast.LENGTH_SHORT).show();
                                // heartIcon.setImageResource(R.drawable.ic_baseline_favorite_24);
                                addToCartText.setText("Remove from cart");
                            }
                        }
                    }
                }
            });

            db.collection("products").document(id).collection("reviews").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot queryDocumentSnapshots = task.getResult();
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (int i = 0; i < list.size(); i++) {
                            DocumentSnapshot doc = list.get(i);
                            if (doc.getId().equals(mAuth.getCurrentUser().getUid())) {
                                reviewEditText.setVisibility(View.GONE);
                                reviewEditTextLayout.setVisibility(View.GONE);
                                postReviewBtn.setVisibility(View.GONE);
                                reviewedLayout.setVisibility(View.VISIBLE);
                                textViewYourReview.setText(doc.getString("review"));
                                ratingBar.setRating(Float.parseFloat(doc.get("rating").toString()));
                                break;
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();


        db.collection("products").document(id).collection("sellers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot queryDocumentSnapshots = task.getResult();
                    final List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    final DocumentSnapshot doc=list.get(0);
                    String sellerID =  doc.getString("id");
                    db.collection("users").document(sellerID).collection("products").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(Integer.parseInt(documentSnapshot.getString("stock")) == 0){
                                productStatus.setText("Out of Stock");
                                productStatus.setTextColor(Color.parseColor("#DE3C3C"));
                                addToCartText.setText("Out of Stock");
                                addToCart.setClickable(false);
                            }
                        }
                    });

//                 flag = 0;
//                    for(int i=0;i<list.size();i++){
//                        DocumentSnapshot doc=list.get(i);
//                        String sellerID =  doc.getString("id");
//
//                        db.collection("users").document(sellerID).collection("products").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                            @Override
//                            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                if(Integer.parseInt(documentSnapshot.getString("stock")) <= 0){
//                                    flag++;
//                                   // Toast.makeText(getApplicationContext(),"No: Flag: "+flag,Toast.LENGTH_SHORT).show();
//                                    if(flag==list.size()){
//                                        productStatus.setText("Out of Stock");
//                                        productStatus.setTextColor(Color.parseColor("#DE3C3C"));
//                                        addToCartText.setText("Out of Stock");
//                                        addToCart.setClickable(false);
//                                    }
//                                }
//                            }
//                        });
//
//                    }
                }
            }
        });


        if(mAuth.getCurrentUser()!=null){
            db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("wishlist").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        QuerySnapshot queryDocumentSnapshots = task.getResult();
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (int i = 0; i < list.size(); i++) {
                            DocumentSnapshot doc=list.get(i);
                            if(doc.getId().equals(id)){
                              // Toast.makeText(getApplicationContext(),"Already",Toast.LENGTH_SHORT).show();
                                heartIcon.setImageResource(R.drawable.ic_baseline_favorite_24);
                                break;
                            }
                            else {
                                heartIcon.setImageResource(R.drawable.ic_baseline_favorite_border_black);
                            }
                        }
                        if(list.size()==0){
                            heartIcon.setImageResource(R.drawable.ic_baseline_favorite_border_black);
                        }
                    }
                }
            });
//
//
            db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("cart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        QuerySnapshot queryDocumentSnapshots = task.getResult();
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (int i = 0; i < list.size(); i++) {
                            DocumentSnapshot doc=list.get(i);
                            if(doc.getId().equals(id)){
                                //Toast.makeText(getApplicationContext(),"Already",Toast.LENGTH_SHORT).show();
                               // heartIcon.setImageResource(R.drawable.ic_baseline_favorite_24);
                                addToCartText.setText("Remove from cart");
                                break;
                            }
                            else {
                                addToCartText.setText("Add to cart");
                            }
                        }
                        if(list.size()==0){
                            addToCartText.setText("Add to cart");
                        }
                    }
                }
            });



//
//
//
//
          //  Toast.makeText(getApplicationContext(),id,Toast.LENGTH_SHORT).show();
        }
    }
}