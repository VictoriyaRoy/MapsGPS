package com.example.mapsgps.location;

import com.example.mapsgps.R;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class UserLocation extends MyLocation{

    public UserLocation(LatLng position) {
        super(position);
    }

    @Override
    public void setMarker(Marker marker) {
        super.setMarker(marker);
        this.marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.active_loc));
    }
}
