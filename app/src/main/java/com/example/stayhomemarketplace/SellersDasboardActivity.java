package com.example.stayhomemarketplace;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class SellersDasboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sellers_dasboard);

        CardView cardView = findViewById(R.id.maplocation);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellersDasboardActivity.this,SellersMapsActivity.class);
                startActivity(intent);
            }
        });



        CardView AdcardviewdProduct = findViewById(R.id.Addproduct);

        AdcardviewdProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellersDasboardActivity.this,SellersAddProducts.class);
                startActivity(intent);
            }
        });



        CardView ViewOrders = findViewById(R.id.vieworders);

        ViewOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellersDasboardActivity.this,SelllerViewOrdersActivity.class);
                startActivity(intent);
            }
        });
    }
}
