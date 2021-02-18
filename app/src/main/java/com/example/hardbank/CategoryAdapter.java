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

public class CategoryAdapter extends FirestoreRecyclerAdapter<Category,CategoryAdapter.CategoryHolder> {

    private CategoryAdapter.OnItemClickListener listener;

    public CategoryAdapter(@NonNull FirestoreRecyclerOptions<Category> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CategoryHolder holder, int position, @NonNull Category model) {
        holder.categoryItemTextView.setText(model.getCatname());

    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_single,parent,false);
        return new CategoryHolder(V);
    }


    class  CategoryHolder extends RecyclerView.ViewHolder{

        TextView categoryItemTextView;

        public CategoryHolder(@NonNull View itemView) {
            super(itemView);
            categoryItemTextView = itemView.findViewById(R.id.categoryItemTextView);

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
    public void  setOnItemClickListener(CategoryAdapter.OnItemClickListener listener){
        this.listener = listener;
    }
}
