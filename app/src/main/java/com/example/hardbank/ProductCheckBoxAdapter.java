package com.example.hardbank;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductCheckBoxAdapter  extends FirestoreRecyclerAdapter<Product,ProductCheckBoxAdapter.MyViewHolder> {

    String projectID;
    public ProductCheckBoxAdapter(@NonNull FirestoreRecyclerOptions<Product> options,String projectID) {
        super(options);
        this.projectID =  projectID;
    }

    @Override
    protected void onBindViewHolder(@NonNull final MyViewHolder holder, int position, @NonNull final Product model) {
        holder.productName.setText(model.getProductname());
        Glide.with(holder.productImage.getContext())
                .load(model.getImage())
                .into(holder.productImage);

        FirebaseFirestore.getInstance().collection("sampleprojects").document(projectID)
                .collection("components").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot queryDocumentSnapshots = task.getResult();
                    List<DocumentSnapshot> ids = queryDocumentSnapshots.getDocuments();
                    for (int i = 0; i < ids.size(); i++){
                        DocumentSnapshot doc=ids.get(i);
                        if(doc.getId().equals(model.getId())){
                            holder.productCheckBox.setChecked(true);
                        }
                    }
                }
            }
        });

        holder.productCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    final Map<String, Object> contentData= new HashMap<>();
                    contentData.put("productid",model.getId());
                    contentData.put("image",model.getImage());
                    contentData.put("productname",model.getProductname());
                    FirebaseFirestore.getInstance().collection("sampleprojects").document(projectID)
                            .collection("components").document(model.getId()).set(contentData);
                } else {
                    FirebaseFirestore.getInstance().collection("sampleprojects").document(projectID)
                            .collection("components").document(model.getId()).delete();
                }
            }
        });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_checkbox_single_view,parent,false);
        return new ProductCheckBoxAdapter.MyViewHolder(V);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
       TextView productName;
       ImageView productImage;
       CheckBox productCheckBox;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage =  itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productCheckBox =  itemView.findViewById(R.id.productCheckBox);
        }
    }
}
