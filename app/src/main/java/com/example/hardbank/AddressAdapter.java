package com.example.hardbank;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import org.w3c.dom.Text;

public class AddressAdapter extends FirestoreRecyclerAdapter<AddressModel,AddressAdapter.MyViewHolder> {
    private OnDeleteClickListener listenerDelete;
    private  OnEditClickListener listenerEdit;

    public AddressAdapter(@NonNull FirestoreRecyclerOptions<AddressModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull AddressModel model) {
        holder.textViewName.setText(model.getName());
        holder.textViewPhone.setText(model.getPhone());
        holder.textViewAddress.setText(model.getAddress());

//        holder.editBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent  =  new Intent(view.getContext(),AddAddressActivity.class);
//                intent.putExtra("editId",);
//                view.getContext().startActivity(intent);
//            }
//        });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_single_view,parent,false);
        return new AddressAdapter.MyViewHolder(V);
    }

    class  MyViewHolder extends RecyclerView.ViewHolder{

        TextView textViewName;
        TextView textViewPhone;
        TextView textViewAddress;

        TextView deleteBtn;
        TextView editBtn;

        public MyViewHolder(View itemView){
            super(itemView);
            textViewName =  itemView.findViewById(R.id.textViewName);
            textViewPhone =  itemView.findViewById(R.id.textViewPhone);
            textViewAddress =  itemView.findViewById(R.id.textViewAddress);

            deleteBtn =  itemView.findViewById(R.id.deleteBtn);
            editBtn =  itemView.findViewById(R.id.editBtn);


            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position  = getAdapterPosition();
                    if(position!= RecyclerView.NO_POSITION && listenerDelete != null){
                        listenerDelete.onDeleteClick(getSnapshots().getSnapshot(position),position);
                    }
                }
            });

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position  = getAdapterPosition();
                    if(position!= RecyclerView.NO_POSITION && listenerEdit != null){
                        listenerEdit.onEditClick(getSnapshots().getSnapshot(position),position);
                    }
                }
            });
        }
    }

    //Reject
    public  interface  OnDeleteClickListener{
        void onDeleteClick(DocumentSnapshot documentSnapshot, int position);
    }
    public  void setOnDeleteClickListener(AddressAdapter.OnDeleteClickListener listener){
        this.listenerDelete = listener;
    }


    //Edit
    public  interface  OnEditClickListener{
        void onEditClick(DocumentSnapshot documentSnapshot, int position);
    }
    public  void setOnEditClickListener(AddressAdapter.OnEditClickListener listener){
        this.listenerEdit = listener;
    }

}
