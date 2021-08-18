package com.example.mapsgps.location.device;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ThrowOnExtraProperties;

import java.util.ArrayList;
import java.util.List;

public class DeviceDatabase {

    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private GoogleMap mMap;
    private Context context;

    private String userId;
    private List<DeviceTracker> devices;

    public DeviceDatabase(String userId, GoogleMap mMap, Context context) {
        this.database = FirebaseDatabase.getInstance("https://mapsgps-fd863-default-rtdb.europe-west1.firebasedatabase.app");
        this.userRef = database.getReference("Users").child(userId);
        this.userId = userId;
        this.mMap = mMap;

        requestDevices();
    }

    public void requestDevices(){
        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    devices = new ArrayList<DeviceTracker>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        DeviceTracker new_device = new DeviceTracker(ds.getValue(String.class));
                        devices.add(new_device);
                        new_device.addMarker(mMap);
                    }
                } else{
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public List<DeviceTracker> getDevices() {
        return devices;
    }
}
