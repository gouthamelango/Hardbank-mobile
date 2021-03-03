package com.example.hardbank;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConsultantAccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConsultantAccountFragment extends Fragment {

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

    RelativeLayout resetPasswordBtn;

    public ConsultantAccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConsultantAccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConsultantAccountFragment newInstance(String param1, String param2) {
        ConsultantAccountFragment fragment = new ConsultantAccountFragment();
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
        View view = inflater.inflate(R.layout.fragment_consultant_account, container, false);

        //FireBase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        //User Details Display Dashboard
        userEmail = (TextView)view.findViewById(R.id.userEmailAccount);
        userName = (TextView)view.findViewById(R.id.username);

        resetPasswordBtn = view.findViewById(R.id.resetPasswordBtn);
        resetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRestLink();
            }
        });

        logoutBtn = (Button)view.findViewById(R.id.logoutConsultantBtn);
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

    public  void sendRestLink(){

        mAuth.sendPasswordResetEmail(mAuth.getCurrentUser().getEmail())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity().getApplicationContext(),"Password Rest Link has been sent to registered mail address",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getContext().getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

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