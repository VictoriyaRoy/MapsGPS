package com.example.mapsgps;

public class GpsLocation {
    public Integer day, hour, minute, month, second, year;
    public Double latitude, longitude;

    public GpsLocation() {
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getSecond() {
        return second;
    }

    public void setSecond(Integer second) {
        this.second = second;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public GpsLocation(Integer day, Integer hour, Double latitude, Double longitude, Integer minute, Integer month, Integer second, Integer year) {
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.month = month;
        this.second = second;
        this.year = year;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
