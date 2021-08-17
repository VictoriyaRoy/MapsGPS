package com.example.mapsgps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.mapsgps.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int FAST_UPDATE_INTERVAL = 5;
    private static final int DEFAULT_UPDATE_INTERVAL = 30;
    private static final int PERMISSIONS_FINE_LOCATION = 99;

    private static final int CAMERA_ZOOM = 17;

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FloatingActionButton search_fab, gps_fab;

    LocationRequest locationRequest;
    LocationCallback locationCallBack;
    FusedLocationProviderClient fusedLocationProviderClient;

    boolean requestingLocationUpdates = true;

    Marker current_marker;
    LatLng current_position;

    GpsLocation deviceLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        search_fab = findViewById(R.id.search_fab);
        search_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "There will be device searching", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        gps_fab = findViewById(R.id.gps_fab);
        gps_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEnabledGPS()){
                    updateCamera();
                }
                else {
                    disconnectGPS();
                    buildAlertMessageNoGps();
                }
            }
        });

        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updatePosition(locationResult.getLastLocation());
                if(!current_marker.isVisible()) {
                    connectGPS();
                }
            }
        };

        updateGps();
        updateDeviceLocation();


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
        }
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGps();
                }
                else {
                    Toast.makeText(this, "This app requires permission to be granted in order to work properly", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    private void updateGps() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null){
                        updatePosition(location);
                        connectGPS();
                    }
                }
            });
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }

    private void updatePosition(Location location) {
        double new_lat = location.getLatitude();
        double new_lon = location.getLongitude();

        if (current_position.latitude != new_lat | current_position.longitude != new_lon) {
            current_position = new LatLng(new_lat, new_lon);
            current_marker.setPosition(current_position);
        }
    }

    private void updateCamera(){
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(current_position, CAMERA_ZOOM));
        mMap.animateCamera(cameraUpdate, 1000, null);
    }

    private boolean isEnabledGPS(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void connectGPS(){
        current_marker.setVisible(true);
        gps_fab.setImageResource(R.drawable.gps_fixed);
        updateCamera();
    }

    private void disconnectGPS(){
        current_marker.setVisible(false);
        gps_fab.setImageResource(R.drawable.gps_not_fixed);
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_maps, menu);
        MenuItem signOutItem = menu.getItem(1);
        SpannableString spannable = new SpannableString("Sign out");
        spannable.setSpan(new ForegroundColorSpan(Color.RED), 0, spannable.length(), 0);
        signOutItem.setTitle(spannable);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.add_device:
                return true;
            case R.id.sign_out:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateDeviceLocation(){
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mapsgps-fd863-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference myRef = database.getReference("Devices");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot device1 = snapshot.child("test_device");
                deviceLocation = device1.getValue(GpsLocation.class);
                LatLng deviceLatLng = new LatLng(deviceLocation.latitude, deviceLocation.longitude);
                mMap.addMarker(new MarkerOptions().position(deviceLatLng));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        GpsLocation newLocation = new GpsLocation(16, 21, 0.0, 0.0, 31, 8, 55, 2021);
        myRef.child("test_device").setValue(newLocation);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        current_position = new LatLng(0,0);
        current_marker = mMap.addMarker(new MarkerOptions().position(current_position).visible(false));
        current_marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.active_loc));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));



    }
}