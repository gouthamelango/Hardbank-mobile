package com.example.hardbank;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

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
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

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

                    for (int i = 0 ; i<components.size();i++){
                        FirebaseFirestore.getInstance().collection("products").document(components.get(i).productid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                holder.total = holder.total + Integer.parseInt(documentSnapshot.get("productprice").toString());
                                    holder.projectPrice.setText(String.valueOf(holder.total));
                            }
                        });
                    }


                }
            }
        });

        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final String id  = list.get(position).getProjectid();
                AlertDialog alertDialog = new AlertDialog.Builder(view.getRootView().getContext()).create();
                alertDialog.setTitle("Remove Project");
                alertDialog.setMessage("Are you sure you want to Remove?");
                alertDialog.setCancelable(false);
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Toast.makeText(getActivity().getApplicationContext(),"Deleted",Toast.LENGTH_LONG).show();
                                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .collection("projectscart").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(view.getContext(),"Project Removed",Toast.LENGTH_SHORT).show();
                                        notifyItemRemoved(position);
                                        list.remove(position);
                                    }
                                });
                            }
                        });
                alertDialog.show();
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
        TextView removeBtn;
        int total = 0;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            projectName = itemView.findViewById(R.id.projectTitle);
            projectImage = itemView.findViewById(R.id.projectImage);
            projectPrice = itemView.findViewById(R.id.projectPrice);
            recyclerView =  itemView.findViewById(R.id.componentsRecyclerView);
            removeBtn = itemView.findViewById(R.id.removeBtn);
        }
    }
}
