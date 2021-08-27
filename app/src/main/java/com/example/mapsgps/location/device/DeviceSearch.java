package com.example.mapsgps.location.device;

import static android.content.Context.CONNECTIVITY_SERVICE;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Toast;

import com.example.mapsgps.MapsActivity;
import com.example.mapsgps.location.Camera;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class DeviceSearch {
    private DeviceDatabase deviceDatabase;
    private Context context;

    public DeviceSearch(DeviceDatabase database, Context context) {
        this.deviceDatabase = database;
        this.context = context;
    }

    public boolean isOnline() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public void searchDevices() {
        switch (deviceDatabase.getDeviceStatus()) {
            case DeviceDatabase.NOT_CONNECT:
                notConnect();
                break;
            case DeviceDatabase.START_CONNECT:
                Toast.makeText(context, "Please wait for searching your devices", Toast.LENGTH_SHORT).show();
                break;
            case DeviceDatabase.SUCCESS_CONNECT:
                successConnect();
        }
    }

    private void notConnect() {
        if(isOnline()){
            if (deviceDatabase.isMapConnect()) {
                deviceDatabase.requestDevices();
            }
            Toast.makeText(context, "Please wait for searching your devices", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(context, "Check your internet connection to see devices", Toast.LENGTH_LONG).show();
        }
    }

    private void successConnect() {
        List<DeviceTracker> devices = deviceDatabase.getDevices();
        if (devices == null) {
            Toast.makeText(context, "You have no connected devices yet", Toast.LENGTH_SHORT).show();
        } else if (devices.size() == 1) {
            Camera.updateCamera(devices.get(0).getPosition());
        }
    }

}
