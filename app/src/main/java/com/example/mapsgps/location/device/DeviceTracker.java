package com.example.mapsgps.location.device;

import androidx.annotation.NonNull;

import com.example.mapsgps.location.Camera;
import com.example.mapsgps.location.LocationTracker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Class represents a device
 * Auto-update a location of the device on map
 */
public class DeviceTracker extends LocationTracker {

    private static String dbLink = "https://mapsgps-fd863-default-rtdb.europe-west1.firebasedatabase.app";
    private static DatabaseReference devicesDB = FirebaseDatabase.getInstance(dbLink).getReference("Devices");

    private String id, title;
    private DeviceEntry entry;

    public DeviceTracker() {
        super();
    }

    public DeviceTracker(String id, String title) {
        super();
        this.id = id;
        this.title = title;
    }

    public void show(){
        Camera.updateCamera(getPosition());
    }

    @Override
    public void addMarker(GoogleMap mMap) {
        super.addMarker(mMap);
        marker.setTitle(title);
        setLocationUpdate();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    private void setLocationUpdate(){
        devicesDB.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                entry = snapshot.getValue(DeviceEntry.class);
                setPosition(entry.getLatLng());
                marker.setVisible(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
