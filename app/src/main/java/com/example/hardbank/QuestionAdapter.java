package com.example.hardbank;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class QuestionAdapter extends FirestoreRecyclerAdapter<Question,QuestionAdapter.MyQuestionHolder> {


    private QuestionAdapter.OnItemClickListener listener;


    public QuestionAdapter(@NonNull FirestoreRecyclerOptions<Question> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyQuestionHolder holder, int position, @NonNull Question model) {
        holder.textViewQuestion.setText(model.getQuestion());
    }

    @NonNull
    @Override
    public MyQuestionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_single_view,parent,false);
        return new QuestionAdapter.MyQuestionHolder(V);
    }

    class MyQuestionHolder extends RecyclerView.ViewHolder{
        TextView textViewQuestion;
        public MyQuestionHolder(View itemView){
            super(itemView);
            textViewQuestion = itemView.findViewById(R.id.textViewQuestion);

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
