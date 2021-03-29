package com.example.hardbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductListingActivity extends AppCompatActivity {

    TextView requestedProductTypeText;
    RecyclerView recyclerView;
    private HomeProductAdapter adapter;
    private ProductSearchAdapter adapter1;

    RelativeLayout sortBtn;

    private CollectionReference notebookRef;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Context context;
    List<Product> products =new ArrayList<>();

    String type;
    String product;

    int flag = 0;


    Boolean sup = true;
    CheckBox stockCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_listing);

        //Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        context  = getApplicationContext();

        //Reference to user collection
        notebookRef = db.collection("products");

        sortBtn =  findViewById(R.id.sortBtn);

        stockCheckBox =  findViewById(R.id.stockCheckBox);


        //RecyclerView
        requestedProductTypeText  =  findViewById(R.id.requestedProductTypeText);
        recyclerView = findViewById(R.id.productsRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);

        Intent intent =  getIntent();
       if(intent.hasExtra("type")){
           product = "none";
           type = getIntent().getExtras().getString("type");
           setUpRecyclerView(type);
           requestedProductTypeText.setText(type);
       }
        if(intent.hasExtra("product")){
            type = "none";
            product = getIntent().getExtras().getString("product");
            setUpRecyclerViewForProduct(product);
            requestedProductTypeText.setText(product);
        }

        sortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getRootView().getContext());
                alertDialog.setTitle("Sort By");
                String[] items = {"Select","Price: Low to High","Price: High to Low"};
                int checkedItem = 0;
                alertDialog.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                break;
                            case 1:
                                if(type.equals("none")){
                                    setUpRecyclerViewForProductSort("1");
                                }
                                else if (product.equals("none")){
                                   setUpRecyclerViewForTypeSort("1");
                                }
                                break;
                            case 2:
                                if(type.equals("none")){
                                    setUpRecyclerViewForProductSort("2");
                                }
                                else if (product.equals("none")){
                                    setUpRecyclerViewForTypeSort("2");
                                }
                                break;


                        }
                    }
                });
                AlertDialog alert = alertDialog.create();
                alert.show();

            }
        });

        stockCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (stockCheckBox.isChecked()){
                    if(type.equals("none")){

                    }
                    else if (product.equals("none")){
                        setUpRecyclerViewForTypeStock("checked");
                    }
                }
                else {
                    if(type.equals("none")){

                    }
                    else if (product.equals("none")){
                        setUpRecyclerViewForTypeStock("unchecked");
                    }
                }
            }
        });
    }

    private  void setUpRecyclerViewForTypeStock(String q){
     //   products.clear();
        if(q.equals("checked")){
            setUpRecyclerView(type);
        }
        else if(q.equals("unchecked")) {

            db.collection("products").orderBy("productprice").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        products.clear();
                        QuerySnapshot queryDocumentSnapshots = task.getResult();
                        final List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (int i = 0; i < list.size(); i++) {

                            final DocumentSnapshot doc=list.get(i);
                            if(doc.getString("verified").equals("true")) {
                                if(doc.getString("category").equals(type)){

                                  //  Toast.makeText(getApplicationContext(), "Hey1: "+String.valueOf(sup), Toast.LENGTH_SHORT).show();
                                final String productID = doc.getId();

                                db.collection("products").document(doc.getId()).collection("sellers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {

                                         //   Toast.makeText(getApplicationContext(), "Hey2: "+String.valueOf(sup), Toast.LENGTH_SHORT).show();
                                            QuerySnapshot queryDocumentSnapshots = task.getResult();
                                            final List<DocumentSnapshot> list1 = queryDocumentSnapshots.getDocuments();


                                            for (int i = 0; i < list1.size(); i++) {
                                                DocumentSnapshot doc1 = list1.get(i);
                                                String sellerID = doc1.getString("id");
                                                flag = 0;

                                                sup = true;
                                              //  Toast.makeText(getApplicationContext(), "Hey3: "+String.valueOf(sup), Toast.LENGTH_SHORT).show();
                                                // Toast.makeText(getApplicationContext(),"Flag out: "+String.valueOf(list1.size()),Toast.LENGTH_SHORT).show();
                                                db.collection("users").document(sellerID).collection("products").document(productID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                        if (Integer.parseInt(documentSnapshot.getString("stock")) > 0) {
                                                            flag++;
                                                       //     Toast.makeText(getApplicationContext(),"Flag: "+String.valueOf(sup),Toast.LENGTH_SHORT).show();

                                                            if (sup) {

                                                              //  sup = false;

                                                                //Toast.makeText(getActivity().getApplicationContext(),doc.getString("productname"),Toast.LENGTH_SHORT).show();
                                                                String productname = doc.getString("productname");
                                                                int productprice = doc.getLong("productprice").intValue();
                                                                //Toast.makeText(getApplicationContext(),productname,Toast.LENGTH_SHORT).show();
                                                                String category = doc.getString("category");
                                                                String id = doc.getString("id");
                                                                String image = doc.getString("image");
                                                                String productbrand = doc.getString("productbrand");
                                                                String productdeliveryprice = doc.getString("productdeliveryprice");
                                                                String productdescription = doc.getString("productdescription");
                                                                String reason = doc.getString("reason");
                                                                String verified = doc.getString("verified");
                                                                Product product = new Product(productname, productprice, category, id, image, productbrand,
                                                                        productdeliveryprice, productdescription, verified, reason);
                                                                products.add(product);
                                                                adapter = new HomeProductAdapter(context, products);
                                                                //       Toast.makeText(getApplicationContext(),String.valueOf(products.size()),Toast.LENGTH_SHORT).show();
                                                                recyclerView.setAdapter(adapter);


                                                            }

                                                        }
                                                    }
                                                });

                                            }

                                        }
                                    }
                                });

                            }
                            }
                        }
                    }
                }
            });

        }
    }


    private  void  setUpRecyclerViewForTypeSort(String q){
        products.clear();
        if(q.equals("1")){
            db.collection("products").orderBy("productprice").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        QuerySnapshot queryDocumentSnapshots = task.getResult();
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (int i = 0; i < list.size(); i++) {
                            DocumentSnapshot doc=list.get(i);
                            if(doc.getString("verified").equals("true")){
                                if(doc.getString("category").equals(type)){
                                    //Toast.makeText(getActivity().getApplicationContext(),doc.getString("productname"),Toast.LENGTH_SHORT).show();
                                    String productname = doc.getString("productname");
                                    int productprice = doc.getLong("productprice").intValue();
                                    //Toast.makeText(getApplicationContext(),productname,Toast.LENGTH_SHORT).show();
                                    String category = doc.getString("category");
                                    String id = doc.getString("id");
                                    String image = doc.getString("image");
                                    String productbrand = doc.getString("productbrand");
                                    String productdeliveryprice = doc.getString("productdeliveryprice");
                                    String productdescription = doc.getString("productdescription");
                                    String  reason = doc.getString("reason");
                                    String verified = doc.getString("verified");
                                    Product product = new Product(productname, productprice,category,id, image,productbrand,
                                            productdeliveryprice, productdescription, verified, reason);
                                    products.add(product);
                                    adapter = new HomeProductAdapter(context,products);
                                    //Toast.makeText(getActivity().getApplicationContext(),product.getImage(),Toast.LENGTH_SHORT).show();
                                    recyclerView.setAdapter(adapter);
                                }
                            }

                        }

                    }
                }
            });
        }
        else if (q.equals("2")){
            db.collection("products").orderBy("productprice", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        QuerySnapshot queryDocumentSnapshots = task.getResult();
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (int i = 0; i < list.size(); i++) {
                            DocumentSnapshot doc=list.get(i);
                            if(doc.getString("verified").equals("true")){
                                if(doc.getString("category").equals(type)){
                                    //Toast.makeText(getActivity().getApplicationContext(),doc.getString("productname"),Toast.LENGTH_SHORT).show();
                                    String productname = doc.getString("productname");
                                    int productprice = doc.getLong("productprice").intValue();
                                    //Toast.makeText(getApplicationContext(),productname,Toast.LENGTH_SHORT).show();
                                    String category = doc.getString("category");
                                    String id = doc.getString("id");
                                    String image = doc.getString("image");
                                    String productbrand = doc.getString("productbrand");
                                    String productdeliveryprice = doc.getString("productdeliveryprice");
                                    String productdescription = doc.getString("productdescription");
                                    String  reason = doc.getString("reason");
                                    String verified = doc.getString("verified");
                                    Product product = new Product(productname, productprice,category,id, image,productbrand,
                                            productdeliveryprice, productdescription, verified, reason);
                                    products.add(product);
                                    adapter = new HomeProductAdapter(context,products);
                                    //Toast.makeText(getActivity().getApplicationContext(),product.getImage(),Toast.LENGTH_SHORT).show();
                                    recyclerView.setAdapter(adapter);
                                }
                            }

                        }

                    }
                }
            });
        }
    }
    private void setUpRecyclerView(final String type){
        products.clear();
        db.collection("products").orderBy("productprice").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot queryDocumentSnapshots = task.getResult();
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (int i = 0; i < list.size(); i++) {
                        DocumentSnapshot doc=list.get(i);
                        if(doc.getString("verified").equals("true")){
                            if(doc.getString("category").equals(type)){
                                //Toast.makeText(getActivity().getApplicationContext(),doc.getString("productname"),Toast.LENGTH_SHORT).show();
                                String productname = doc.getString("productname");
                                int productprice = doc.getLong("productprice").intValue();
                                //Toast.makeText(getApplicationContext(),productname,Toast.LENGTH_SHORT).show();
                                String category = doc.getString("category");
                                String id = doc.getString("id");
                                String image = doc.getString("image");
                                String productbrand = doc.getString("productbrand");
                                String productdeliveryprice = doc.getString("productdeliveryprice");
                                String productdescription = doc.getString("productdescription");
                                String  reason = doc.getString("reason");
                                String verified = doc.getString("verified");
                                Product product = new Product(productname, productprice,category,id, image,productbrand,
                                        productdeliveryprice, productdescription, verified, reason);
                                products.add(product);
                                adapter = new HomeProductAdapter(context,products);
                                //Toast.makeText(getActivity().getApplicationContext(),product.getImage(),Toast.LENGTH_SHORT).show();
                                recyclerView.setAdapter(adapter);
                            }
                        }

                    }

                }
            }
        });
    }

    private  void  setUpRecyclerViewForProduct(String product){

        products.clear();
        String lowerCase = product.toLowerCase();
        Query query = notebookRef.whereArrayContains("keys",lowerCase).orderBy("productprice");
        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query, Product.class)
                .build();

        adapter1 = new ProductSearchAdapter(options);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(adapter1);
        adapter1.startListening();
       // Toast.makeText(getApplicationContext(),lowerCase,Toast.LENGTH_SHORT).show();

    }
    private  void  setUpRecyclerViewForProductSort(String q){
    products.clear();
        if(q.equals("1")){
            setUpRecyclerViewForProduct(product);
        }
        else if(q.equals("2")) {
            String lowerCase = product.toLowerCase();
            Query query = notebookRef.whereArrayContains("keys",lowerCase).orderBy("productprice", Query.Direction.DESCENDING);
            FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                    .setQuery(query, Product.class)
                    .build();

            adapter1 = new ProductSearchAdapter(options);

            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
            recyclerView.setLayoutManager(mLayoutManager);

            recyclerView.setAdapter(adapter1);
            adapter1.startListening();
        }

    }

}