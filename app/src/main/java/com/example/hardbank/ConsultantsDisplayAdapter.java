package com.example.hardbank;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import org.w3c.dom.Text;

public class ConsultantsDisplayAdapter extends FirestoreRecyclerAdapter<Consultant,ConsultantsDisplayAdapter.MyViewHolder> {



    public ConsultantsDisplayAdapter(@NonNull FirestoreRecyclerOptions<Consultant> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Consultant model) {
        holder.textViewName.setText(model.getFullname());
        holder.textViewEmail.setText(model.getEmail());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.consultant_display_single_view,parent,false);
        return new ConsultantsDisplayAdapter.MyViewHolder(V);
    }

    class  MyViewHolder extends RecyclerView.ViewHolder{

        TextView textViewName;
        TextView textViewEmail;
        public MyViewHolder(View itemView){
            super(itemView);
            textViewEmail =  itemView.findViewById(R.id.textViewEmail);
            textViewName =  itemView.findViewById(R.id.textViewName);
        }
    }
}
