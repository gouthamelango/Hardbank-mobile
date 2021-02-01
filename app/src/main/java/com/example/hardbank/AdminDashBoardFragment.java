package com.example.hardbank;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminDashBoardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminDashBoardFragment extends Fragment {

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

    private ProductAdapter adapter;
    RecyclerView recyclerView;

    public AdminDashBoardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminDashBoardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminDashBoardFragment newInstance(String param1, String param2) {
        AdminDashBoardFragment fragment = new AdminDashBoardFragment();
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
        View view =inflater.inflate(R.layout.fragment_admin_dash_board, container, false);

        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.adminNewRequestOfProductsRecyclerView);
        notebookRef = db.collection("products");
        setUpRecyclerView();
        return view;
    }
    private void setUpRecyclerView() {
        Query query = notebookRef.whereEqualTo("verified","false");
        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query, Product.class)
                .build();
        adapter = new ProductAdapter(options);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Product product = documentSnapshot.toObject(Product.class);
                String id  = documentSnapshot.getId();
                Toast.makeText(getActivity().getApplicationContext(),"Position: "+position+ " ID: "+id,Toast.LENGTH_LONG).show();
            }
        });
        adapter.setOnRejectClickListener(new ProductAdapter.OnRejectClickListener() {
            @Override
            public void onRejectClick(DocumentSnapshot documentSnapshot, int position) {
                Toast.makeText(getActivity().getApplicationContext(),"Rejected",Toast.LENGTH_LONG).show();
            }
        });
        adapter.setOnAcceptClickListener(new ProductAdapter.OnAcceptClickListener() {
            @Override
            public void onAcceptClick(DocumentSnapshot documentSnapshot, int position) {
                Toast.makeText(getActivity().getApplicationContext(),"Accepted",Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}