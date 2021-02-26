package com.example.hardbank;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

public class SelectedComponentsAdapter extends FirestoreRecyclerAdapter<SelectedComponent, SelectedComponentsAdapter.MyViewHolder> {


    String projectID;

    public SelectedComponentsAdapter(@NonNull FirestoreRecyclerOptions<SelectedComponent> options, String projectID) {
        super(options);
        this.projectID =  projectID;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull final SelectedComponent model) {
        holder.productName.setText(model.getProductname());
        Glide.with(holder.productImage.getContext())
                .load(model.getImage())
                .into(holder.productImage);

        holder.deleteComponentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore.getInstance().collection("sampleprojects")
                        .document(projectID).collection("components")
                        .document(model.getProductid()).delete();
            }
        });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_selected_components_single_view,parent,false);
        return new SelectedComponentsAdapter.MyViewHolder(V);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        ImageView productImage;
        RelativeLayout deleteComponentBtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage =  itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            deleteComponentBtn =  itemView.findViewById(R.id.deleteComponentBtn);
        }
    }
}
