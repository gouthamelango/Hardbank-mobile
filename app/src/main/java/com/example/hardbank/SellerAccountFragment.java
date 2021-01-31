package com.example.hardbank;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SellerAccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SellerAccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button logoutBtn;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String userId;

    TextView userName, userEmail;

    public SellerAccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SellerAccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SellerAccountFragment newInstance(String param1, String param2) {
        SellerAccountFragment fragment = new SellerAccountFragment();
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
       View view   = inflater.inflate(R.layout.fragment_seller_account, container, false);;

        //FireBase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        //User Details Display Dashboard
        userEmail = (TextView)view.findViewById(R.id.userEmailAccountSeller);
        userName = (TextView)view.findViewById(R.id.userNameAccountSeller);


        logoutBtn = (Button)view.findViewById(R.id.logoutSellerBtn);
        //Logout Listener
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                getActivity().finish();
                startActivity(new Intent(getActivity().getApplicationContext(), HomeActivity.class));
            }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
        }
        else  if(mAuth.getCurrentUser() != null){
            //Displaying Data
            userEmail.setText(mAuth.getCurrentUser().getEmail());
            userId  = mAuth.getCurrentUser().getUid();
            db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String user_name = documentSnapshot.getString("fullname");
                    userName.setText(user_name);
                }
            });
        }
    }
}