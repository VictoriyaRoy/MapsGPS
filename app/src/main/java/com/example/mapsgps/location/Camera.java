package com.example.mapsgps.location;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class Camera {
    public static GoogleMap mMap;
    private static final int CAMERA_ZOOM = 17;
    private static final int START_CAMERA_ZOOM = 17;

    public static void updateCamera(LatLng position){
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(position, CAMERA_ZOOM));
        mMap.animateCamera(cameraUpdate, 1000, null);
    }

    public static void start() {
        mMap.moveCamera(CameraUpdateFactory.zoomTo(START_CAMERA_ZOOM));
    }
}
