package com.example.stayhomemarketplace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class SellersAddProducts extends AppCompatActivity {

    private Button AddNewproductButton;
    private ImageView selectproductimage;
    private EditText Inputproductname,inputProductDescription,inputproductPrice;


    private static final int Gallery_Pick = 1;

    private String Price,Name,Description,Offer;
    private Uri ImageUri;
    private DatabaseReference productsRef;
    private StorageReference ProductImagesRef;
    private ProgressDialog loadingBar;

    private  String Category,Location,CatergorySpinner,VendorName;
    private String saveCurrentDate, saveCurrentTime, productRandomKey, downloadImageUrl;
    private  String Categoryname;

    private String CurrentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sellers_add_products);



        selectproductimage= (ImageView) findViewById(R.id.select_product_image);
        AddNewproductButton= (Button)findViewById(R.id.add_product_button) ;

        Inputproductname   = findViewById(R.id.productname);
        inputProductDescription = findViewById(R.id.productdescription);
        inputproductPrice  = findViewById(R.id.productprice);

        loadingBar= new ProgressDialog(this);

        CurrentUserID = FirebaseAuth.getInstance().getUid();




        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        productsRef = FirebaseDatabase.getInstance().getReference().child("All Products");



        selectproductimage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                OpenGallery();
            }
        });


        AddNewproductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                validatepostdetails();

            }
        });






        Spinner spinner = findViewById(R.id.productoffer);
        ArrayList<String> arrayList = new ArrayList<>();

        arrayList.add("Select Product Category");
        arrayList.add("Breakfast");
        arrayList.add("Main Dishes");
        arrayList.add("Fast Foods");
        arrayList.add("Alcoholic Drinks");
        arrayList.add("Non-Alcoholic Drinks/Beverages");
        arrayList.add("Accommodation");




        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String category = parent.getItemAtPosition(position).toString();
                CatergorySpinner=category;


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });





    }

    private void validatepostdetails()
    {
        Name =  Inputproductname.getText().toString();
        Description =  inputProductDescription.getText().toString();
        Price =  inputproductPrice.getText().toString();


        if (ImageUri==null)
        {
            Toast.makeText(this, "Please select product photo ", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Description))
        {
            Toast.makeText(this, "Please enter your product details", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Name))
        {
            Toast.makeText(this, "Please enter your product Name", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(Price))
        {
            Toast.makeText(this, "Please enter your product Price", Toast.LENGTH_SHORT).show();
        }

        else if (CatergorySpinner=="Select Product Category")
        {
            Toast.makeText(this, "Please select your product Category", Toast.LENGTH_SHORT).show();
        }




        else
        {
            StoreProductInformation();

        }


    }

    private void StoreProductInformation()
    {
        loadingBar.setTitle("Adding New Product");
        loadingBar.setMessage("Please Wait");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();


        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForTime.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = ProductImagesRef.child("Post Images").child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(ImageUri);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();

                Toast.makeText(SellersAddProducts.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {


                Toast.makeText(SellersAddProducts.this, "Product Image Uploaded successfully ", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if (!task.isSuccessful())

                        {
                            throw task.getException();
                        }
                        downloadImageUrl=  filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if (task.isSuccessful())
                        {

                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(SellersAddProducts.this, "Product added Successfully", Toast.LENGTH_SHORT).show();

                            saveProductInfoToDatabase();
                        }

                    }
                });

            }
        });


    }

    private void saveProductInfoToDatabase()
    {

        final HashMap<String,Object> productMap  = new HashMap<>();
        productMap.put("pid",productRandomKey);
        productMap.put("date",saveCurrentDate);
        productMap.put("time",saveCurrentTime);
        productMap.put("description",Description);
        productMap.put("image",downloadImageUrl);
        productMap.put("category",CatergorySpinner);
        productMap.put("price",Price);
        productMap.put("pname",Name);
        productMap.put("poffer",Offer);
        productMap.put("VendorName",VendorName);
        productMap.put("ShopID",CurrentUserID);
        productMap.put("Location",Location);






        productsRef.child(CurrentUserID).child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {

                            Intent intent = new Intent(SellersAddProducts.this,SellersDasboardActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            loadingBar.dismiss();

                        }
                        else
                        {
                            String message = task.getException().toString();

                            Toast.makeText(SellersAddProducts.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                        }

                    }
                });





    }





    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,Gallery_Pick);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==Gallery_Pick && resultCode == RESULT_OK && data!= null)
        {
            ImageUri = data.getData();
            selectproductimage.setImageURI(ImageUri);


        }

    }


    @Override
    protected void onStart() {
        super.onStart();


        DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("All Shops").child("ShopDetails").child(CurrentUserID).child("Details");


        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {

                    String sCategory = dataSnapshot.child("Category").getValue().toString();
                    String sLocation = dataSnapshot.child("Location").getValue().toString();
                    String sVendorNAme = dataSnapshot.child("Name").getValue().toString();


                    Location = sLocation;
                    Category= sCategory;
                    VendorName = sVendorNAme;
                    Toast.makeText(SellersAddProducts.this, sLocation, Toast.LENGTH_SHORT).show();




                }else
                {
                    Toast.makeText(SellersAddProducts.this, "No info saved in your profile", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });


    }
}

