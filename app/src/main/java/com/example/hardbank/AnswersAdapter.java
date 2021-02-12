package com.example.hardbank;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.Date;

public class AnswersAdapter extends FirestoreRecyclerAdapter<Answers,AnswersAdapter.MyAnswerHolder> {

    String name;

    public AnswersAdapter(@NonNull FirestoreRecyclerOptions<Answers> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final MyAnswerHolder holder, int position, @NonNull Answers model) {
        holder.answerTextView.setText(model.getAnswer());

//
        FirebaseFirestore.getInstance().collection("users").document(model.getCustomerid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
               // Toast.makeText(holder.customerNameTextView.getContext(),documentSnapshot.getString("type"),Toast.LENGTH_SHORT).show();
                holder.customerNameTextView.setText(documentSnapshot.getString("fullname"));
            }
        });

        Date date = model.getTime().toDate();
        holder.time.setText(date.toString());
    }

    @NonNull
    @Override
    public MyAnswerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_single_view,parent,false);
        return new AnswersAdapter.MyAnswerHolder(V);
    }

    class MyAnswerHolder extends RecyclerView.ViewHolder{

        TextView answerTextView;
        TextView customerNameTextView;
        TextView time;

        public MyAnswerHolder(View itemView){
            super(itemView);
            answerTextView = itemView.findViewById(R.id.answerTextView);
            customerNameTextView = itemView.findViewById(R.id.customerNameTextView);
            time  = itemView.findViewById(R.id.time);
        }
    }
}
