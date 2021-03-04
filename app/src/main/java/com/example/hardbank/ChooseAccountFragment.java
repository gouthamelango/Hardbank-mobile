package com.example.hardbank;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChooseAccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChooseAccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Button logInBtn;
    Button logoutBtn;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String userId;

    TextView userName, userEmail;

    RelativeLayout userDetailsLayout, customerFunctionsChooserLayout, sellOnHardBank, managementBtn,whoWeAreBtn, joinOurTeamBtn;

    RelativeLayout helpAndSupport;

    public ChooseAccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChooseAccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChooseAccountFragment newInstance(String param1, String param2) {
        ChooseAccountFragment fragment = new ChooseAccountFragment();
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
        View view =inflater.inflate(R.layout.fragment_choose_account, container, false);

        //FireBase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        //Customer Feature Btns
        customerFunctionsChooserLayout = view.findViewById(R.id.customerFunctionsChooserLayout);

        //User Details Display Dashboard
        userDetailsLayout =  view.findViewById(R.id.userDetailsLayout);
        userEmail = (TextView)view.findViewById(R.id.userEmailAccountChooserFragment);
        userName = (TextView)view.findViewById(R.id.userNameAccountChooserFragment);

        //Login Btn Navigation
        logInBtn = view.findViewById(R.id.loginBtn);
        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent customerLoginActivity = new Intent(getActivity().getApplicationContext(),CustomerLoginActivity.class);
                startActivity(customerLoginActivity);
                getActivity().overridePendingTransition(R.anim.bottom_up, R.anim.nothing_ani);
            }
        });

        logoutBtn = (Button)view.findViewById(R.id.logoutBtnAccountChooserFragment);
        //Logout Listener
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                getActivity().finish();
                startActivity(new Intent(getActivity().getApplicationContext(), HomeActivity.class));
            }
        });

        //helpAndSupport
        helpAndSupport =  view.findViewById(R.id.helpAndSupport);
        helpAndSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(getActivity().getApplicationContext(),ChatWithUsActivity.class);
                startActivity(intent);
            }
        });

        //Who are we
        whoWeAreBtn =  (RelativeLayout)view.findViewById(R.id.whoWeAreBtn);
        whoWeAreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(getActivity().getApplicationContext(),AboutUsActivity.class);
                startActivity(intent);
            }
        });

        //Sell on HardBank
        sellOnHardBank =  view.findViewById(R.id.sellOnHardBankBtn);
        sellOnHardBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sellerSignInActivity =  new Intent(getActivity().getApplicationContext(),SellerSignUpActivity.class);
                startActivity(sellerSignInActivity);
            }
        });

        //Management
        managementBtn = view.findViewById(R.id.managementBtn);
        managementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sellerSignInActivity =  new Intent(getActivity().getApplicationContext(),AdminLoginActivity.class);
                startActivity(sellerSignInActivity);
            }
        });

        //Join Us
        joinOurTeamBtn =  view.findViewById(R.id.joinOurTeamBtn);
        joinOurTeamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent consultantLoginIntent  =  new Intent(getActivity().getApplicationContext(),ConsultantLoginActivity.class);
                startActivity(consultantLoginIntent);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            logInBtn.setVisibility(View.VISIBLE);
            userDetailsLayout.setVisibility(View.GONE);
            customerFunctionsChooserLayout.setVisibility(View.GONE);
        }
        else  if(mAuth.getCurrentUser() != null){

            userDetailsLayout.setVisibility(View.VISIBLE);
            customerFunctionsChooserLayout.setVisibility(View.VISIBLE);
            logInBtn.setVisibility(View.GONE);

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