import serial

pi_usb_port = '/dev/ttyUSB0'  # the port where arduino uno is connected to raspberry pi

if __name__ == '__main__':
    baud_rate = 9600
    ser = serial.Serial(pi_usb_port, baud_rate)
    while True:
        try:
            gps_data = ser.readline().decode('utf-8')[:-2].split()
            converted_gps_data = [int(x) for x in gps_data]
        except UnicodeDecodeError:
            print("skip")
