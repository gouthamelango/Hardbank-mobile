package com.example.hardbank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AddressBookActivity extends AppCompatActivity {

    TextView addAddressText;

    RecyclerView recyclerView;
    AddressAdapter adapter;

    FirebaseAuth mAuth;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_book);

        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        addAddressText =  findViewById(R.id.addAddressText);
        addAddressText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AddAddressActivity.class);
                startActivity(intent);
            }
        });

        recyclerView =  findViewById(R.id.addressRecyclerView);

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query =  db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("addresses");
        FirestoreRecyclerOptions<AddressModel> options = new FirestoreRecyclerOptions.Builder<AddressModel>()
                .setQuery(query, AddressModel.class)
                .build();
        adapter = new AddressAdapter(options);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnDeleteClickListener(new AddressAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(DocumentSnapshot documentSnapshot, int position) {

                final String id  = documentSnapshot.getId();
                AlertDialog alertDialog = new AlertDialog.Builder(AddressBookActivity.this).create();
                alertDialog.setTitle("Delete");
                alertDialog.setMessage("Are you sure you want to Delete this Address?");
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
                                db.collection("users").document(mAuth.getCurrentUser().getUid())
                                        .collection("addresses").document(id).delete();
                                Toast.makeText(getApplicationContext(),"Address Deleted",Toast.LENGTH_SHORT).show();
                            }
                        });
                alertDialog.show();
            }
        });

        adapter.setOnEditClickListener(new AddressAdapter.OnEditClickListener() {
            @Override
            public void onEditClick(DocumentSnapshot documentSnapshot, int position) {
                final String id  = documentSnapshot.getId();
               // Toast.makeText(getApplicationContext(),"Edit Clicked",Toast.LENGTH_SHORT).show();
                Intent intent =  new Intent(getApplicationContext(),AddAddressActivity.class);
                intent.putExtra("editid",id);
                startActivity(intent);
            }
        });

        adapter.startListening();
    }
}