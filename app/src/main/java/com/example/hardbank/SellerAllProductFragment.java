package com.example.hardbank;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


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
 * Use the {@link SellerAllProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SellerAllProductFragment extends Fragment {

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

    StockAdapter adapter;
    RecyclerView recyclerView;

    String userID;
    List<String> productsID = new ArrayList<>();
    List<String> acceptedID = new ArrayList<>();
    Context context;

    List<ProductStock> products =new ArrayList<>();

    public SellerAllProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SellerAllProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SellerAllProductFragment newInstance(String param1, String param2) {
        SellerAllProductFragment fragment = new SellerAllProductFragment();
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
       // adapter = new StockAdapter(context,products);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seller_all_product, container, false);
        //Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        //Getting my products RecyclerView
        recyclerView = view.findViewById(R.id.allProductsSellerRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        //TO get Current seller ID
        userID = mAuth.getCurrentUser().getUid();

        context=getActivity().getApplicationContext();
        //Reference to user collection
        notebookRef = db.collection("users").document(userID).collection("products");



        return view;
    }
    private void setUpRecyclerView() {
        db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("products").orderBy("productname").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
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
                                if(documentSnapshot.getString("verified").equals("true")){
                                    DocumentSnapshot doc = documentSnapshot;
                                    db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("products").document(doc.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            DocumentSnapshot document = documentSnapshot;
                                            String productname = document.getString("productname");
                                            String stock = document.getString("stock");
                                            String productid = document.getString("productid");
                                            String category = document.getString("category");
                                            //Toast.makeText(getActivity().getApplicationContext(),productname,Toast.LENGTH_SHORT).show();
                                            ProductStock productStock = new ProductStock(category, productname, stock ,productid);
                                            products.add(productStock);
                                            adapter = new StockAdapter(context,products);
                                            recyclerView.setAdapter(adapter);
                                            //Toast.makeText(getActivity().getApplicationContext(),String.valueOf(products.size()),Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    }

                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        products.clear();
        productsID.clear();
        setUpRecyclerView();
    }
}