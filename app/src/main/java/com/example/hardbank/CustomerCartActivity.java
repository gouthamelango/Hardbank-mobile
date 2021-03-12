package com.example.hardbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CustomerCartActivity extends AppCompatActivity implements MyInterface{

    RelativeLayout backNav;
    Button continueShopping;
    RelativeLayout ifCartIsEmptyLayout,notEmptyLayout,actionsLayout;
    Context context;


    List<Product> products =new ArrayList<>();
    private CartAdapter adapter;
    RecyclerView recyclerView;

    List<SampleProject> projects =new ArrayList<>();
    private CartProjectAdapter adapterProject;
    RecyclerView recyclerViewProject;


    FirebaseAuth mAuth;
    FirebaseFirestore db;

    int cartTotal= 0;
    int delivery = 0;
    int totalAmount = 0;


    Button confirmOrderBtn;

    TextView textViewCartTotal, textViewDeliveryAmount,textViewTotalAmount,textViewTotalPrice;

    List<String> productsInCart =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_cart);

        //Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        //Back Nav
        backNav = findViewById(R.id.CartActivityBackNav);
        backNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //ContinueShopping
        continueShopping =  findViewById(R.id.continueShoppingFromCart);
        continueShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        textViewCartTotal = findViewById(R.id.textViewCartTotal);
        textViewDeliveryAmount =  findViewById(R.id.textViewDeliveryAmount);
        textViewTotalAmount =  findViewById(R.id.textViewTotalAmount);
        textViewTotalPrice =  findViewById(R.id.textViewTotalPrice);

        confirmOrderBtn =  findViewById(R.id.confirmOrderBtn);


        recyclerView =  findViewById(R.id.productsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        recyclerViewProject =  findViewById(R.id.projectsRecyclerView);
        recyclerViewProject.setHasFixedSize(true);
        recyclerViewProject.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        //If Empty
        ifCartIsEmptyLayout =  findViewById(R.id.ifCartIsEmptyLayout);
        actionsLayout =  findViewById(R.id.actionsLayout);
        notEmptyLayout =  findViewById(R.id.notEmptyLayout);
       if(mAuth.getCurrentUser().getUid()!=null){
           db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("cart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
               @Override
               public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        QuerySnapshot queryDocumentSnapshots = task.getResult();
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        if(list.isEmpty()){
                            db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("projectscart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        QuerySnapshot queryDocumentSnapshots = task.getResult();
                                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                        if(list.isEmpty()){
                                            ifCartIsEmptyLayout.setVisibility(View.VISIBLE);
                                            notEmptyLayout.setVisibility(View.GONE);
                                            actionsLayout.setVisibility(View.GONE);
                                        }


                                    }
                                }
                            });
                        }

                        else {
                            for (int i = 0; i < list.size(); i++) {
                                DocumentSnapshot doc=list.get(i);
                                db.collection("products").document(doc.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        String productname = documentSnapshot.getString("productname");
                                        int productprice = documentSnapshot.getLong("productprice").intValue();
                                        //Toast.makeText(getApplicationContext(),productname,Toast.LENGTH_SHORT).show();
                                        String category = documentSnapshot.getString("category");
                                        String id = documentSnapshot.getString("id");
                                        String image = documentSnapshot.getString("image");
                                        String productbrand = documentSnapshot.getString("productbrand");
                                        String productdeliveryprice = documentSnapshot.getString("productdeliveryprice");
                                        String productdescription = documentSnapshot.getString("productdescription");
                                        String  reason = documentSnapshot.getString("reason");
                                        String verified = documentSnapshot.getString("verified");
                                        Product product = new Product(productname, productprice,category,id, image,productbrand,
                                                productdeliveryprice, productdescription, verified, reason);
                                        products.add(product);
                                        adapter = new CartAdapter(context,products,CustomerCartActivity.this);
                                        //Toast.makeText(getApplicationContext(),productname,Toast.LENGTH_SHORT).show();
                                        recyclerView.setAdapter(adapter);
                                    }
                                });
                            }
                        }
                    }
               }
           });

           db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("projectscart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
               @Override
               public void onComplete(@NonNull Task<QuerySnapshot> task) {
                   if(task.isSuccessful()){
                       QuerySnapshot queryDocumentSnapshots = task.getResult();
                       List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                       for (int i = 0; i < list.size(); i++) {
                           DocumentSnapshot doc=list.get(i);
                           db.collection("sampleprojects").document(doc.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                               @Override
                               public void onSuccess(DocumentSnapshot documentSnapshot) {
                                   String projectid = documentSnapshot.getString("projectid");
                                   String image = documentSnapshot.getString("image");
                                   String title = documentSnapshot.getString("title");
                                   SampleProject sampleProject =  new SampleProject(image,projectid,title);
                                   projects.add(sampleProject);
                                   //Toast.makeText(getApplicationContext(),String.valueOf(projects.size()),Toast.LENGTH_SHORT).show();
                                   adapterProject = new CartProjectAdapter(context,projects,CustomerCartActivity.this);
                                   recyclerViewProject.setAdapter(adapterProject);
                               }
                           });
                       }

                   }
               }
           });

           confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("addresses").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                QuerySnapshot queryDocumentSnapshots = task.getResult();
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                if(list.size()==0){
                                    Intent intent =  new Intent(getApplicationContext(),AddressBookActivity.class);
                                    startActivity(intent);
                                }
                                else {
                                  //  Toast.makeText(getApplicationContext(),"Hey",Toast.LENGTH_SHORT).show();
                                    Intent intent =  new Intent(getApplicationContext(),ConfirmOrderActivity.class);
                                    intent.putExtra("cart",cartTotal);
                                    intent.putExtra("delivery",delivery);
                                    intent.putExtra("total",totalAmount);
                                    intent.putStringArrayListExtra("productsInCart",(ArrayList<String>) productsInCart);
                                    startActivity(intent);
                                }
                            }
                       }
                   });
               }
           });
       }
        updatePrice();
    }

    public  void updatePrice(){
        cartTotal = 0;
        delivery = 0;
        totalAmount = 0;
        productsInCart.clear();
        db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("cart")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot queryDocumentSnapshots = task.getResult();
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (int i = 0; i < list.size(); i++) {
                        DocumentSnapshot doc=list.get(i);
                        productsInCart.add(doc.getId());
                        //Toast.makeText(getApplicationContext(),doc.getString("id"),Toast.LENGTH_SHORT).show();
                        int tot =  Integer.parseInt(doc.get("price").toString()) *  Integer.parseInt(doc.get("quantity").toString());
                        cartTotal +=  tot;
                        //Toast.makeText(getApplicationContext(),String.valueOf(cartTotal),Toast.LENGTH_SHORT).show();
                    }
                    if(cartTotal>499){
                        delivery = 0;
                    }
                    else if(cartTotal==0){
                        delivery = 0;
                    }
                    else {
                        delivery = 40;
                    }
                    totalAmount = delivery + cartTotal;
                    textViewCartTotal.setText(String.valueOf(cartTotal));
                    textViewTotalAmount.setText(String.valueOf(totalAmount));
                    textViewTotalPrice.setText(String.valueOf(totalAmount));
                    textViewDeliveryAmount.setText(String.valueOf(delivery));


                    db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("projectscart")
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {


                            if(task.isSuccessful()){
                                QuerySnapshot queryDocumentSnapshots = task.getResult();
                                List<DocumentSnapshot> list1 = queryDocumentSnapshots.getDocuments();
                                for (int i = 0; i < list1.size(); i++){
                                    DocumentSnapshot doc=list1.get(i);
                                    db.collection("sampleprojects").document(doc.getId()).collection("components")
                                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {


                                            if(task.isSuccessful()){
                                                QuerySnapshot queryDocumentSnapshots = task.getResult();
                                                List<DocumentSnapshot> list2 = queryDocumentSnapshots.getDocuments();
                                                for (int i = 0; i < list2.size(); i++){
                                                    DocumentSnapshot doc=list2.get(i);
                                                    productsInCart.add(doc.getId());
                                                    cartTotal +=  Integer.parseInt(doc.get("productprice").toString());
                                                }
                                                // Toast.makeText(getApplicationContext(),String.valueOf(cartTotal),Toast.LENGTH_SHORT).show();
                                                if(cartTotal>499){
                                                    delivery = 0;
                                                }
                                                else if(cartTotal==0){
                                                    delivery = 0;
                                                }
                                                else {
                                                    delivery = 40;
                                                }
                                                totalAmount = delivery + cartTotal;
                                                textViewCartTotal.setText(String.valueOf(cartTotal));
                                                textViewTotalAmount.setText(String.valueOf(totalAmount));
                                                textViewTotalPrice.setText(String.valueOf(totalAmount));
                                                textViewDeliveryAmount.setText(String.valueOf(delivery));
                                            }
                                        }
                                    });
                                }


                            }
                        }
                    });


                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.nothing_ani, R.anim.bottom_down);
    }
}