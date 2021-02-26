package com.example.hardbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AddProjectActivity extends AppCompatActivity {


    String projectID;
    String contentID;
    RecyclerView contentRecyclerView;
    ImageView uploadImageBtn;
    EditText editTextTitle;
    TextView addBtn;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Button createBtn;
    int counter =0;
    List<DocumentSnapshot> list;
    private ProjectContentAdapter adapter;
    List<ProjectContent> contentList =new ArrayList<>();


    TextView addComponentsBtn;

    private SelectedComponentsAdapter selectedComponentsAdapter;
    RecyclerView recyclerView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        //FireBase initialization
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        projectID = UUID.randomUUID().toString();
        contentRecyclerView = findViewById(R.id.contentRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        contentRecyclerView.setLayoutManager(mLayoutManager);
        uploadImageBtn = findViewById(R.id.uploadImageBtn);
        editTextTitle = findViewById(R.id.editTextTitle);
        createBtn =  findViewById(R.id.createBtn);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTextTitle.getText().toString();
                if(title.isEmpty()){
                    editTextTitle.setError("Title is required");
                    editTextTitle.requestFocus();
                    return;
                }

                submitProject(title);
            }
        });

        addBtn = findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNew();
            }
        });

        addComponentsBtn =  findViewById(R.id.addComponentBtn);
        addComponentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(getApplicationContext(),AddComponentsActivity.class);
                intent.putExtra("id",projectID);

                startActivity(intent);
            }
        });


        recyclerView = findViewById(R.id.componentsRecyclerView);
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager1);
        setUpRecyclerView();

        createFirstContent();
    }
    private void setUpRecyclerView() {
        Query query = db.collection("sampleprojects").document(projectID).collection("components");
        FirestoreRecyclerOptions<SelectedComponent> options = new FirestoreRecyclerOptions.Builder<SelectedComponent>()
                .setQuery(query,SelectedComponent.class)
                .build();
        selectedComponentsAdapter = new SelectedComponentsAdapter(options,projectID);
        recyclerView.setAdapter(selectedComponentsAdapter);
       selectedComponentsAdapter.startListening();
    }

    public  void  submitProject(String title){
        db.collection("sampleprojects").document(projectID).update("title", title).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Project Posted",Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });
    }

    public void createNew(){
        String id = UUID.randomUUID().toString();
        final Map<String, Object> contentData= new HashMap<>();
        contentData.put("contentid",id);
        contentData.put("projectid",projectID);
        contentData.put("content","empty");
        contentData.put("header","empty");
        contentData.put("count",counter);
        db.collection("sampleprojects").document(projectID).collection("contents").document(id).set(contentData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                list.clear();
                contentList.clear();
                counter++;
                setUpAdapter();
            }
        });

    }

    public void createFirstContent(){

        contentID = UUID.randomUUID().toString();
        final Map<String, Object> contentData= new HashMap<>();
        contentData.put("contentid",contentID);
        contentData.put("content","empty");
        contentData.put("header","empty");
        contentData.put("projectid",projectID);
        contentData.put("count",counter);

        Map<String, Object> projectData= new HashMap<>();
        projectData.put("projectid",projectID);
        projectData.put("title","empty");

        db.collection("sampleprojects").document(projectID).set(projectData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                db.collection("sampleprojects").document(projectID).collection("contents").document(contentID).set(contentData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        counter++;
                       setUpAdapter();
                    }
                });
            }
        });
    }

    public void setUpAdapter(){
        db.collection("sampleprojects").document(projectID).collection("contents").orderBy("count").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot queryDocumentSnapshots = task.getResult();
                list = queryDocumentSnapshots.getDocuments();
                for (int i = 0; i < list.size(); i++) {
                    DocumentSnapshot doc=list.get(i);
                    String header = doc.getString("header");
                    String content = doc.getString("content");
                    String contentid = doc.getString("contentid");
                    String projectid = doc.getString("projectid");

                    ProjectContent projectContent = new ProjectContent(contentid, content,header,projectid);
                    contentList.add(projectContent);
                    adapter = new ProjectContentAdapter(getApplicationContext(),contentList);
                    //Toast.makeText(getActivity().getApplicationContext(),product.getImage(),Toast.LENGTH_SHORT).show();
                    contentRecyclerView.setAdapter(adapter);
                }

            }
        });
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        db.collection("sampleprojects").document(projectID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getString("title").equals("empty")){
                    db.collection("sampleprojects").document(projectID).delete();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}