package com.example.hardbank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CartProjectAdapter extends RecyclerView.Adapter<CartProjectAdapter.MyViewHolder>  {

    Context context;
    List<SampleProject> list;

    CartComponentAdapter adapter;


    public CartProjectAdapter(Context context,List<SampleProject> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_project_single_view,parent,false);
        return new CartProjectAdapter.MyViewHolder(V);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        holder.projectName.setText(list.get(position).getTitle());
        Glide.with(holder.projectImage.getContext())
                .load(list.get(position).getImage())
                .into(holder.projectImage);
        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder.projectImage.getContext()));

        FirebaseFirestore.getInstance().collection("sampleprojects")
                .document(list.get(position).getProjectid()).collection("components")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    final List<SelectedComponent> components =new ArrayList<>();
                    QuerySnapshot queryDocumentSnapshots = task.getResult();
                    final List<DocumentSnapshot> documentList = queryDocumentSnapshots.getDocuments();
                    for (int i = 0; i < documentList.size(); i++) {
                        DocumentSnapshot doc=documentList.get(i);
                       // Toast.makeText(holder.projectImage.getContext(),doc.getId(),Toast.LENGTH_SHORT).show();
                        String image = doc.getString("image");
                        String productid = doc.getString("productid");
                        String productname = doc.getString("productname");
                        SelectedComponent selectedComponent =  new SelectedComponent(image,productid,productname);
                        components.add(selectedComponent);
                        adapter = new CartComponentAdapter(context,components);
                        holder.recyclerView.setAdapter(adapter);
                    }

//                    for (int i = 0 ; i<components.size();i++){
//                        FirebaseFirestore.getInstance().collection("products").document(components.get(i).productid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                            @Override
//                            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                int total = 0;
//                                 total = total + Integer.parseInt(documentSnapshot.get("productprice").toString());
//                                    holder.projectPrice.setText(String.valueOf(total));
//                            }
//                        });
//                    }


                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder{

        TextView projectName;
        ImageView projectImage;
        TextView projectPrice;
        RecyclerView recyclerView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            projectName = itemView.findViewById(R.id.projectTitle);
            projectImage = itemView.findViewById(R.id.projectImage);
            projectPrice = itemView.findViewById(R.id.projectPrice);
            recyclerView =  itemView.findViewById(R.id.componentsRecyclerView);

        }
    }
}