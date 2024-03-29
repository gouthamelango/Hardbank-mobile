package com.example.hardbank;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SellerDashBoardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SellerDashBoardFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    FloatingActionButton fabAddProduct;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    RecyclerView recyclerView;
    OrdersAdapter adapter;

    public SellerDashBoardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SellerDashBoardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SellerDashBoardFragment newInstance(String param1, String param2) {
        SellerDashBoardFragment fragment = new SellerDashBoardFragment();
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
        View view = inflater.inflate(R.layout.fragment_seller_dash_board, container, false);

        //FireBase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        recyclerView =  view.findViewById(R.id.newOrdersRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        //FAB
        fabAddProduct = view.findViewById(R.id.floatingActionAddProduct);
        fabAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.getString("verified").equals("true")){
                            Intent newNoteIntent = new Intent(getActivity().getApplicationContext(), AddProductActivity.class);
                            startActivity(newNoteIntent);
                            getActivity().overridePendingTransition(R.anim.bottom_up, R.anim.nothing_ani);
                        }
                        else {
                            Toast.makeText(getActivity().getApplicationContext(),"You're not yet Verified by HardBank!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        loadAllOrders();
        return view;
    }

    public void loadAllOrders(){
        Query query =  db.collection("orders").whereEqualTo("sellerid",mAuth.getCurrentUser().getUid())
                .orderBy("date",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<OrderModel> options = new FirestoreRecyclerOptions.Builder<OrderModel>()
                .setQuery(query, OrderModel.class)
                .build();
        adapter = new OrdersAdapter(options,"sellerorderstab");

//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setAdapter(adapter);

        adapter.startListening();
    }
    @Override
    public void onResume() {
        super.onResume();
        adapter.stopListening();
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }
}