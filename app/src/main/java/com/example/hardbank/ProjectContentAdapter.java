package com.example.hardbank;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
//
public class ProjectContentAdapter  extends RecyclerView.Adapter<ProjectContentAdapter.MyViewHolder> {

    Context context;
    List<ProjectContent> list;

    public ProjectContentAdapter(Context context,List<ProjectContent> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view= LayoutInflater.from(parent.getContext()).inflate(R.layout.project_content_single_view,parent,false);
        return new ProjectContentAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        FirebaseFirestore.getInstance().collection("sampleprojects")
                .document(list.get(position).getProjectid()).collection("contents")
                .document(list.get(position).getContentid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(!documentSnapshot.getString("header").equals("empty")) {
                    holder.editTextHeader.setText(documentSnapshot.getString("header"));
                }
                if(!documentSnapshot.getString("content").equals("empty")) {
                    holder.editTextDescription.setText(documentSnapshot.getString("content"));
                }

            }
        });
        holder.editTextDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                list.get(position).setHeader(charSequence.toString());
                FirebaseFirestore.getInstance().collection("sampleprojects")
                        .document(list.get(position).getProjectid()).collection("contents")
                        .document(list.get(position).getContentid()).update("content",charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        holder.editTextHeader.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                list.get(position).setHeader(charSequence.toString());
                FirebaseFirestore.getInstance().collection("sampleprojects")
                        .document(list.get(position).getProjectid()).collection("contents")
                        .document(list.get(position).getContentid()).update("header",charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        EditText editTextHeader;
        EditText editTextDescription;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            editTextHeader  = itemView.findViewById(R.id.editTextHeader);
            editTextDescription =  itemView.findViewById(R.id.editTextDescription);
        }
    }
}

//public class ProjectContentAdapter extends FirestoreRecyclerAdapter<ProjectContent,ProjectContentAdapter.MyViewHolder>{
//
//
//
//    public ProjectContentAdapter(@NonNull FirestoreRecyclerOptions<ProjectContent> options) {
//        super(options);
//    }
//
//    @Override
//    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull final ProjectContent model) {
//        holder.editTextHeader.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                    model.setHeader(charSequence.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
//    }
//
//    @NonNull
//    @Override
//    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_content_single_view,parent,false);
//        return new ProjectContentAdapter.MyViewHolder(V);
//    }
//
//    public class MyViewHolder extends RecyclerView.ViewHolder {
//        EditText editTextHeader;
//        EditText editTextDescription;
//        public MyViewHolder(@NonNull View itemView) {
//            super(itemView);
//            editTextHeader  = itemView.findViewById(R.id.editTextHeader);
//            editTextDescription =  itemView.findViewById(R.id.editTextDescription);
//        }
//    }
//
//}