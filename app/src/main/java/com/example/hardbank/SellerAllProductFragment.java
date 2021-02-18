package com.example.hardbank;

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
    private  CollectionReference productsCollectionReference;

    public StockAdapter adapter;
    RecyclerView recyclerView;

    String userID;
    List<String> productsID = new ArrayList<>();
    List<String> acceptedID = new ArrayList<>();

    Boolean allowRefresh = true;

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
        //TO get Current seller ID
        userID = mAuth.getCurrentUser().getUid();

        //Reference to user collection
        notebookRef = db.collection("users").document(userID).collection("products");


        setUpRecyclerView();
        return view;
    }
    private void setUpRecyclerView() {
        productsCollectionReference = db.collection("users").document(userID).collection("products");
        productsCollectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        productsID.add(document.getId());
                    }
                    if (!productsID.isEmpty()){
                        db.collection("products").whereIn("id",productsID).whereEqualTo("verified","true").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        acceptedID.add(document.getId());
                                    }
                                    if(!acceptedID.isEmpty()){
                                        Query query = notebookRef.whereIn("productid",acceptedID).orderBy("productname");
                                        FirestoreRecyclerOptions<ProductStock> options = new FirestoreRecyclerOptions.Builder<ProductStock>()
                                                .setQuery(query, ProductStock.class)
                                                .build();
                                        adapter = new StockAdapter(options);
                                        recyclerView.setHasFixedSize(true);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
                                        recyclerView.setAdapter(adapter);
                                        adapter.setOnItemClickListener(new StockAdapter.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                                                //Toast.makeText(getContext().getApplicationContext(),"Clicked",Toast.LENGTH_SHORT).show();
                                                final String id  = documentSnapshot.getId();
                                                Intent intent =  new Intent(getActivity().getApplicationContext(),UpdateStockActivity.class);
                                                intent.putExtra("id",id);
                                                startActivity(intent);
                                            }
                                        });
                                        adapter.startListening();
                                    }
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
        adapter.stopListening();

    }

    @Override
    public void onResume() {
        super.onResume();
        setUpRecyclerView();
    }
}