package com.example.mapsgps;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.mapsgps.databinding.ActivityMapsBinding;
import com.example.mapsgps.location.Camera;
import com.example.mapsgps.location.GpsConnection;
import com.example.mapsgps.location.UserLocation;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_FINE_LOCATION = 99;

    GoogleMap mMap;
    private ActivityMapsBinding binding;

    private FloatingActionButton search_fab, gps_fab;

    UserLocation user;
    GpsConnection gpsChecker;


    GpsLocation deviceLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        startInit();

        user = new UserLocation(new LatLng(0,0));
        gpsChecker = new GpsConnection(gps_fab, user, this);
        updateGps();
        updateDeviceLocation();

    }

    private void startInit(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setSupportActionBar(binding.toolbar);

        gps_fab = findViewById(R.id.gps_fab);
        search_fab = findViewById(R.id.search_fab);
        search_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "There will be device searching", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void updateGps() {
        gpsChecker.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gpsChecker.fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    gpsChecker.successUpdate(location);
                }
            });
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
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

    @Override
    protected void onResume() {
        super.onResume();
            gpsChecker.startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gpsChecker.stopLocationUpdates();
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
        user.setMarker(mMap.addMarker(new MarkerOptions().position(user.getPosition())));
        Camera.mMap = mMap;
        Camera.start();
    }
}