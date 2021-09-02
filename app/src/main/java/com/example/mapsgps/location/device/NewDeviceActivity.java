package com.example.mapsgps.location.device;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mapsgps.R;
import com.example.mapsgps.registration.Credentials;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Activity for adding new device to user's account
 */
public class NewDeviceActivity extends AppCompatActivity {

    private static final String USER_ID = "user_id";
    private static final int NEW_DEVICE_REQUEST = 1;
    private static final String IS_ADDED_KEY = "is_added";
    private String userId, deviceId;

    private TextInputLayout titleInput, deviceIdInput, pinCodeInput;
    private Button add_device_btn;

    private FirebaseDatabase database;
    private DatabaseReference deviceData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device);

        startInit();
        userId = getIntent().getStringExtra(USER_ID);
        database = FirebaseDatabase.getInstance("https://mapsgps-fd863-default-rtdb.europe-west1.firebasedatabase.app");


    }

    /**
     * Declaration of all screen elements
     */
    private void startInit() {
        titleInput = findViewById(R.id.title);
        deviceIdInput = findViewById(R.id.device_id);
        pinCodeInput = findViewById(R.id.pin_code);
        add_device_btn = findViewById(R.id.add_device);
        add_device_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceIdInput.setErrorEnabled(false);
                deviceId = Credentials.getData(deviceIdInput);
                deviceData = database.getReference("Users").child(userId).child(deviceId);
                if (Credentials.checkEmpty(deviceIdInput) & Credentials.checkEmpty(titleInput) & Credentials.checkEmpty(pinCodeInput)){
                    checkIsNewDevice();
                }
            }
        });
    }

    /**
     * Check if user hasn't this device connected yet
     * If true, check correctness of entered data
     * Otherwise, show message about it
     */
    private void checkIsNewDevice(){
        deviceData.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    Object result = task.getResult().getValue();
                    if(result == null){
                        checkDeviceCode();
                    } else{
                        deviceIdInput.setError("This device already connected to your account");
                        deviceIdInput.setErrorEnabled(true);
                    }
                } else {
                    DeviceDatabase.exceptionCheck(task, NewDeviceActivity.this);
                }
            }
        });
    }

    /**
     * Check if in database is device with entered id and pin-code
     * If true, add new device to user's account
     * Otherwise, show message about error
     */
    private void checkDeviceCode() {
        DatabaseReference deviceCodes = database.getReference("DeviceCodes").child(deviceId);
        deviceCodes.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    Object rightPinCode = task.getResult().getValue();
                    if (rightPinCode == null){
                        deviceIdInput.setError("Device with this id doesn't exist");
                        deviceIdInput.setErrorEnabled(true);
                    } else if (!rightPinCode.equals(Credentials.getData(pinCodeInput))){
                        Toast.makeText(NewDeviceActivity.this, "Check device id and pin code and try again", Toast.LENGTH_LONG).show();
                    } else {
                        addDevice();
                    }
                    
                } else {
                    DeviceDatabase.exceptionCheck(task, NewDeviceActivity.this);
                }
            }
        });
    }

    /**
     * Add new device to user's account
     * Set result to update ui
     */
    private void addDevice() {
        deviceData.setValue(Credentials.getData(titleInput));
        Toast.makeText(NewDeviceActivity.this, "Device was added", Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.putExtra(IS_ADDED_KEY, true);
        setResult(NEW_DEVICE_REQUEST, intent);
        finish();

    }
}