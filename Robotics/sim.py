import serial
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

PI_USB_PORT = '/dev/ttyUSB0'  # the port where arduino uno is connected to raspberry pi
BAUD_RATE = 9600
DEVICE_ID = 5000

if __name__ == '__main__':
    # initializing firebase application to be able to make requests
    cred = credentials.Certificate("service.json")
    firebase_admin.initialize_app(cred, {
        "databaseURL": "https://mapsgps-fd863-default-rtdb.europe-west1.firebasedatabase.app/"})
    ref = db.reference("/")  # create reference object in database to which data will be sent

    ser = serial.Serial(PI_USB_PORT, BAUD_RATE)  # connect to serial port
    while True:
        try:
            # create array of gps data values and convert them to numbers
            gps_data = ser.readline().decode('utf-8')[:-2].split()
            for i, item in enumerate(gps_data):
                if "." not in item:
                    gps_data[i] = int(item)
                else:
                    gps_data[i] = float(item)

            request_data = {}
            keys = ["latitude", "longitude", "year", "month", "day", "hour", "minute", "second"]
            for i, key in enumerate(keys):
                request_data[key] = gps_data[i]
            print(gps_data)

            ref.set({"Devices": {DEVICE_ID: request_data}})  # send request to referenced object
        except (UnicodeDecodeError, ValueError):
            print("skip")
