package com.example.stayhomemarketplace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class BuyersLoginActivity extends AppCompatActivity {

    TextView RegisterLink;
    private Button Customerloginbtn;
    private EditText Emailcustomer,Passwordcustomer;
    private ProgressDialog loadingbar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyers_login);
        RegisterLink = findViewById(R.id.registerlink);
        Emailcustomer=findViewById(R.id.emailcustomerlogin);
        Passwordcustomer=findViewById(R.id.passwordcustomerlogin);
        Customerloginbtn = findViewById(R.id.loginbutton);
        loadingbar=new ProgressDialog(this);

        mAuth= FirebaseAuth.getInstance();





        RegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        Customerloginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String email=Emailcustomer.getText().toString();
                String password=Passwordcustomer.getText().toString();

                SignInCustomer(email,password);

            }
        });


         TextView  sellerslogin = findViewById(R.id.registersellers);
        sellerslogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                Intent intent =  new Intent(BuyersLoginActivity.this,RegisterSellersActivity.class);
                startActivity(intent);
            }
        });





    }

    private void SignInCustomer(String email, String password) {


        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(BuyersLoginActivity.this,"Please enter your email",Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(BuyersLoginActivity.this,"Please enter your password",Toast.LENGTH_SHORT).show();
        }else
        {
            loadingbar.setTitle("Authenticating Details ");
            loadingbar.setMessage("please wait..");
            loadingbar.show();


            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful())
                    {

                        Intent customerIntent=new Intent(BuyersLoginActivity.this,HomeActivity.class);
                        startActivity(customerIntent);
                        Toast.makeText(BuyersLoginActivity.this,"signed in successfully ",Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();


                    }

                    else
                    {
                        Toast.makeText(BuyersLoginActivity.this,"Sign in Unsuccessful..please check your details and  try again",Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();

                    }

                }
            });
        }



    }
}
