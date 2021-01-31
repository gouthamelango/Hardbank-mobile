package com.example.hardbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class NewProductActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;

    // views for button
    Button btnSelect, btnUpload;

    //Views for editText
    EditText productNameEditText,productBrandEditText,productPriceEditText,productDeliveryPriceEditText,productDescriptionEditText, productStockEditText;

    String user_id;
    private Uri imageUri = null;
    private Bitmap compressed;
    ProgressDialog progressDialog;
    ImageView previewImage;

    String sellerName;
    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    Spinner spinner;
    EditText otherCategory;
    String selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);

        //FireBase initialization
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

        //initialization
        otherCategory =  findViewById(R.id.typeCategoryEditText);
        productNameEditText = findViewById(R.id.productNameEditText);
        productBrandEditText = findViewById(R.id.productBrandEditText);
        productPriceEditText = findViewById(R.id.productPriceEditText);
        productDeliveryPriceEditText= findViewById(R.id.productDeliveryPriceEditText);
        productDescriptionEditText  =  findViewById(R.id.productDescriptionEditText);
        productStockEditText = findViewById(R.id.productStockEditText);
        progressDialog = new ProgressDialog(this);
        previewImage = findViewById(R.id.selectedImagePreview);


        //Categories Spinner
        spinner = (Spinner) findViewById(R.id.categorySpinner);
        final List<String> categories = new ArrayList<>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        db.collection("products").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                categories.add("Select");
                for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()) {
//                   Toast.makeText(getApplicationContext(), doc.getDocument().getId().toString(),Toast.LENGTH_LONG).show();
                    categories.add(doc.getDocument().getId().toString());
                }
                categories.add("Others");
                adapter.notifyDataSetChanged();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCategory = spinner.getSelectedItem().toString();
                if (selectedCategory.equals("Others")){
                   // Toast.makeText(getApplicationContext(), "Others Selected",Toast.LENGTH_LONG).show();
                    otherCategory.setVisibility(View.VISIBLE);
                }
                else {
                    otherCategory.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        //Upload Images
        // initialise views
        btnSelect = findViewById(R.id.selectImageBtn);
        btnUpload = findViewById(R.id.addProductBtn);

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // on pressing btnSelect SelectImage() is called

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(NewProductActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        choseImage();
                    }
                } else {
                    choseImage();
                }
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "Add Product", Toast.LENGTH_SHORT).show();
                progressDialog.setMessage("Storing Data...");
                progressDialog.show();

                final String productName = productNameEditText.getText().toString();
                final String productBrand = productBrandEditText.getText().toString();
                final String productPrice = productPriceEditText.getText().toString();
                final String productDeliveryPrice = productDeliveryPriceEditText.getText().toString();
                final String productDescription =   productDescriptionEditText.getText().toString();
                final String productStock  = productStockEditText.getText().toString();

                if(!TextUtils.isEmpty(productName)&&!TextUtils.isEmpty(productBrand)&&!TextUtils.isEmpty(productPrice)&&!TextUtils.isEmpty(productDeliveryPrice)&&!TextUtils.isEmpty(productDescription)&&imageUri!=null){
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

                    final StorageReference ur_firebase_reference  = storageReference.child("images").child( UUID.randomUUID().toString()+ ".jpg");
                    UploadTask image_path = ur_firebase_reference.putBytes(thumbData);

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
                                Toast.makeText(getApplicationContext(), "Successfully uploaded", Toast.LENGTH_SHORT).show();
                                if (downloadUri != null) {

                                    String photoStringLink = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                                  //  Toast.makeText(getApplicationContext(),photoStringLink,Toast.LENGTH_LONG).show();

                                    storeData(photoStringLink,
                                        productName,
                                        productBrand,
                                        productPrice,
                                        productDeliveryPrice,
                                        productStock,
                                        productDescription);
                                }

                            } else {
                                // Handle failures
                                // ...
                            }
                        }
                    });

                }
            }
        });

    }


    private void storeData(String downloadUri, final String  productName, String  productBrand, String productPrice, String productDeliveryPrice, final String productStock, String productDescription) {

        Map<String, String> productData = new HashMap<>();
        productData.put("id",UUID.randomUUID().toString());
        productData.put("productname",productName);
        productData.put("productbrand",productBrand);
        productData.put("productprice",productPrice);
        productData.put("productdeliveryprice",productDeliveryPrice);
        productData.put("productdescription",productDescription);
        productData.put("image",downloadUri);
        productData.put("verified","false");

       // Toast.makeText(getApplicationContext(),downloadUri,Toast.LENGTH_LONG).show();
        selectedCategory  = spinner.getSelectedItem().toString();
        if(selectedCategory.equals("Others")){
            selectedCategory = otherCategory.getText().toString();
        }
        final String productID = UUID.randomUUID().toString();
        db.collection("products").document(selectedCategory).collection(productID).document(productName).set(productData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    Map<String, String> sellerProduct = new HashMap<>();
                    sellerProduct.put("stock",productStock);
                    sellerProduct.put("productname",productName);
                    sellerProduct.put("category",selectedCategory);
                    db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("products").document(productID).set(sellerProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });

                    db.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            sellerName = documentSnapshot.getString("shopname");
                            Map<String, String> seller = new HashMap<>();
                            seller.put("shopname",sellerName );
                            db.collection("products").document(selectedCategory).collection(productID).document(productName).collection("sellers").document(mAuth.getCurrentUser().getUid()).set(seller).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getApplicationContext(), "User Data is Stored Successfully", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            });

                        }
                    });


                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(getApplicationContext(), "(FIRESTORE Error) : " + error, Toast.LENGTH_LONG).show();
                }


            }
        });
    }

    private void choseImage() {


        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(NewProductActivity.this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {


            CropImage.ActivityResult result = CropImage.getActivityResult(data);


            if (resultCode == RESULT_OK) {


                imageUri = result.getUri();


                previewImage.setImageURI(imageUri);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {


                Exception error = result.getError();


            }

        }


    }


}