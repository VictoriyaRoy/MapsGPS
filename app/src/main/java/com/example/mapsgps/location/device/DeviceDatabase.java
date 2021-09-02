package com.example.mapsgps.location.device;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Class connect to Firebase database to find list of user's devices
 *
 * Variable "deviceStatus" represents status of connection:
    * NOT_CONNECT - don't get a request for connection yet
    * START_CONNECT - request is in process
    * SUCCESS_CONNECT - list of devices was received
 */
public class DeviceDatabase {

    public static final String NOT_CONNECT = "not connect";
    public static final String START_CONNECT = "start connect";
    public static final String SUCCESS_CONNECT = "success connect";

    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private GoogleMap googleMap;
    private Context context;

    private String userId;
    private List<DeviceTracker> devices;
    private String deviceStatus = NOT_CONNECT;

    public DeviceDatabase(String userId, Context context) {
        this.database = FirebaseDatabase.getInstance("https://mapsgps-fd863-default-rtdb.europe-west1.firebasedatabase.app");
        this.userRef = database.getReference("Users").child(userId);
        this.userId = userId;
        this.context = context;
    }

    /**
     * Make request to Firebase database to find list of user's devices
     */
    public void requestDevices(){
        deviceStatus = START_CONNECT;
        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    devices = new ArrayList<DeviceTracker>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        DeviceTracker new_device = new DeviceTracker(ds.getKey(), ds.getValue(String.class));
                        devices.add(new_device);
                        new_device.addMarker(googleMap);
                    }
                    deviceStatus = SUCCESS_CONNECT;
                } else{
                    exceptionCheck(task, context);
                }
            }
        });
    }

    /**
     * If task isn't successful, show relevant message
     */
    static void exceptionCheck(Task<DataSnapshot> task, Context context) {
        String errorMsg = task.getException().getMessage();
        if (errorMsg == "Client is offline"){
            Toast.makeText(context, "Check your internet connection to see devices", Toast.LENGTH_LONG).show();
        } else{
            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public List<DeviceTracker> getDevices() {
        return devices;
    }

    public boolean isMapConnect() {
        return googleMap != null;
    }

    public String getDeviceStatus(){
        return deviceStatus;
    }
}
