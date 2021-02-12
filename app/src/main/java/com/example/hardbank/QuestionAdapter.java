package com.example.hardbank;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class QuestionAdapter extends FirestoreRecyclerAdapter<Question,QuestionAdapter.MyQuestionHolder> {


    private QuestionAdapter.OnItemClickListener listener;


    public QuestionAdapter(@NonNull FirestoreRecyclerOptions<Question> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final MyQuestionHolder holder, int position, @NonNull Question model) {
        holder.textViewQuestion.setText(model.getQuestion());
        FirebaseFirestore.getInstance().collection("users").document(model.getUserid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // Toast.makeText(holder.customerNameTextView.getContext(),documentSnapshot.getString("type"),Toast.LENGTH_SHORT).show();
                holder.textViewUserID.setText(documentSnapshot.getString("fullname"));
            }
        });
        Date date = model.getTime().toDate();
        holder.textViewTime.setText(date.toString());
    }

    @NonNull
    @Override
    public MyQuestionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_single_view,parent,false);
        return new QuestionAdapter.MyQuestionHolder(V);
    }

    class MyQuestionHolder extends RecyclerView.ViewHolder{
        TextView textViewQuestion;
        TextView textViewUserID;
        TextView textViewTime;
        public MyQuestionHolder(View itemView){
            super(itemView);
            textViewQuestion = itemView.findViewById(R.id.textViewQuestion);
            textViewUserID = itemView.findViewById(R.id.userId);
            textViewTime = itemView.findViewById(R.id.time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position  = getAdapterPosition();
                    if(position!= RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);
                    }
                }
            });
        }
    }
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void  setOnItemClickListener(QuestionAdapter.OnItemClickListener listener){
        this.listener = listener;
    }
}
