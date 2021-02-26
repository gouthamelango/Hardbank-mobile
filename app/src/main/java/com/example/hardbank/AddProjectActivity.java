package com.example.hardbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

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

    private Uri imageUri = null;
    private Bitmap compressed;
    TextView addComponentsBtn;

    private SelectedComponentsAdapter selectedComponentsAdapter;
    RecyclerView recyclerView;

    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        //FireBase initialization
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        projectID = UUID.randomUUID().toString();
        contentRecyclerView = findViewById(R.id.contentRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        contentRecyclerView.setLayoutManager(mLayoutManager);
        uploadImageBtn = findViewById(R.id.uploadImageBtn);
        editTextTitle = findViewById(R.id.editTextTitle);
        createBtn =  findViewById(R.id.createBtn);


        uploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(AddProjectActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        choseImage();
                    }
                } else {
                    choseImage();
                }
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTextTitle.getText().toString();
                if(imageUri==null){
                    Toast.makeText(getApplicationContext(),"Choose Supporting Doc Image",Toast.LENGTH_SHORT).show();
                    return;
                }
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

    public  void  submitProject(final String title){


        File newFile = new File(imageUri.getPath());
        try {
            compressed = new Compressor(getApplicationContext())
                    .setMaxHeight(125)
                    .setMaxWidth(125)
                    .setQuality(100)
                    .compressToBitmap(newFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        compressed.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] thumbData = byteArrayOutputStream.toByteArray();
        final StorageReference ur_firebase_reference  = storageReference.child("images/project").child( UUID.randomUUID().toString()+ ".jpg");
        UploadTask image_path = ur_firebase_reference.putFile(imageUri);

        Task<Uri> urlTask = image_path.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return ur_firebase_reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    System.out.println("Upload " + downloadUri);
                    // Toast.makeText(getApplicationContext(), "Successfully uploaded", Toast.LENGTH_SHORT).show();
                    if (downloadUri != null) {

                        final String photoStringLink = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                        //Toast.makeText(getApplicationContext(),photoStringLink,Toast.LENGTH_LONG).show();

                       // changeDoc(photoStringLink);
                        db.collection("sampleprojects").document(projectID).update("title", title).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                db.collection("sampleprojects").document(projectID).update("image", photoStringLink).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(),"Project Posted",Toast.LENGTH_SHORT).show();
                                        onBackPressed();
                                    }
                                });

                            }
                        });
                    }

                } else {
                    // Handle failures
                    // ...
                }
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
        projectData.put("image","empty");

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

    private void choseImage() {
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(AddProjectActivity.this);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {


            CropImage.ActivityResult result = CropImage.getActivityResult(data);


            if (resultCode == RESULT_OK) {


                imageUri = result.getUri();


                uploadImageBtn.setImageURI(imageUri);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {


                Exception error = result.getError();


            }

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}