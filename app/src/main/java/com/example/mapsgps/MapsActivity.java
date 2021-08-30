package com.example.mapsgps;


import android.Manifest;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.mapsgps.databinding.ActivityMapsBinding;
import com.example.mapsgps.location.Camera;
import com.example.mapsgps.location.device.DeviceDatabase;
import com.example.mapsgps.location.device.DeviceSearch;
import com.example.mapsgps.location.device.NewDeviceActivity;
import com.example.mapsgps.location.user.GpsConnection;
import com.example.mapsgps.location.user.UserTracker;
import com.example.mapsgps.registration.LoginActivity;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_FINE_LOCATION = 99;
    private static final String USER_ID = "user_id";

    GoogleMap mMap;
    private ActivityMapsBinding binding;

    private FloatingActionButton search_fab, gps_fab;

    UserTracker userTracker;
    GpsConnection gpsChecker;

    DeviceDatabase deviceDatabase;
    DeviceSearch deviceSearch;

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        startInit();

        if(currentUser != null) {
            userId = currentUser.getUid();
            userTracker = new UserTracker();
            gpsChecker = new GpsConnection(gps_fab, userTracker, this);
            deviceDatabase = new DeviceDatabase(userId, this);
            deviceSearch = new DeviceSearch(search_fab, deviceDatabase, this);

            updateGps();
        }
    }

    private void startInit(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setSupportActionBar(binding.toolbar);

        gps_fab = findViewById(R.id.gps_fab);
        search_fab = findViewById(R.id.search_fab);
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
        if (currentUser != null) {
            gpsChecker.startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(currentUser != null) {
            gpsChecker.stopLocationUpdates();
        }
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
                add_device();
                return true;
            case R.id.sign_out:
                mAuth.signOut();
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void add_device() {
        Intent intent = new Intent(MapsActivity.this, NewDeviceActivity.class);
        intent.putExtra(USER_ID, userId);
        startActivity(intent);
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
        if (currentUser != null) {
            userTracker.addMarker(mMap);
            deviceDatabase.setGoogleMap(mMap);
        }

        Camera.mMap = mMap;
        Camera.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser != null){
            Toast.makeText(MapsActivity.this, currentUser.getEmail(), Toast.LENGTH_LONG).show();
        } else {
            signOut();
        }
    }

    private void signOut(){
        Intent intent = new Intent(MapsActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}