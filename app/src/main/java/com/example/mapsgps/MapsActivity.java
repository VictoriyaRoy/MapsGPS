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
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.mapsgps.databinding.ActivityMapsBinding;
import com.example.mapsgps.location.Camera;
import com.example.mapsgps.location.device.DeviceConnection;
import com.example.mapsgps.location.device.DeviceDatabase;
import com.example.mapsgps.location.device.DeviceTracker;
import com.example.mapsgps.location.device.NewDeviceActivity;
import com.example.mapsgps.location.user.GpsConnection;
import com.example.mapsgps.location.user.UserTracker;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

/**
 * Main Activity with map for showing locations of user and its devices
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_FINE_LOCATION = 99;

    private ActivityMapsBinding binding;

    private FloatingActionButton search_fab, gps_fab, settings_fab;

    UserTracker userTracker;
    GpsConnection gpsChecker;

    public static DeviceDatabase deviceDatabase;
    DeviceConnection deviceConnection;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        startInit();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            userTracker = new UserTracker();
            gpsChecker = new GpsConnection(gps_fab, userTracker, this);
            deviceDatabase = new DeviceDatabase(userId, this);
            deviceConnection = new DeviceConnection(search_fab, deviceDatabase, this) {
                @Override
                public void successConnect() {
                    showDevices();
                }
            };
            updateGps();
        }
    }

    /**
     * Start declaration of screen elements: map, toolbar, FABs
     */
    private void startInit() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        gps_fab = findViewById(R.id.gps_fab);
        search_fab = findViewById(R.id.search_fab);
        settings_fab = findViewById(R.id.settings_fab);
        settings_fab.setOnClickListener(view -> showSettingsMenu());
    }

    /**
     * Start request for user location
     */
    public void updateGps() {
        gpsChecker.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gpsChecker.fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    gpsChecker.successUpdate(location);
                }
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateGps();
            } else {
                Toast.makeText(this, "This app requires permission to be granted in order to work properly", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /**
     * If user signs out, open registration screen
     */
    private void signOut() {
        Intent intent = new Intent(MapsActivity.this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    /**
     * Open screen for adding new device
     */
    private void addDevice() {
        Intent intent = new Intent(MapsActivity.this, NewDeviceActivity.class);
        startActivity(intent);
    }

    /**
     * Find the count of user's devices:
     * If you haven't any device - show message about it
     * If you have 1 device - show its location
     * If you have a few devices - show popup menu, after choice show its location
     **/
    private void showDevices() {
        List<DeviceTracker> devices = deviceDatabase.getDevicesList();
        if (devices.size() == 0) {
            Toast.makeText(MapsActivity.this, "You have no connected devices yet", Toast.LENGTH_SHORT).show();
        } else if (devices.size() == 1) {
            devices.get(0).show();
        } else {
            showDeviceMenu(devices);
        }
    }

    /**
     * Show menu to choose a device
     * After choice, show this device on the map
     *
     * @param devicesList - list of user's devices
     **/
    private void showDeviceMenu(List<DeviceTracker> devicesList) {
        PopupMenu popupMenu = new PopupMenu(MapsActivity.this, search_fab);
        int orderNumber = 0;
        for (DeviceTracker device : devicesList) {
            popupMenu.getMenu().add(Menu.NONE, Menu.NONE, orderNumber, device.getTitle());
            orderNumber++;
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            devicesList.get(item.getOrder()).show();
            return false;
        });
        popupMenu.show();
    }

    private void showSettingsMenu() {
        PopupMenu popup = new PopupMenu(MapsActivity.this, settings_fab);
        MenuInflater inflater = popup.getMenuInflater();
        Menu menu = popup.getMenu();
        inflater.inflate(R.menu.menu_maps, menu);
        MenuItem signOutItem = menu.getItem(1);
        SpannableString spannable = new SpannableString("Sign out");
        spannable.setSpan(new ForegroundColorSpan(Color.RED), 0, spannable.length(), 0);
        signOutItem.setTitle(spannable);

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.add_device:
                    addDevice();
                    return true;
                case R.id.sign_out:
                    mAuth.signOut();
                    signOut();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        });
        popup.show();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        if (currentUser != null) {
            userTracker.addMarker(googleMap);
            deviceDatabase.setGoogleMap(googleMap);
            deviceConnection.startConnection();
        }

        Camera.mMap = googleMap;
        Camera.start();
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
        if (currentUser != null) {
            gpsChecker.stopLocationUpdates();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null) {
            signOut();
        }
    }
}