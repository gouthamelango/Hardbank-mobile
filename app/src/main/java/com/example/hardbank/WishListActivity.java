package com.example.hardbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
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

public class WishListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private WishListAdapter adapter;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Context context;
    List<Product> products =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);

        //Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        context  = getApplicationContext();


        //RecyclerView
        recyclerView = findViewById(R.id.productsRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);

        setUpRecyclerView();
    }
    private void setUpRecyclerView(){
        db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("wishlist").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot queryDocumentSnapshots = task.getResult();
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (int i = 0; i < list.size(); i++){
                        DocumentSnapshot doc=list.get(i);
                        //Toast.makeText(getApplicationContext(),doc.getId(),Toast.LENGTH_SHORT).show();
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
                                adapter = new WishListAdapter(context,products);
                                //Toast.makeText(getActivity().getApplicationContext(),product.getImage(),Toast.LENGTH_SHORT).show();
                                recyclerView.setAdapter(adapter);
                            }
                        });
                    }
                }
            }
        });

    }
}