package com.example.mapsgps.location.device;

import static android.content.Context.CONNECTIVITY_SERVICE;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.mapsgps.MapsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * This class controls status of connection to Firebase database:
 * If you haven't internet connection - show message about it
 * If devices are requesting now - show message about it
 * If devices are connected - do abstract method
 */

public abstract class DeviceConnection {
    private DeviceDatabase deviceDatabase;
    private Context context;

    private DeviceConnection(DeviceDatabase database, Context context) {
        this.deviceDatabase = database;
        this.context = context;
    }

    public DeviceConnection(Button button, DeviceDatabase database, Context context) {
        this(database, context);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDevices();
            }
        });
    }

    public DeviceConnection(FloatingActionButton search_fab, DeviceDatabase database, Context context) {
        this(database, context);
        search_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDevices();
            }
        });
    }

    /**
     * Check if internet is connected
     * @return boolean true if connected, and false otherwise
     */
    public boolean isOnline() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * Check status of connection to device database and do relevant function
     */
    public void searchDevices() {
        switch (deviceDatabase.getDeviceStatus()) {
            case DeviceDatabase.NOT_CONNECT:
                //try to connect to database
                startConnection();
                break;

            case DeviceDatabase.START_CONNECT:
                //need more time for end request, show message to wait
                Toast.makeText(context, "Please wait a few seconds and try again", Toast.LENGTH_LONG).show();
                break;

            case DeviceDatabase.SUCCESS_CONNECT:
                //show devices
                successConnect();
        }
    }

    /**
     * If you have internet connection - make request to firebase database
     * Otherwise, show message about internet connection
     */
    public void startConnection() {
        if(isOnline()){
            if (deviceDatabase.isMapConnect()) {
                deviceDatabase.requestDevices();
            }
        } else{
            Toast.makeText(context, "Check your internet connection and try again", Toast.LENGTH_LONG).show();
        }
    }

    public abstract void successConnect();
}
