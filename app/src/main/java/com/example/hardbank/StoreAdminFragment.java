package com.example.hardbank;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StoreAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoreAdminFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TabLayout tabLayout;

    public StoreAdminFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StoreAdminFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StoreAdminFragment newInstance(String param1, String param2) {
        StoreAdminFragment fragment = new StoreAdminFragment();
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
        View view = inflater.inflate(R.layout.fragment_store_admin, container, false);

        tabLayout = (TabLayout)view.findViewById(R.id.main_tab_menu);
        listener();
        FragmentTransaction f1 =  getActivity().getSupportFragmentManager().beginTransaction();
        SellerVerificationFragment sellerVerificationFragment =  new SellerVerificationFragment();
        f1.replace(R.id.container,sellerVerificationFragment);
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
                        SellerVerificationFragment sellerVerificationFragment =  new SellerVerificationFragment();
                        f1.replace(R.id.container,sellerVerificationFragment);
                        f1.commit();
                        break;
                    case 1:
                        FragmentTransaction f2 =  getActivity().getSupportFragmentManager().beginTransaction();
                        AllSellersFragment allSellersFragment =  new AllSellersFragment();
                        f2.replace(R.id.container,allSellersFragment);
                        f2.commit();
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