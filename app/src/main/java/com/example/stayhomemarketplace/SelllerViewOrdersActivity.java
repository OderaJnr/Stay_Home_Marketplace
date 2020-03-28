package com.example.stayhomemarketplace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stayhomemarketplace.model.Itemslisted;
import com.example.stayhomemarketplace.viewholder.Itemsviewholder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SelllerViewOrdersActivity extends AppCompatActivity {

    private DatabaseReference OrdersRef;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;


    private String currentUser;
    private FirebaseAuth mAuth;
    String VendorId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selller_view_orders);



        OrdersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child("Bookings").child("Vendor view");

        recyclerView=  findViewById(R.id.viewordersrecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(SelllerViewOrdersActivity.this));

        mAuth= FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser().getUid();

        progressDialog =new ProgressDialog(this);


        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(SelllerViewOrdersActivity.this,LinearLayoutManager.VERTICAL, false );
        recyclerView.setLayoutManager(layoutManager);
        progressDialog =new ProgressDialog(SelllerViewOrdersActivity.this);


    }









    @Override
    public void onStart() {
        super.onStart();



        progressDialog.setTitle("Refreshing");
        progressDialog.setMessage("please wait..");
        progressDialog.show();


        FirebaseRecyclerOptions<Itemslisted> options =
                new FirebaseRecyclerOptions.Builder<Itemslisted>()
                        .setQuery(OrdersRef.child(currentUser).child("Bookings"),Itemslisted.class)
                        .build();



        FirebaseRecyclerAdapter<Itemslisted, Itemsviewholder> adapter =new FirebaseRecyclerAdapter<Itemslisted, Itemsviewholder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull final Itemsviewholder holder, int position, @NonNull final Itemslisted model)
            {
               holder.DispatchOrder.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       Intent intent = new Intent(SelllerViewOrdersActivity.this,LocateRidersMapsActivity.class);
                       startActivity(intent);
                   }
               });

                progressDialog.dismiss();



            }

            @NonNull
            @Override
            public Itemsviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_order_layout,viewGroup, false);
                Itemsviewholder holder = new Itemsviewholder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();



    }








}
