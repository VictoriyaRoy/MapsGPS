package com.example.mapsgps.location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Class represents a tracker of device or user on the map
 */
public class LocationTracker {
    protected Marker marker;
    protected LatLng position;

    public LocationTracker() {
        this.position = new LatLng(0, 0);
    }

    public void setPosition(LatLng new_position) {
        if (new_position != position) {
            position = new_position;
            if (marker != null) {
                marker.setPosition(position);
            }
        }
    }

    public void addMarker(GoogleMap mMap) {
        setMarker(mMap.addMarker(new MarkerOptions().position(position)));
    }

    public void setMarker(Marker marker) {
        marker.setVisible(false);
        this.marker = marker;
        marker.setPosition(position);
    }

    public LatLng getPosition() {
        return position;
    }

    public Marker getMarker() {
        return marker;
    }
}
