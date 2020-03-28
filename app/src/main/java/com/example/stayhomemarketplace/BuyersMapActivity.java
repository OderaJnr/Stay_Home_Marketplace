package com.example.stayhomemarketplace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuyersMapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    Location lastLocation;
    String ShopName;
    LatLng DriverLocation;
    Button FindSellersAround;



    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String customerID;
    private DatabaseReference CustomerDatabaseRef;
    private  LatLng CustomerpickupLocation;
    private DatabaseReference DriverAvailableRef;
    private int radius=1;
    private Boolean driverFound=false;
    private String driverFoundID;

    private ProgressDialog loadingbar;
    LinearLayout Detailslinear;
    Button PricelistBtn;

    Marker DriverMarker;

    TextView ShopsDetails;



    private  Boolean requestbol=false;
    GeoQuery geoQuery;
    String SellerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyers_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        mAuth= FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();

        ShopsDetails = findViewById(R.id.shopdetails);
        Detailslinear =findViewById(R.id.lineardetails);

        PricelistBtn =findViewById(R.id.viewlistbutton);




        Detailslinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BuyersMapActivity.this,BuyersViewItemsActivity.class);
                startActivity(intent);
            }
        });

        PricelistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BuyersMapActivity.this,BuyersViewItemsActivity.class);
                intent.putExtra("sellerID",SellerID);
                startActivity(intent);
            }
        });








        FindSellersAround = findViewById(R.id.find_sellers_aroundbtn);
        FindSellersAround.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if  (requestbol)
                {   requestbol=false;
                    geoQuery.removeAllListeners();
                    DriverLocationRef.removeEventListener(DriverLocationRefListener);


                    driverFound=false;
                    radius=1;
                    String userID =FirebaseAuth.getInstance().getCurrentUser().getUid();

                    DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Customers Requests");
                    GeoFire geofire =new GeoFire(ref);
                    geofire.removeLocation(userID);



                }

                else
                {  requestbol=true;

                    String userID= currentUser.getUid();
                    DatabaseReference Requests = FirebaseDatabase.getInstance().getReference().child("Buyer Request");
                    GeoFire geoFire = new GeoFire(Requests);



                    geoFire.setLocation(userID, new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()), new GeoFire.CompletionListener(){
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                        }
                    });


                    CustomerpickupLocation=new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(CustomerpickupLocation).title("Buyer"));


                    FindSellersAround.setText("Finding Sellers Around");


                    GetClosestDriverCab();

                }

            }
        });

        loadingbar=new ProgressDialog(this);



        CustomerDatabaseRef= FirebaseDatabase.getInstance().getReference().child("RescueCustomers Requests");



        currentUser=mAuth.getCurrentUser();



    } private void GetClosestDriverCab()
    {
        DriverAvailableRef=FirebaseDatabase.getInstance().getReference().child("Sellers Location");
        GeoFire geoFire=new GeoFire(DriverAvailableRef);
        geoQuery=geoFire.queryAtLocation(new GeoLocation(CustomerpickupLocation.latitude,CustomerpickupLocation.longitude),radius);
        geoQuery.removeAllListeners();


        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location)
            {
                if(!driverFound && requestbol)
                {
                    driverFound=true;
                    driverFoundID=key;

                    SellerID = driverFoundID;


                    GettingDriverLocation();


                }

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady()
            {
                if (!driverFound)
                {
                    radius++;
                    GetClosestDriverCab();
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }









    private DatabaseReference DriverLocationRef;
    private ValueEventListener DriverLocationRefListener;

    private void GettingDriverLocation()

    {

        DriverLocationRef=FirebaseDatabase.getInstance().getReference().child("Sellers Location").child(driverFoundID).child("l");
        DriverLocationRefListener=DriverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {if (dataSnapshot.exists()&& requestbol)
            {
                List<Object> driverLocationmap=(List<Object>)dataSnapshot.getValue();
                double LocationLat=0;
                double LocationLng=0;

                if (driverLocationmap.get(0) !=null)
                {
                    LocationLat=Double.parseDouble(driverLocationmap.get(0).toString());

                } if (driverLocationmap.get(1) !=null)

            {
                LocationLng=Double.parseDouble(driverLocationmap.get(1).toString());
            }

                LatLng driverLatLng=new LatLng(LocationLat,LocationLng);
                if (DriverMarker!=null)
                {
                    DriverMarker.remove();
                }


                Location location1=new Location("");
                location1.setLatitude(CustomerpickupLocation.latitude);
                location1.setLongitude(CustomerpickupLocation.longitude);


                Location location2=new Location("");
                location2.setLatitude(driverLatLng.latitude);
                location2.setLongitude(driverLatLng.longitude);

                float Distance =location1.distanceTo(location2);

                DriverLocation =driverLatLng;







                getDriverInfo();






            }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        loadingbar.setTitle("LOADING YOUR LOCATION");
        loadingbar.setMessage("please wait..");
        loadingbar.show();

        buildGoogleApiClient();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);


    }


    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location)
    {

        loadingbar.dismiss();
        lastLocation=location;

        LatLng latlng=new LatLng(location.getLatitude(),location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

    }

    protected synchronized void buildGoogleApiClient()

    {
        googleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();

    }




    private  void getDriverInfo()
    {

        DatabaseReference SellersDatabaseRef=FirebaseDatabase.getInstance().getReference().child("Sellers").child(driverFoundID);
        SellersDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {



                if (dataSnapshot.exists())
                {

                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                        String displayname=datas.child("name").getValue().toString();
                        Toast.makeText(BuyersMapActivity.this, displayname, Toast.LENGTH_SHORT).show();
                        DriverMarker = mMap.addMarker(new MarkerOptions().position(DriverLocation).title(displayname).snippet("Click To Order From This Store"));
                        FindSellersAround.setVisibility(View.GONE);
                        Detailslinear.setVisibility(View.VISIBLE);


                        ShopsDetails.setText( displayname +" is closest Grocery  store near you");



                    }
                }else {
                    Toast.makeText(BuyersMapActivity.this, "No sellers around at the moment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {




        return false;




    }



}
