package com.example.mapsgps.location.device;

import androidx.annotation.NonNull;

import com.example.mapsgps.location.LocationTracker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DeviceTracker extends LocationTracker {

    private static String dbLink = "https://mapsgps-fd863-default-rtdb.europe-west1.firebasedatabase.app";
    private static DatabaseReference devicesDB = FirebaseDatabase.getInstance(dbLink).getReference("Devices");

    private String id;
    private DeviceEntry entry;

    public DeviceTracker() {
        super();
    }

    public DeviceTracker(String id) {
        super();
        this.id = id;

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
