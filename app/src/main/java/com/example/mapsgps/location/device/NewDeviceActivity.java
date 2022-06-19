package com.example.mapsgps.location.device;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mapsgps.MapsActivity;
import com.example.mapsgps.R;
import com.example.mapsgps.registration.Credentials;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Activity for adding new device to user's account
 */
public class NewDeviceActivity extends AppCompatActivity {

    private String newId;
    private String newTitle;

    private TextInputLayout title_input, id_input, pin_code_input;
    private Button add_device_btn;

    private FirebaseDatabase database;
    private DeviceDatabase deviceDatabase;
    private DeviceConnection deviceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device);

        deviceDatabase = MapsActivity.deviceDatabase;
        startInit();
        database = FirebaseDatabase.getInstance("https://mapsgps-fd863-default-rtdb.europe-west1.firebasedatabase.app");
    }

    /**
     * Declaration of all screen elements
     */
    private void startInit() {
        title_input = findViewById(R.id.title);
        id_input = findViewById(R.id.device_id);
        pin_code_input = findViewById(R.id.pin_code);
        add_device_btn = findViewById(R.id.add_device);
        deviceConnection = new DeviceConnection(add_device_btn, deviceDatabase, NewDeviceActivity.this) {
            @Override
            public void searchDevices() {
                if (Credentials.checkEmpty(id_input) & Credentials.checkEmpty(title_input) & Credentials.checkEmpty(pin_code_input)) {
                    super.searchDevices();
                }
            }

            @Override
            public void successConnect() {
                checkUniqueness();
            }
        };
    }

    /**
     * Check if device with entered id doesn't connect to user's account yet
     * and if title is different from another user's devices
     * If true, check correctness of entered data
     * Otherwise, show message about error
     */
    private void checkUniqueness() {
        newId = Credentials.getData(id_input);
        newTitle = Credentials.getData(title_input);
        List<DeviceTracker> devicesList = deviceDatabase.getDevicesList();
        for (DeviceTracker device : devicesList) {
            if (device.getId().equals(newId)) {
                id_input.setError("This device already connected to your account");
                id_input.setErrorEnabled(true);
                return;
            }
            if (device.getTitle().equals(newTitle)) {
                title_input.setError("You already have a device with the same title");
                title_input.setErrorEnabled(true);
                return;
            }
        }
        checkCorrectness();
    }

    /**
     * Check if in database is device with entered id and pin-code
     * If true, add new device to user's account
     * Otherwise, show message about error
     */
    private void checkCorrectness() {
        DatabaseReference deviceCodes = database.getReference("DeviceCodes").child(newId);
        deviceCodes.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Object rightPinCode = task.getResult().getValue();
                if (rightPinCode == null) {
                    id_input.setError("Device with this id doesn't exist");
                    id_input.setErrorEnabled(true);
                } else if (!rightPinCode.equals(Credentials.getData(pin_code_input))) {
                    Toast.makeText(NewDeviceActivity.this, "Check device id and pin code and try again", Toast.LENGTH_LONG).show();
                } else {
                    addDevice();
                }

            } else {
                DeviceDatabase.exceptionCheck(task, NewDeviceActivity.this);
            }
        });
    }

    /**
     * Add new device to user's account
     */
    private void addDevice() {
        if (deviceConnection.isOnline()) {
            deviceDatabase.addNewDevice(new DeviceTracker(newId, newTitle), NewDeviceActivity.this);
        } else {
            Toast.makeText(NewDeviceActivity.this, "Check your internet connection and try again", Toast.LENGTH_LONG).show();
        }

    }
}
