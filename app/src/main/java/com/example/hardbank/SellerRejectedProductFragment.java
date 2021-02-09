package com.example.hardbank;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SellerRejectedProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SellerRejectedProductFragment extends Fragment {

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

    private SellerRejectedProductAdapter adapter;
    RecyclerView recyclerView;

    String userID;
    List<String> productsID = new ArrayList<>();
    List<String>  data = new ArrayList<>();

    public SellerRejectedProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SellerRejectedProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SellerRejectedProductFragment newInstance(String param1, String param2) {
        SellerRejectedProductFragment fragment = new SellerRejectedProductFragment();
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
        View view = inflater.inflate(R.layout.fragment_seller_rejected_product, container, false);
        //Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        //Getting my products RecyclerView
        recyclerView = view.findViewById(R.id.rejectedProductSellerRecyclerView);

        //Reference to user collection
        notebookRef = db.collection("products");

        //TO get Current seller ID
        userID = mAuth.getCurrentUser().getUid();

        setUpRecyclerView();

        return view;
    }
    private void setUpRecyclerView() {

        //To get all the products sold by that particular seller
        productsCollectionReference = db.collection("users").document(userID).collection("products");
        productsCollectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        productsID.add(document.getId());
                    }
                    if(!productsID.isEmpty()){
                        Query query = notebookRef.whereIn("id",productsID).whereNotEqualTo("reason","none");

                        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                                .setQuery(query, Product.class)
                                .build();
                        adapter = new SellerRejectedProductAdapter(options);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
                        recyclerView.setAdapter(adapter);

                        adapter.setOnDeleteClickListener(new SellerRejectedProductAdapter.OnDeleteClickListener() {
                            @Override
                            public void onDeleteClick(DocumentSnapshot documentSnapshot, int position) {
                                //Toast.makeText(getActivity().getApplicationContext(),"Deleted",Toast.LENGTH_LONG).show();
                                final String id  = documentSnapshot.getId();
                                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                                alertDialog.setTitle("Delete Product");
                                alertDialog.setMessage("Are you sure you want to Delete?");
                                alertDialog.setCancelable(false);
                                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                //Toast.makeText(getActivity().getApplicationContext(),"Deleted",Toast.LENGTH_LONG).show();
                                                db.collection("products").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        String image = documentSnapshot.getString("image");
                                                        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(image);
                                                        storageReference.delete();
                                                        db.collection("products").document(id).delete();
                                                        db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("products").document(id).delete();
                                                        Toast.makeText(getActivity().getApplicationContext(),"Product Deleted",Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        });
                                alertDialog.show();
                            }
                        });

                        adapter.setOnRequestClickListener(new SellerRejectedProductAdapter.OnRequestClickListener() {
                            @Override
                            public void onRequestClick(DocumentSnapshot documentSnapshot, int position) {
                                // Toast.makeText(getActivity().getApplicationContext(),"Requested",Toast.LENGTH_LONG).show();
                                final String id  = documentSnapshot.getId();
                                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                                alertDialog.setTitle("Request Again");
                                alertDialog.setMessage("Are you sure you want to request for verification again?");
                                alertDialog.setCancelable(false);
                                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Toast.makeText(getActivity().getApplicationContext(),"Requested",Toast.LENGTH_LONG).show();
                                                db.collection("products").document(id).update("verified","false");
                                                db.collection("products").document(id).update("reason","none");
                                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                                ft.detach(SellerRejectedProductFragment.this).attach(SellerRejectedProductFragment.this).commit();
                                            }
                                        });
                                alertDialog.show();


                            }
                        });

                        adapter.startListening();
                    }
                    else {

                    }
                }
            }
        });
    }


    @Override
    public void onStop() {
        super.onStop();
       // adapter.stopListening();
    }
}