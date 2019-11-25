package com.example.mylocation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mylocation.directionhelpers.FetchURL;
import com.example.mylocation.directionhelpers.TaskLoadedCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, TaskLoadedCallback
{
    private Marker mMarker;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastloction;
    private Marker currentUserLocationMarker, BusStopMaker;
    private Bitmap btmp;
    private static final int Request_User_Location_Code = 99;
    private double a = 37.4219983 ;private double b = -122.084;
    private int count = 0;
    private MarkerOptions address_1, address_2;
    private Polyline currentPolyline;
    private Button btn_payment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkUserLocationPermission();
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        view();
        controlButton();

    }

    public  void openPayment(View v){
        switch (v.getId()){
            case R.id.btn_Payment:
                Dialog dialog = new Dialog(MapsActivity.this);
                dialog.setTitle("HÓA ĐƠN");
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.activity_pill);
                dialog.show();
        }
    }
    private void controlButton() {
        btn_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(MapsActivity.this);
                dialog.setTitle("HÓA ĐƠN");
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.activity_pill);
                dialog.show();
            }
        });
    }


    private void view() {
        btn_payment = (Button)findViewById(R.id.btn_Payment);
    }

    public void onClickCalc(View v){
        switch (v.getId()){
            case R.id.btn_distant:
                String url = getUrl(address_1.getPosition(),address_2.getPosition(),"driving");
                new FetchURL(MapsActivity.this).execute(url,"driving");
        }
    }

    public String getUrl(LatLng origin, LatLng dest, String directionMode){
        String str_origin = "origin" + origin.latitude + "," + origin.longitude;

        String str_dest = "destination" + dest.latitude +"," + dest.longitude;

        String mode = "mode" + directionMode;

        String parameters = str_origin + "&" + str_dest + "&" + mode;

        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_search:
                EditText txt_adress = (EditText)findViewById(R.id.input_search);
                String address = txt_adress.getText().toString();
                List<Address> addressList = null;
                MarkerOptions userMarkerOption = new MarkerOptions();
                if (mMarker != null){
                    mMarker.remove();
                }
                if(!TextUtils.isEmpty(address))
                {
                    Geocoder geocoder = new Geocoder(this);

                    try
                    {
                        addressList = geocoder.getFromLocationName(address,6);

                        if(addressList != null)
                        {
                            for (int i = 0; i < addressList.size() ; i++)
                            {
                                Address userAddress = addressList.get(i);
                                LatLng newlatLng = new LatLng(userAddress.getLatitude(), userAddress.getLongitude());

                                userMarkerOption.position(newlatLng);
                                userMarkerOption.title(address);
                                userMarkerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                                mMarker = mMap.addMarker(userMarkerOption);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(newlatLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomBy(14));

                                /// ADDRESS 2
                                address_2 = new MarkerOptions().position(newlatLng).title("address 2");
                            }
                        }
                        else
                        {
                            Toast.makeText(this,"Address not found",Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                    {
                        Toast.makeText(this, "rewrite",Toast.LENGTH_SHORT).show();
                    }
                break;

        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            buildGoogleApiClient();

            mMap.setMyLocationEnabled(true);
        }




    }

    public boolean checkUserLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }
            return false;
        }
        else {
            return true;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case Request_User_Location_Code:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        if(googleApiClient == null)
                        {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(this,"Permission Denied...",Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    protected synchronized void buildGoogleApiClient()
    {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }


    @Override
    public void onLocationChanged(Location location) {


        lastloction = location;
        System.out.println(location);
        if(currentUserLocationMarker != null)
        {
            currentUserLocationMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Your current location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));




        currentUserLocationMarker = mMap.addMarker(markerOptions);

        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(this));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(12));
        if(googleApiClient != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
        }

        /// ADDRESS 1
        address_1 = new MarkerOptions().position(latLng).title("address 1");

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);

        }




    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onTaskDone(Object... values) {
        if(currentPolyline != null){
            currentPolyline.remove();
        }
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
}
