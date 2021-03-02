package com.example.hardbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectInformationActivity extends AppCompatActivity {

    TextView projectTitle;
    ImageView projectImage;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    RecyclerView contentRecyclerView;
    private DisplayProjectContentAdapter displayProjectContentAdapter;

    RecyclerView componentRecyclerView;
    private  DisplaySelectedComponentsAdapter displaySelectedComponentsAdapter;

    Button addToCartBtn;

    Toolbar toolbar;
    Menu menu;
    String projectID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_information);

        projectTitle =  findViewById(R.id.projectTitle);
        projectImage =  findViewById(R.id.projectImage);
        //FireBase initialization
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Toolbar
        toolbar =  findViewById(R.id.projectToolbarLayout);
        toolbar.inflateMenu(R.menu.project_information_menu);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Tapped",Toast.LENGTH_LONG).show();
            }
        });
        menu = toolbar.getMenu();

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_wish_list:
                        //Toast.makeText(getApplicationContext(),"Fav",Toast.LENGTH_LONG).show();
                        if(mAuth.getCurrentUser()!=null){
                            Intent intent = new Intent(getApplicationContext(),WishListActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Intent customerLoginActivity = new Intent(getApplicationContext(),CustomerLoginActivity.class);
                            startActivity(customerLoginActivity);
                            overridePendingTransition(R.anim.bottom_up, R.anim.nothing_ani);
                        }
                        break;
                    case R.id.nav_cart:
                        //Toast.makeText(getApplicationContext(),"Cart",Toast.LENGTH_LONG).show();
                        if(mAuth.getCurrentUser()!= null){
                            Intent cartActivityIntent = new Intent(getApplicationContext(), CustomerCartActivity.class);
                            startActivity(cartActivityIntent);
                            overridePendingTransition(R.anim.bottom_up, R.anim.nothing_ani);
                        }
                        else {
                            Intent customerLoginActivity = new Intent(getApplicationContext(),CustomerLoginActivity.class);
                            startActivity(customerLoginActivity);
                            overridePendingTransition(R.anim.bottom_up, R.anim.nothing_ani);
                        }
                        break;

                }
                return false;
            }
        });

        contentRecyclerView = findViewById(R.id.contentRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        contentRecyclerView.setLayoutManager(mLayoutManager);

        componentRecyclerView =  findViewById(R.id.componentRecyclerView);
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getApplicationContext());
        componentRecyclerView.setLayoutManager(mLayoutManager1);


        Intent intent = getIntent();
        if(intent.hasExtra("id")){
            projectID =  intent.getExtras().getString("id");
            db.collection("sampleprojects").document(projectID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    projectTitle.setText(documentSnapshot.getString("title"));
                    Glide.with(projectImage.getContext())
                            .load(documentSnapshot.getString("image"))
                            .into(projectImage);
                }
            });
        }

        setUpRecyclerView();

        addToCartBtn = findViewById(R.id.addToCartBtn);
        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAuth.getCurrentUser()!=null){
                    db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("projectscart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                int flag = 0;
                                QuerySnapshot queryDocumentSnapshots = task.getResult();
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                for (int i = 0; i < list.size(); i++) {
                                    DocumentSnapshot doc=list.get(i);
                                    if(doc.getId().equals(projectID)){
                                        //Toast.makeText(getApplicationContext(),"Already",Toast.LENGTH_SHORT).show();
                                        db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("projectscart").document(doc.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getApplicationContext(),"Removed from Cart",Toast.LENGTH_SHORT).show();
                                                //heartIcon.setImageResource(R.drawable.ic_baseline_favorite_border_black);
                                                addToCartBtn.setText("Add All to cart");
                                            }
                                        });
                                        flag=1;
                                        break;
                                    }
                                }
                                if(flag ==0){
                                    Map<String, Object> productData = new HashMap<>();
                                    productData.put("id",projectID);
                                    productData.put("quantity","1");
                                    db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("projectscart").document(projectID).set(productData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(),"Added to Cart",Toast.LENGTH_SHORT).show();
                                            //heartIcon.setImageResource(R.drawable.ic_baseline_favorite_24);
                                           addToCartBtn.setText("Remove from cart");
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
                else {
                    Intent customerLoginActivity = new Intent(getApplicationContext(),CustomerLoginActivity.class);
                    startActivity(customerLoginActivity);
                    overridePendingTransition(R.anim.bottom_up, R.anim.nothing_ani);
                }
            }
        });
        setUpRecyclerView1();
    }
    private void setUpRecyclerView() {
        Query query = db.collection("sampleprojects").document(projectID).collection("contents").whereNotEqualTo("header","empty");
        FirestoreRecyclerOptions<ProjectContent> options = new FirestoreRecyclerOptions.Builder<ProjectContent>()
                .setQuery(query,ProjectContent.class)
                .build();
        displayProjectContentAdapter = new DisplayProjectContentAdapter(options);
        contentRecyclerView.setAdapter(displayProjectContentAdapter);
        displayProjectContentAdapter.startListening();
    }

    private void setUpRecyclerView1() {
        Query query = db.collection("sampleprojects").document(projectID).collection("components");
        FirestoreRecyclerOptions<SelectedComponent> options = new FirestoreRecyclerOptions.Builder<SelectedComponent>()
                .setQuery(query,SelectedComponent.class)
                .build();
        displaySelectedComponentsAdapter = new DisplaySelectedComponentsAdapter(options);
        componentRecyclerView.setAdapter(displaySelectedComponentsAdapter);
        displaySelectedComponentsAdapter.startListening();

        if(mAuth.getCurrentUser()!=null){
            db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("projectscart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        QuerySnapshot queryDocumentSnapshots = task.getResult();
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (int i = 0; i < list.size(); i++) {
                            DocumentSnapshot doc=list.get(i);
                            if(doc.getId().equals(projectID)){
                               addToCartBtn.setText("Remove from cart");
                            }
                        }
                    }
                }
            });
        }
    }
}