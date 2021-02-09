package com.example.hardbank;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommunityHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommunityHomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    private CollectionReference notebookRef;
    private  CollectionReference productsCollectionReference;

    private QuestionAdapter adapter;
    RecyclerView recyclerView;

    String userID;
    FloatingActionButton fabAskQuestion;


    public CommunityHomeFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CommunityHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommunityHomeFragment newInstance(String param1, String param2) {
        CommunityHomeFragment fragment = new CommunityHomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_community_home, container, false);

        //Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        //Getting my products RecyclerView
        recyclerView = view.findViewById(R.id.askedQuestionsRecyclerView);

        //Reference to user collection
        notebookRef = db.collection("communitysupport");

        //TO get Current seller ID
       // userID = mAuth.getCurrentUser().getUid();


        //FAB
        fabAskQuestion = view.findViewById(R.id.floatingActionAskQuestion);
        fabAskQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAuth.getCurrentUser()!=null){
                    Intent newNoteIntent = new Intent(getActivity().getApplicationContext(), AskQuestionActivity.class);
                    startActivity(newNoteIntent);
                    getActivity().overridePendingTransition(R.anim.bottom_up, R.anim.nothing_ani);
                }
                else {
                    Intent customerLoginActivity = new Intent(getActivity().getApplicationContext(),CustomerLoginActivity.class);
                    startActivity(customerLoginActivity);
                    getActivity().overridePendingTransition(R.anim.bottom_up, R.anim.nothing_ani);
                }
            }
        });

        setUpRecyclerView();

        return view;
    }

    private void setUpRecyclerView() {
        Query query = notebookRef.whereNotEqualTo("question","none");
        FirestoreRecyclerOptions<Question> options = new FirestoreRecyclerOptions.Builder<Question>()
                .setQuery(query, Question.class)
                .build();
        adapter = new QuestionAdapter(options);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new QuestionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String id  = documentSnapshot.getId();
                Intent intent =  new Intent(getActivity().getApplicationContext(),AnswerViewActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });

        adapter.startListening();
    }

}