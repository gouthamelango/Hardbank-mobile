package com.example.hardbank;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SellerProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SellerProductFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    FloatingActionButton fabAddProduct;

    TabLayout tabLayout;
    TabItem allProducts, pendingProducts;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    public SellerProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SellerProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SellerProductFragment newInstance(String param1, String param2) {
        SellerProductFragment fragment = new SellerProductFragment();
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
        View view = inflater.inflate(R.layout.fragment_seller_product, container, false);

        //FireBase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

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



        tabLayout = (TabLayout)view.findViewById(R.id.main_tab_menu);

        allProducts = (TabItem)view.findViewById(R.id.tabMenuAllProducts);
        pendingProducts = (TabItem)view.findViewById(R.id.tabMenuPendingProducts);
        listener();

        FragmentTransaction f1 =  getActivity().getSupportFragmentManager().beginTransaction();
        SellerAllProductFragment  sellerAllProductFragment = new SellerAllProductFragment();
        f1.replace(R.id.container,sellerAllProductFragment);
        f1.commit();

        return view;
    }
    private void listener() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                switch (pos){
                    case 0:
                        FragmentTransaction f1 =  getActivity().getSupportFragmentManager().beginTransaction();
                        SellerAllProductFragment  sellerAllProductFragment = new SellerAllProductFragment();
                        f1.replace(R.id.container,sellerAllProductFragment);
                        f1.commit();
                        break;
                    case 1:
                        FragmentTransaction f2 =  getActivity().getSupportFragmentManager().beginTransaction();
                        SellerPendingProductsFragment sellerPendingProductsFragment =  new SellerPendingProductsFragment();
                        f2.replace(R.id.container,sellerPendingProductsFragment);
                        f2.commit();
                        break;
                    case 2:
                        FragmentTransaction f3 =  getActivity().getSupportFragmentManager().beginTransaction();
                        SellerRejectedProductFragment sellerRejectedProductFragment =  new SellerRejectedProductFragment();
                        f3.replace(R.id.container,sellerRejectedProductFragment);
                        f3.commit();
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
}