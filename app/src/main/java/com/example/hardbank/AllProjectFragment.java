package com.example.hardbank;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllProjectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllProjectFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView allProjectsRecyclerView;
    AdminProjectAdapter adapter;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    public AllProjectFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AllProjectFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AllProjectFragment newInstance(String param1, String param2) {
        AllProjectFragment fragment = new AllProjectFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_project, container, false);
        //Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        allProjectsRecyclerView = view.findViewById(R.id.allProjectsRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        allProjectsRecyclerView.setLayoutManager(mLayoutManager);



        setUpRecyclerView();

        return  view;
    }
    public  void setUpRecyclerView(){
        Query query = db.collection("sampleprojects").whereNotEqualTo("title","empty");
        FirestoreRecyclerOptions<SampleProject> options = new FirestoreRecyclerOptions.Builder<SampleProject>()
                .setQuery(query, SampleProject.class)
                .build();
        adapter = new AdminProjectAdapter(options,"customer");

        allProjectsRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}