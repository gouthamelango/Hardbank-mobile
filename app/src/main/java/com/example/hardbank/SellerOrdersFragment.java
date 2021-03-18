package com.example.hardbank;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SellerOrdersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SellerOrdersFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TabLayout tabLayout;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    RecyclerView recyclerView;
    OrdersAdapter adapter;


    public SellerOrdersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SellerOrdersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SellerOrdersFragment newInstance(String param1, String param2) {
        SellerOrdersFragment fragment = new SellerOrdersFragment();
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
        View view = inflater.inflate(R.layout.fragment_seller_orders, container, false);

        //FireBase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        tabLayout = (TabLayout)view.findViewById(R.id.main_tab_menu);
        recyclerView =  view.findViewById(R.id.itemsRecyclerView);


        loadAllOrders();
        listener();

        return view;
    }

    private void listener() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                switch (pos){
                    case 0:
                       // Toast.makeText(getActivity().getApplicationContext(), "All", Toast.LENGTH_SHORT).show();
                        loadAllOrders();
                        break;
                    case 1:
                       // Toast.makeText(getActivity().getApplicationContext(), "Pending", Toast.LENGTH_SHORT).show();
                        loadFilter("Ordered");
                        break;
                    case 2:
                       // Toast.makeText(getActivity().getApplicationContext(), "Shipped", Toast.LENGTH_SHORT).show();
                        loadFilter("Shipped");
                        break;
                    case 3:
                      //  Toast.makeText(getActivity().getApplicationContext(), "Delivered", Toast.LENGTH_SHORT).show();
                        loadFilter("Delivered");
                        break;
                    case 4:
                        //Toast.makeText(getActivity().getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                        loadFilter("Cancelled");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void loadFilter(String type){
        adapter.stopListening();
        Query query =  db.collection("orders").whereEqualTo("sellerid",mAuth.getCurrentUser().getUid())
                .whereEqualTo("status",type).orderBy("date",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<OrderModel> options = new FirestoreRecyclerOptions.Builder<OrderModel>()
                .setQuery(query, OrderModel.class)
                .build();
        adapter = new OrdersAdapter(options,"sellerorderstab");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public void loadAllOrders(){

        Query query =  db.collection("orders").whereEqualTo("sellerid",mAuth.getCurrentUser().getUid())
                .orderBy("date",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<OrderModel> options = new FirestoreRecyclerOptions.Builder<OrderModel>()
                .setQuery(query, OrderModel.class)
                .build();
        adapter = new OrdersAdapter(options,"sellerorderstab");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
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