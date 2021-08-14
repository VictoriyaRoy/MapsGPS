#include <Arduino.h>
#include <TinyGPS++.h>
#include <SoftwareSerial.h>

const int RXPin = 9, TXPin = 8;
const int BaudRate = 9600;
const int timeZone = 2;
const int printDelay = 100;
const int transmissionDelay = 100;

TinyGPSPlus gps;

// The serial connection to the GPS device
SoftwareSerial GPSModule(RXPin, TXPin);

struct GPSDataStruct {
    double latitude;
    double longitude;
    int year;
    int month;
    int day;
    int hour;
    int minute;
    int second;
} GPSData;

void getLocation();
void printLocation();
void getDate();
void printDate();
void getTime();
void printTime();
void transmit_data();

void setup(){
    Serial.begin(BaudRate);
    GPSModule.begin(BaudRate);
}

void loop(){
    // Serial.print("Works!");
    // Serial.write("Here");
    while (GPSModule.available() > 0) {
        byte gpsData = GPSModule.read();
        gps.encode(gpsData);
        if (gps.location.isValid() && gps.date.isValid() && gps.time.isValid()) {
            getLocation();
            getDate();
            getTime();
            transmit_data();
        } 
        else {
            Serial.write(gpsData); // print NMEA sentences
        }
    }
}

void getLocation() {
    GPSData.latitude =  gps.location.lat();
    GPSData.longitude = gps.location.lng();
}

void printLocation() {
    Serial.print("Latitude= ");
    Serial.println(GPSData.latitude, 6);

    Serial.print("Longitude= ");
    Serial.println(GPSData.longitude, 6);
    delay(printDelay);
}

void getDate() {
    GPSData.year = gps.date.year();
    GPSData.month = gps.date.month();
    GPSData.day = gps.date.day();
}

void printDate() {
    Serial.print("Year = ");
    Serial.println(GPSData.year);

    Serial.print("Month = ");
    Serial.println(GPSData.month);

    Serial.print("Day = ");
    Serial.println(GPSData.day);
    delay(printDelay);
}

void getTime() {
    GPSData.hour = gps.time.hour() + timeZone;
    GPSData.minute = gps.time.minute();
    GPSData.second= gps.time.second();
}

void printTime() {
    Serial.print("Hour = ");
    Serial.println(GPSData.hour);

    Serial.print("Minute = ");
    Serial.println(GPSData.minute);

    Serial.print("Second = ");
    Serial.println(GPSData.second);
    delay(printDelay);
}

void transmit_data() {
    Serial.print(GPSData.latitude, 6);
    Serial.print(" ");
    Serial.print(GPSData.longitude, 6);
    Serial.print(" ");
    Serial.print(GPSData.year);
    Serial.print(" ");
    Serial.print(GPSData.month);
    Serial.print(" ");
    Serial.print(GPSData.day);
    Serial.print(" ");
    Serial.print(GPSData.hour);
    Serial.print(" ");
    Serial.print(GPSData.minute);
    Serial.print(" ");
    Serial.print(GPSData.second);
    Serial.println();
}