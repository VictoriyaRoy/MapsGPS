package com.example.mapsgps.location.device;

import com.google.android.gms.maps.model.LatLng;

/**
 * Class represents entry about location of device
 */
public class DeviceEntry {
    public Integer day, hour, minute, month, second, year;
    public Double latitude, longitude;

    public DeviceEntry() {

    }

    public DeviceEntry(Integer day, Integer hour, Double latitude, Double longitude, Integer minute, Integer month, Integer second, Integer year) {
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.month = month;
        this.second = second;
        this.year = year;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }
}
