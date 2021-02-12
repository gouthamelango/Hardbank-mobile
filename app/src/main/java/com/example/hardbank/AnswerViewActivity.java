package com.example.hardbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AnswerViewActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    private CollectionReference notebookRef;
    private  CollectionReference productsCollectionReference;

    private AnswersAdapter adapter;
    RecyclerView recyclerView;

    String questionId;

    Dialog answerDialog;

    RelativeLayout emptyAnswer;

    TextView userId;
    TextView questionTextView;
    TextView answerBtn;

    String uuid;

    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_view);

        Intent intent = getIntent();
        if(intent.hasExtra("id")){
            questionId = getIntent().getExtras().getString("id");
           // Toast.makeText(getApplicationContext(),questionId,Toast.LENGTH_SHORT).show();
            //Firebase initialization
            mAuth = FirebaseAuth.getInstance();
            db =  FirebaseFirestore.getInstance();

            //Getting my products RecyclerView
            recyclerView = findViewById(R.id.answersRecyclerView);

            emptyAnswer = findViewById(R.id.emptyAnswer);

            //Reference to user collection
            notebookRef = db.collection("communitysupport").document(questionId).collection("answers");

            userId  = findViewById(R.id.userId);


            questionTextView = findViewById(R.id.questionTextView);
            db.collection("communitysupport").document(questionId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    questionTextView.setText(documentSnapshot.getString("question"));
                    uuid = documentSnapshot.getString("userid");
                    FirebaseFirestore.getInstance().collection("users").document(uuid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            userId.setText(documentSnapshot.getString("fullname"));
                        }
                    });
                }
            });


            answerDialog = new Dialog(this);
            setUpRecyclerView();
            //
            answerBtn = findViewById(R.id.answerBtn);
            answerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   if(mAuth.getCurrentUser()!=null){
                       //Can be cancelable

                       answerDialog.setCancelable(true);

                       //Inflating Dialog view
                       View dia = getLayoutInflater().inflate(R.layout.answer_popup,null);
                       //Setting view
                       answerDialog.setContentView(dia);

                       //initialization of dialog Views
                       final EditText answerEditText  = dia.findViewById(R.id.answerEditText);
                       Button postAnsBtn = dia.findViewById(R.id.answerPostBtn);

                       //Dialog Post Button
                       postAnsBtn.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {
                               if(!answerEditText.getText().toString().isEmpty()){
                                   adapter.stopListening();
                                   Map<String,Object> answerData =  new HashMap<>();
                                   answerData.put("answer",answerEditText.getText().toString());
                                   answerData.put("customerid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                   answerData.put("time", FieldValue.serverTimestamp());
                                   db.collection("communitysupport").document(questionId).collection("answers").document(UUID.randomUUID().toString()).set(answerData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                       @Override
                                       public void onSuccess(Void aVoid) {
                                           answerDialog.dismiss();
                                           check();
                                           adapter.startListening();

                                       }
                                   });
                               }
                               else {
                                   answerEditText.setError("Answer Cannot be empty");
                                   answerEditText.requestFocus();
                                   return;
                               }
                           }
                       });

                       //Showing Dialog
                       answerDialog.show();
                   }
                   else {
                       Intent customerLoginActivity = new Intent(getApplicationContext(),CustomerLoginActivity.class);
                       startActivity(customerLoginActivity);
                       overridePendingTransition(R.anim.bottom_up, R.anim.nothing_ani);
                   }

                }
            });

        }


    }
    public void check(){
        notebookRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (DocumentSnapshot document : task.getResult()) {
                        count++;
                    }
                    if(count==0){
                        emptyAnswer.setVisibility(View.VISIBLE);
                    }
                    else {
                        emptyAnswer.setVisibility(View.GONE);
                    }
                }
            }
        });
    }
    private void setUpRecyclerView() {


        check();

        Query query = notebookRef.orderBy("time",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Answers> options = new FirestoreRecyclerOptions.Builder<Answers>()
                .setQuery(query, Answers.class)
                .build();
        adapter = new AnswersAdapter(options);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //adapter.startListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}