package com.example.hardbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class UpdateDocActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;

    ImageView preview;

    private Bitmap compressed;

    Button btnSelect;
    Button uploadBtn;

    private Uri imageUri = null;
    String currentDoc;

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_doc);

        //FireBase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        userId = mAuth.getCurrentUser().getUid();

        preview = findViewById(R.id.selectedImagePreview);

        //Btn to choose doc
        btnSelect = findViewById(R.id.selectImageBtn);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(UpdateDocActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        choseImage();
                    }
                } else {
                    choseImage();
                }
            }
        });

        //post
        uploadBtn = findViewById(R.id.uploadBtn);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageUri==null){
                    Toast.makeText(getApplicationContext(),"Choose Supporting Doc Image",Toast.LENGTH_SHORT).show();
                    return;
                }

                db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        currentDoc = documentSnapshot.getString("doc");

                    }
                });

                //Processing Image
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

                final StorageReference ur_firebase_reference  = storageReference.child("images/sellerverification").child( UUID.randomUUID().toString()+ ".jpg");
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

                                String photoStringLink = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                                //Toast.makeText(getApplicationContext(),photoStringLink,Toast.LENGTH_LONG).show();

                               changeDoc(photoStringLink);
                            }

                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });

            }
        });
    }

    public  void changeDoc(String downloadUri){

        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(currentDoc);
        storageReference.delete();
        db.collection("users").document(userId).update("doc",downloadUri);
        db.collection("users").document(userId).update("comment","none");
        Toast.makeText(getApplicationContext(),"Document Uploaded",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(),SellerHome.class);
        startActivity(intent);

    }
    private void choseImage() {
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(UpdateDocActivity.this);

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {


            CropImage.ActivityResult result = CropImage.getActivityResult(data);


            if (resultCode == RESULT_OK) {


                imageUri = result.getUri();


                preview.setImageURI(imageUri);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {


                Exception error = result.getError();


            }

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}