package com.example.hardbank;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Toolbar toolbar;

    CarouselView carouselView;

    int[] sampleImages = {R.drawable.dummycomponentimage,R.drawable.dummycomponentimage,R.drawable.dummycomponentimage,R.drawable.dummycomponentimage,R.drawable.dummycomponentimage};
    List<String> images = new ArrayList<>();


    FirebaseAuth mAuth;
    FirebaseFirestore db;

    private HomeProductAdapter adapter;
    RecyclerView recyclerView;
    RecyclerView recyclerViewTools;
    RecyclerView recyclerViewCables;

    TextView sensorMoreBtn, toolsMoreBtn, cablesMoreBtn;

    List<String> productsID = new ArrayList<>();

    Context context;

    List<Product> products =new ArrayList<>();
    List<Product> productsTools =new ArrayList<>();
    List<Product> productsCables =new ArrayList<>();

    RelativeLayout motorBtn, displayBtn, batteryBtn, toolsBtn, connectorBtn;

    EditText searchEditText;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        searchEditText =  view.findViewById(R.id.searchEditText);
        //Home ToolBar
        toolbar =  view.findViewById(R.id.toolBar);
        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(getActivity().getApplicationContext(),"Tapped",Toast.LENGTH_LONG).show();

                Intent intent =  new Intent(getActivity().getApplicationContext(),MenuActivity.class);
                startActivity(intent);
            }
        });

        images.add("https://whatis6g.com/wp-content/uploads/2020/08/internet-of-senses-inset-1080x675.jpg");
        images.add("https://searchengineland.com/figz/wp-content/seloads/2017/10/internet-of-things-smart-ss-1920.gif");
        images.add("https://www.mobifilia.com/wp-content/uploads/2018/11/IoT-for-Healthcare.jpg");
        images.add("https://innovationatwork.ieee.org/wp-content/uploads/2017/06/bigstock-185933284.jpg");

        //CarouselView
        carouselView = (CarouselView) view.findViewById(R.id.carousel);
        carouselView.setPageCount(images.size());
        carouselView.setImageListener(imageListener);
        carouselView.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(getActivity().getApplicationContext(),String.valueOf(position),Toast.LENGTH_SHORT).show();

            }
        });

        //Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();

        //Getting my products RecyclerView for sensors
        recyclerView = view.findViewById(R.id.sensorRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(mLayoutManager);

        //Getting my products RecyclerView for tools
        recyclerViewTools = view.findViewById(R.id.toolsRecyclerView);
        RecyclerView.LayoutManager mLayoutManagerTools = new LinearLayoutManager(this.getActivity(),LinearLayoutManager.HORIZONTAL,false);
        recyclerViewTools.setLayoutManager(mLayoutManagerTools);

        //Getting my products RecyclerView for cables
        recyclerViewCables = view.findViewById(R.id.cablesRecyclerView);
        RecyclerView.LayoutManager mLayoutManagerCables = new LinearLayoutManager(this.getActivity(),LinearLayoutManager.HORIZONTAL,false);
        recyclerViewCables.setLayoutManager(mLayoutManagerCables);


        //Category Listeners
        motorBtn  = view.findViewById(R.id.motorBtn);
        motorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCategory("Motors");
            }
        });

        displayBtn = view.findViewById(R.id.displayBtn);
        displayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCategory("Display");
            }
        });

        batteryBtn = view.findViewById(R.id.batteryBtn);
        batteryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCategory("Battery");
            }
        });

        toolsBtn = view.findViewById(R.id.toolsBtn);
        toolsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCategory("Tools");
            }
        });

        connectorBtn = view.findViewById(R.id.connectorBtn);
        connectorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCategory("Connectors");
            }
        });

        //More Button's

        //Sensor More btn
        sensorMoreBtn =  view.findViewById(R.id.sensorMoreBtn);
        sensorMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(getActivity().getApplicationContext(),"Sensors",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity().getApplicationContext(),ProductListingActivity.class);
                intent.putExtra("type","Sensors");
                startActivity(intent);
            }
        });
        //Tools More Btn
        toolsMoreBtn = view.findViewById(R.id.toolsMoreBtn);
        toolsMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity().getApplicationContext(),"Tools",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity().getApplicationContext(),ProductListingActivity.class);
                intent.putExtra("type","Tools");
                startActivity(intent);
            }
        });
        //Cables More Btn
        cablesMoreBtn = view.findViewById(R.id.cablesMoreBtn);
        cablesMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(getActivity().getApplicationContext(),"Cables",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity().getApplicationContext(),ProductListingActivity.class);
                intent.putExtra("type","Cables");
                startActivity(intent);
            }
        });

        context  = getActivity().getApplicationContext();


        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

        setUpRecyclerView();
        setUpRecyclerViewForTools();
        setUpRecyclerViewForCables();
        return view;
    }

    public  void performSearch(){

        String searchText = searchEditText.getText().toString().trim();
        if(!searchText.isEmpty()){
            searchEditText.clearFocus();
           // Toast.makeText(getActivity().getApplicationContext(),searchText,Toast.LENGTH_SHORT).show();
            InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
            Intent intent = new Intent(getActivity().getApplicationContext(),ProductListingActivity.class);
            intent.putExtra("product",searchText);
            startActivity(intent);
        }

    }

    private void setUpRecyclerView(){
        db.collection("products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot queryDocumentSnapshots = task.getResult();
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (int i = 0; i < list.size(); i++) {
                        DocumentSnapshot doc=list.get(i);
                        if(doc.getString("verified").equals("true")){
                            if(doc.getString("category").equals("Sensors")){
                                //Toast.makeText(getActivity().getApplicationContext(),doc.getString("productname"),Toast.LENGTH_SHORT).show();
                                String productname = doc.getString("productname");
                                int productprice = doc.getLong("productprice").intValue();
                                //Toast.makeText(getApplicationContext(),productname,Toast.LENGTH_SHORT).show();
                                String category = doc.getString("category");
                                String id = doc.getString("id");
                                String image = doc.getString("image");
                                String productbrand = doc.getString("productbrand");
                                String productdeliveryprice = doc.getString("productdeliveryprice");
                                String productdescription = doc.getString("productdescription");
                                String  reason = doc.getString("reason");
                                String verified = doc.getString("verified");
                                Product product = new Product(productname, productprice,category,id, image,productbrand,
                                        productdeliveryprice, productdescription, verified, reason);
                                products.add(product);
                                adapter = new HomeProductAdapter(context,products);
                                //Toast.makeText(getActivity().getApplicationContext(),product.getImage(),Toast.LENGTH_SHORT).show();
                                recyclerView.setAdapter(adapter);
                            }
                        }

                    }

                }
            }
        });
    }
    private void setUpRecyclerViewForTools(){
        db.collection("products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot queryDocumentSnapshots = task.getResult();
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (int i = 0; i < list.size(); i++) {
                        DocumentSnapshot doc=list.get(i);
                        if(doc.getString("verified").equals("true")){
                            if(doc.getString("category").equals("Tools")){
                                //Toast.makeText(getActivity().getApplicationContext(),doc.getString("productname"),Toast.LENGTH_SHORT).show();
                                String productname = doc.getString("productname");
                                int productprice = doc.getLong("productprice").intValue();
                                //Toast.makeText(getApplicationContext(),productname,Toast.LENGTH_SHORT).show();
                                String category = doc.getString("category");
                                String id = doc.getString("id");
                                String image = doc.getString("image");
                                String productbrand = doc.getString("productbrand");
                                String productdeliveryprice = doc.getString("productdeliveryprice");
                                String productdescription = doc.getString("productdescription");
                                String  reason = doc.getString("reason");
                                String verified = doc.getString("verified");
                                Product product = new Product(productname, productprice,category,id, image,productbrand,
                                        productdeliveryprice, productdescription, verified, reason);
                                productsTools.add(product);
                                adapter = new HomeProductAdapter(context,productsTools);
                                //Toast.makeText(getActivity().getApplicationContext(),product.getImage(),Toast.LENGTH_SHORT).show();
                                recyclerViewTools.setAdapter(adapter);
                            }
                        }

                    }

                }
            }
        });
    }
    private void setUpRecyclerViewForCables(){
        db.collection("products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot queryDocumentSnapshots = task.getResult();
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (int i = 0; i < list.size(); i++) {
                        DocumentSnapshot doc=list.get(i);
                        if(doc.getString("verified").equals("true")){
                            if(doc.getString("category").equals("Cables")){
                                //Toast.makeText(getActivity().getApplicationContext(),doc.getString("productname"),Toast.LENGTH_SHORT).show();
                                String productname = doc.getString("productname");
                                int productprice = doc.getLong("productprice").intValue();
                                //Toast.makeText(getApplicationContext(),productname,Toast.LENGTH_SHORT).show();
                                String category = doc.getString("category");
                                String id = doc.getString("id");
                                String image = doc.getString("image");
                                String productbrand = doc.getString("productbrand");
                                String productdeliveryprice = doc.getString("productdeliveryprice");
                                String productdescription = doc.getString("productdescription");
                                String  reason = doc.getString("reason");
                                String verified = doc.getString("verified");
                                Product product = new Product(productname, productprice,category,id, image,productbrand,
                                        productdeliveryprice, productdescription, verified, reason);
                                productsCables.add(product);
                                adapter = new HomeProductAdapter(context,productsCables);
                                //Toast.makeText(getActivity().getApplicationContext(),product.getImage(),Toast.LENGTH_SHORT).show();
                                recyclerViewCables.setAdapter(adapter);
                            }
                        }

                    }

                }
            }
        });
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            //imageView.setImageResource(sampleImages[position]);

                Glide.with(imageView.getContext())
                        .load(images.get(position))
                        .into(imageView);

        }
    };

    public  void viewCategory(String type){
        Intent intent = new Intent(getActivity().getApplicationContext(),ProductListingActivity.class);
        intent.putExtra("type",type);
        startActivity(intent);
    }
}