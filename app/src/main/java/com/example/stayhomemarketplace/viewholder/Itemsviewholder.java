package com.example.stayhomemarketplace.viewholder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.stayhomemarketplace.R;

public class Itemsviewholder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView Itemname,ItemPrice,ItemDescription,DispatchOrder;
    public ImageView ItemImage;
    public ElegantNumberButton elegantNumberButton;
    public Button Addtocartbtn;



    public Itemsviewholder(@NonNull View itemView) {
        super(itemView);


        Itemname = itemView.findViewById(R.id.Sproduct_name);
        ItemPrice = itemView.findViewById(R.id.Sproduct_price);
        ItemDescription = itemView.findViewById(R.id.Sproduct_description);
        Addtocartbtn = itemView.findViewById(R.id.addtocart);

        DispatchOrder = itemView.findViewById(R.id.dispatch);





        ItemImage = itemView.findViewById(R.id.Sproduct_image);



        elegantNumberButton = itemView.findViewById(R.id.numberbutton);


    }

    @Override
    public void onClick(View view) {

    }
}
