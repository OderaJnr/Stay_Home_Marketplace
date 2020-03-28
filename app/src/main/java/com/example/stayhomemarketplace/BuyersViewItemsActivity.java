package com.example.stayhomemarketplace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class BuyersViewItemsActivity extends AppCompatActivity  {
    private DatabaseReference ItemsRef;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    private Button orderbtn;
    private TextView TotalpricetextView;
    private  String  Quantity,productid,productname,productprice,vendorname,vendorID;
    private int TotalPriceofanItem;

    private String currentUser;
    private String Username,UserPhonenumber;
    private FirebaseAuth mAuth;
    private android.widget.LinearLayout LinearLayout;
    private  int overTotalPrice = 0;
    String VendorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyers_view_items);


        ItemsRef = FirebaseDatabase.getInstance().getReference().child("All Products");

        recyclerView=  findViewById(R.id.restaurantsmenu);
        recyclerView.setLayoutManager(new LinearLayoutManager(BuyersViewItemsActivity.this));

        mAuth= FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser().getUid();

        progressDialog =new ProgressDialog(this);


        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(BuyersViewItemsActivity.this,LinearLayoutManager.VERTICAL, false );
        recyclerView.setLayoutManager(layoutManager);
        progressDialog =new ProgressDialog(BuyersViewItemsActivity.this);



        Button ConfirmOrder = findViewById(R.id.confirmordere);
        ConfirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Confirm_Buyer_Order();
            }
        });



    }

    private void Confirm_Buyer_Order() {





        progressDialog.setTitle("Processing your order");
        progressDialog.setMessage("please wait..");
        progressDialog.show();



        final String saveCurrentDate,saveCurrentTime,OrderID;


        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate = currentDate.format(callForDate.getTime());


        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(callForTime.getTime());

        OrderID= saveCurrentDate+saveCurrentTime;



        final DatabaseReference OrderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child("Bookings");

        final HashMap<String, Object> ordermap = new HashMap<>();
        ordermap.put("bookingID",OrderID);
        ordermap.put("date",saveCurrentDate);
        ordermap.put("time",saveCurrentTime);
        ordermap.put("vendorName",vendorname);
        ordermap.put("userName",Username);
        ordermap.put("vendorID",VendorId);
        ordermap.put("Userphone",UserPhonenumber);
        ordermap.put("BuyerId",currentUser);

        ordermap.put("TotalAmount",String.valueOf(overTotalPrice));



        OrderRef.child("User view").child(currentUser).child("Bookings").child(OrderID).updateChildren(ordermap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            OrderRef.child("Vendor view").child(VendorId).child("Bookings").child(OrderID).updateChildren(ordermap);
                            Toast.makeText(BuyersViewItemsActivity.this, "Items ordered  successfully please pay to complete the order ", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(BuyersViewItemsActivity.this,OrderPaymentActivity.class);
                            startActivity(intent);

                            progressDialog.dismiss();

                        }
                        else
                        {
                            Toast.makeText(BuyersViewItemsActivity.this, "Error Occurred Please Try Again", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }

                    }
                });


    }






    @Override
    public void onStart() {
        super.onStart();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("sellerID");

            VendorId = value;
        }

        progressDialog.setTitle("Refreshing");
        progressDialog.setMessage("please wait..");
        progressDialog.show();


        FirebaseRecyclerOptions<Itemslisted> options =
                new FirebaseRecyclerOptions.Builder<Itemslisted>()
                        .setQuery(ItemsRef.child(VendorId),Itemslisted.class)
                        .build();



        FirebaseRecyclerAdapter<Itemslisted, Itemsviewholder> adapter =new FirebaseRecyclerAdapter<Itemslisted, Itemsviewholder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull final Itemsviewholder holder, int position, @NonNull final Itemslisted model)
            {
                holder.Itemname.setText(model.getPname());
                holder.ItemDescription.setText(model.getDescription());
                holder.ItemPrice.setText(model.getPrice());
                Picasso.get().load(model.getImage()).into(holder.ItemImage);

                progressDialog.dismiss();





                holder.Addtocartbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        progressDialog.setTitle("Adding this product");
                        progressDialog.setMessage("please wait..");
                        progressDialog.show();


                        String quantityfromnumber = holder.elegantNumberButton.getNumber();
                        Quantity = quantityfromnumber;

                        int convertedquantity = Integer.parseInt(quantityfromnumber);


                        if ( convertedquantity>=1)
                        {

                            //Calculating Total Price of an item

                            int OneitemPrice = Integer.parseInt(model.getPrice());
                            int quantity = Integer.parseInt(Quantity);

                            int Totalprice = OneitemPrice *quantity;
                            TotalPriceofanItem = Totalprice;

                            overTotalPrice = overTotalPrice + Totalprice;


                            final DatabaseReference cartlist = FirebaseDatabase.getInstance().getReference().child("Orders").child("CartList");



                            final HashMap<String, Object> cartmap = new HashMap<>();
                            cartmap.put("pid",model.getPid());
                            cartmap.put("pname",model.getPname());
                            cartmap.put("price",model.getPrice());
                            cartmap.put("quantity",Quantity);





                            cartlist.child("User view").child(currentUser).child(model.getPid()).child("Products").updateChildren(cartmap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                cartlist.child("Vendor view").child(VendorId).child(model.getPid()).child("Products").updateChildren(cartmap);
                                                Toast.makeText(BuyersViewItemsActivity.this, "Product selected successfully ", Toast.LENGTH_SHORT).show();
                                                holder.Addtocartbtn.setText("Added");

                                                progressDialog.dismiss();

                                            }
                                            else
                                            {
                                                progressDialog.dismiss();

                                                Toast.makeText(BuyersViewItemsActivity.this, "Error Occurred Please Try Again", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });


                        } else{
                            Toast.makeText(BuyersViewItemsActivity.this, "Select quantity", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }
                    }
                });

            }

            @NonNull
            @Override
            public Itemsviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.items_layout,viewGroup, false);
                Itemsviewholder holder = new Itemsviewholder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();



    }






}
