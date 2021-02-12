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
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import id.zelory.compressor.Compressor;

public class SellerSignUpActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword, editTextName, editTextShopName, editTextShopAddress;
    Button signUpBtn;
    CheckBox termsCheckBox;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String userId;
    ImageView preview;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;

    private Bitmap compressed;

    Button btnSelect;
    private Uri imageUri = null;

    TextView signInText;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{4,}" +               //at least 4 characters
                    "$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_sign_up);

        //Initialization
        editTextEmail = (EditText) findViewById(R.id.signUpEmailEditText);
        editTextPassword = (EditText) findViewById(R.id.signUpPasswordEditText);
        editTextName = (EditText)findViewById(R.id.signUpNameEditText);
        editTextShopName = (EditText)findViewById(R.id.signUpShopNameEditText);
        editTextShopAddress = (EditText)findViewById(R.id.signUpShopAddressEditText);
        signUpBtn  = (Button) findViewById(R.id.sellerSignUpButton);
        termsCheckBox =  (CheckBox)findViewById(R.id.termsCheckBox);
        btnSelect = findViewById(R.id.chooseDoc);
        preview = findViewById(R.id.selectedImage);

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Firebase Auth initializing instance
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        //sign In
        signInText  = findViewById(R.id.signInText);
        signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent signInActivityIntent  = new Intent(getApplicationContext(),SellerLoginActivity.class);
                startActivity(signInActivityIntent);
            }
        });

        //sign Up
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(termsCheckBox.isChecked()){
                    registerUser();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Please accept our Terms and Condition",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Btn to choose doc
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(SellerSignUpActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        choseImage();
                    }
                } else {
                    choseImage();
                }
            }
        });

    }
    private void choseImage() {
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(SellerSignUpActivity.this);

    }
    @Override
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

    private void registerUser() {
        //Storing Edit Text values in string variables
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String name  =  editTextName.getText().toString().trim();
        final String shopName  =  editTextShopName.getText().toString().trim();
        final String shopAddress = editTextShopAddress.getText().toString().trim();

        //Validation
        if(name.isEmpty()){
            editTextName.setError("Name is required");
            editTextName.requestFocus();
            return;
        }
        if(name.length()<2){
            editTextName.setError("Name should be at least 2 Characters");
            editTextName.requestFocus();
            return;
        }
        if(shopName.isEmpty()){
            editTextShopName.setError("ShopName is required");
            editTextShopName.requestFocus();
            return;
        }
        if(shopAddress.isEmpty()){
            editTextShopAddress.setError("Shop Address is required");
            editTextShopAddress.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 8) {
            editTextPassword.setError("Minimum length of password should be 8");
            editTextPassword.requestFocus();
            return;
        }
        if (!PASSWORD_PATTERN.matcher(password).matches() ){
            editTextPassword.setError("Password Must Contain one digit from 0-9, one lowercase character, one uppercase character, one special symbols [@#$%] ");
            editTextPassword.requestFocus();
            return;
        }
        if(imageUri==null){
            Toast.makeText(getApplicationContext(),"Choose Supporting Doc Image",Toast.LENGTH_SHORT).show();
            return;
        }

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

                        createUser(name,shopName,email,password,photoStringLink,shopAddress);
                    }

                } else {
                    // Handle failures
                    // ...
                }
            }
        });





    }
    public  void  createUser(final String name, final String shopName, final String email, String password, final String downloadUri, final String shopAddress){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    userId  = mAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = db.collection("users").document(userId);
                    Map<String,Object> user =  new HashMap<>();
                    user.put("id",userId);
                    user.put("fullname",name);
                    user.put("email",email);
                    user.put("type","seller");
                    user.put("shopname",shopName);
                    user.put("verified","false");
                    user.put("address",shopAddress);
                    user.put("doc",downloadUri);
                    user.put("comment","none");
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("TAG","OnSuccess");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TAG","OnFailure");
                        }
                    });
                    finish();
                    Intent intent = new Intent(getApplicationContext(), SellerHome.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(),"Registered",Toast.LENGTH_SHORT).show();
                } else {

                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }
}