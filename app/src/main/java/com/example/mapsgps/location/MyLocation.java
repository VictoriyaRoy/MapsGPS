package com.example.mapsgps.location;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MyLocation {
    protected Marker marker;
    protected LatLng position;

    public MyLocation(LatLng position) {
        this.position = position;
    }

    protected void updatePosition(LatLng new_position) {
        if (new_position != position) {
            position = new_position;
            marker.setPosition(position);
        }
    }

    public void setMarker(Marker marker) {
        marker.setVisible(false);
        this.marker = marker;
    }

    public LatLng getPosition() {
        return position;
    }
}
