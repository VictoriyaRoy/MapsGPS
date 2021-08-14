#include <Arduino.h>
#include <TinyGPS++.h>
#include <SoftwareSerial.h>

const int RXPin = 9, TXPin = 8;
const int BaudRate = 9600;
const int timeZone = 2;

TinyGPSPlus gps;

// The serial connection to the GPS device
SoftwareSerial GPSModule(RXPin, TXPin);

struct GPSInfoStruct {
    double latitude;
    double longitude;
    double altitude;
    int year;
    int month;
    int day;
    int hour;
    int minute;
    int second;
    double speedMPS;
    double speedKMPH;
} GPSInfo;

bool isGPSInfoAvailable();
void getLocation();
void getDate();
void getTime();
void getSpeed();
void printLocation();
void printDate();
void printTime();
void printSpeed();

void setup(){
    Serial.begin(BaudRate);
    GPSModule.begin(BaudRate);
}

void loop(){
    while (GPSModule.available() > 0){
        byte gpsData = GPSModule.read();
        gps.encode(gpsData);
        if (gps.location.isValid() || gps.date.isValid() || gps.time.isValid() ||
             gps.speed.isValid() || gps.altitude.isValid()) {
            printLocation();
            getLocation();
            getDate();
            getTime();
            getSpeed();
        } else {
            Serial.write(gpsData); // print NMEA sentences
        }
    }
}

void getLocation() {
    GPSInfo.latitude =  gps.location.lat();
    GPSInfo.longitude = gps.location.lng();
    GPSInfo.altitude = gps.altitude.meters();
}

void printLocation() {
    Serial.print("Latitude= ");
    Serial.println(GPSInfo.latitude, 6);

    Serial.print("Longitude= ");
    Serial.println(GPSInfo.longitude, 6);

    Serial.print("Altitude in meters = ");
    Serial.println(GPSInfo.altitude, 6);
}

void getDate() {
    GPSInfo.year = gps.date.year();
    GPSInfo.month = gps.date.month();
    GPSInfo.day = gps.date.day();
}

void printDate() {
    Serial.print("Year = ");
    Serial.println(GPSInfo.year);

    Serial.print("Month = ");
    Serial.println(GPSInfo.month);

    Serial.print("Day = ");
    Serial.println(GPSInfo.day);
}

void getTime() {
    GPSInfo.hour = gps.time.hour() + timeZone;
    GPSInfo.minute = gps.time.minute();
    GPSInfo.second= gps.time.second();
}

void printTime() {
    Serial.print("Hour = ");
    Serial.println(GPSInfo.hour);

    Serial.print("Minute = ");
    Serial.println(GPSInfo.minute);

    Serial.print("Second = ");
    Serial.println(GPSInfo.second);
}

void getSpeed() {
    GPSInfo.speedMPS = gps.speed.mps();
    GPSInfo.speedKMPH = gps.speed.kmph();
}

void printSpeed() {
    Serial.print("Speed in m/s = ");
    Serial.println(GPSInfo.speedMPS);

    Serial.print("Speed in km/h = ");
    Serial.println(GPSInfo.speedKMPH);
}