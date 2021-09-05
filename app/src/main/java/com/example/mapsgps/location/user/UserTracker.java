package com.example.mapsgps.location.user;

import com.example.mapsgps.R;
import com.example.mapsgps.location.LocationTracker;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Class represents a user
 * Auto-update a location of the user on map
 */
public class UserTracker extends LocationTracker {

    public UserTracker() {
        super();
    }

    @Override
    public void setMarker(Marker marker) {
        super.setMarker(marker);
        this.marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.active_loc));
    }
}
