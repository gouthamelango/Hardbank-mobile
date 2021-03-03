package com.example.hardbank;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SellerPendingProductsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SellerPendingProductsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    private CollectionReference notebookRef;
    private  CollectionReference productsCollectionReference;

    private MyPendingProductsSellerAdapter adapter;
    RecyclerView recyclerView;

    String userID;
    List<String> productsID = new ArrayList<>();

    Context context;

    List<Product> products =new ArrayList<>();

    public SellerPendingProductsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SellerPendingProductsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SellerPendingProductsFragment newInstance(String param1, String param2) {
        SellerPendingProductsFragment fragment = new SellerPendingProductsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seller_pending_products, container, false);

        //Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        //Getting my products RecyclerView
        recyclerView = view.findViewById(R.id.myProductsSellerRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        //Reference to user collection
        notebookRef = db.collection("products");

        //TO get Current seller ID
        userID = mAuth.getCurrentUser().getUid();
        context  = getActivity().getApplicationContext();
        setUpRecyclerView();

        return  view;
    }
    private void setUpRecyclerView() {

//         //To get all the products sold by that particular seller
//        productsCollectionReference = db.collection("users").document(userID).collection("products");
//        productsCollectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()){
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        productsID.add(document.getId());
//                    }
//                    if (!productsID.isEmpty()){
//                        Query query = notebookRef.whereIn("id",productsID).whereEqualTo("verified","false");
//                        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
//                                .setQuery(query, Product.class)
//                                .build();
//                        adapter = new MyPendingProductsSellerAdapter(options);
//                        recyclerView.setHasFixedSize(true);
//                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
//                        recyclerView.setAdapter(adapter);
//                        adapter.startListening();
//                    }
//                }
//
//            }
//        });


        db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot queryDocumentSnapshots = task.getResult();
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (int i = 0; i < list.size(); i++) {
                        DocumentSnapshot doc=list.get(i);
                        productsID.add(doc.getId());
                    }
                    for (int j=0;j<productsID.size();j++){
                        db.collection("products").document(productsID.get(j)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.getString("verified").equals("false")){
                                    DocumentSnapshot doc = documentSnapshot;
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
                                    adapter = new MyPendingProductsSellerAdapter(context,products);
                                    //Toast.makeText(getActivity().getApplicationContext(),product.getImage(),Toast.LENGTH_SHORT).show();
                                    recyclerView.setAdapter(adapter);
                                }

                            }
                        });
                    }


                }
            }
        });


    }
    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
        //adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}