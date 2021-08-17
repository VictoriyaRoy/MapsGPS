package com.example.mapsgps;

public class GpsLocation {
    public String day, hour, latitude, longitude, minute, month, second, year;

    public GpsLocation(String day, String hour, String latitude, String longitude, String minute, String month, String second, String year) {
        this.day = day;
        this.hour = hour;
        this.latitude = latitude;
        this.longitude = longitude;
        this.minute = minute;
        this.month = month;
        this.second = second;
        this.year = year;
    }
}
