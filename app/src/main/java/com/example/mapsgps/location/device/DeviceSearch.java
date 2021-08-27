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

import com.example.mapsgps.MapsActivity;
import com.example.mapsgps.R;
import com.example.mapsgps.location.Camera;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class DeviceSearch {
    private DeviceDatabase deviceDatabase;
    private Context context;

    public DeviceSearch(FloatingActionButton search_fab, DeviceDatabase database, Context context) {
        search_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDevices(view);
            }
        });
        this.deviceDatabase = database;
        this.context = context;
    }

    public boolean isOnline() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public void searchDevices(View view) {
        switch (deviceDatabase.getDeviceStatus()) {
            case DeviceDatabase.NOT_CONNECT:
                notConnect();
                break;
            case DeviceDatabase.START_CONNECT:
                Toast.makeText(context, "Please wait for searching your devices", Toast.LENGTH_SHORT).show();
                break;
            case DeviceDatabase.SUCCESS_CONNECT:
                successConnect(view);
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

    private void successConnect(View view) {
        List<DeviceTracker> devices = deviceDatabase.getDevices();
        if (devices == null) {
            Toast.makeText(context, "You have no connected devices yet", Toast.LENGTH_SHORT).show();
        } else if (devices.size() == 1) {
            devices.get(0).show();
        } else {
            showPopupMenu(view, devices);
        }
    }


    private void showPopupMenu(View view, List<DeviceTracker> devicesList) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        int orderNumber = 0;
        for (DeviceTracker device: devicesList){
            popupMenu.getMenu().add(Menu.NONE, Menu.NONE, orderNumber, device.getId());
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
