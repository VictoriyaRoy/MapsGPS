package com.example.mapsgps.location.device;

import static android.content.Context.CONNECTIVITY_SERVICE;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * This class process clicks on FAB for searching devices:
 * If you haven't internet connection - show message about it
 * If devices are requesting now - show message about it
 * If devices are connected:
    * If you haven't any device - show message about it
    * If you have 1 device - show its location
    * If you have a few devices - show popup menu, after choice show its location
 */

public class SearchFab {
    private DeviceDatabase deviceDatabase;
    private Context context;


    public SearchFab(FloatingActionButton search_fab, DeviceDatabase database, Context context) {
        search_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDevices(view);
            }
        });
        this.deviceDatabase = database;
        this.context = context;
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
     * @param view - view of FAB to show menu there
     */
    public void searchDevices(View view) {
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
                successConnect(view);
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

    /**
     * Find the count of user's devices:
        * If you haven't any device - show message about it
        * If you have 1 device - show its location
        * If you have a few devices - show popup menu, after choice show its location
     * @param view - view of FAB to show menu there
     */
    private void successConnect(View view) {
        List<DeviceTracker> devices = deviceDatabase.getDevices();
        if (devices.size() == 0) {
            Toast.makeText(context, "You have no connected devices yet", Toast.LENGTH_SHORT).show();
        } else if (devices.size() == 1) {
            devices.get(0).show();
        } else {
            showPopupMenu(view, devices);
        }
    }


    /**
     * Show menu to choose a device
     * After choice, show this device on the map
     * @param view - view of FAB to show menu there
     * @param devicesList - list of user's devices
     */
    private void showPopupMenu(View view, List<DeviceTracker> devicesList) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        int orderNumber = 0;
        for (DeviceTracker device: devicesList){
            popupMenu.getMenu().add(Menu.NONE, Menu.NONE, orderNumber, device.getTitle());
            orderNumber ++;
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                devicesList.get(item.getOrder()).show();
                return false;
            }
        });
        popupMenu.show();
    }

}
