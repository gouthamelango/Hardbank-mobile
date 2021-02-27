package com.example.hardbank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ProjectInformationActivity extends AppCompatActivity {

    TextView projectTitle;
    ImageView projectImage;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    RecyclerView contentRecyclerView;
    private DisplayProjectContentAdapter displayProjectContentAdapter;

    RecyclerView componentRecyclerView;
    private  DisplaySelectedComponentsAdapter displaySelectedComponentsAdapter;

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
    }
}