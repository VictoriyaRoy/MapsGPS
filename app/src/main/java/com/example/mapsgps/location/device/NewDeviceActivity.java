package com.example.mapsgps.location.device;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class NewDeviceActivity extends AppCompatActivity {

    private static final String USER_ID = "user_id";
    private String userId;

    private TextInputLayout titleInput, deviceIdInput, pinCodeInput;
    private Button add_device_btn;

    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device);

        userId = getIntent().getStringExtra(USER_ID);

        database = FirebaseDatabase.getInstance("https://mapsgps-fd863-default-rtdb.europe-west1.firebasedatabase.app");

        startInit();
    }

    private void startInit() {
        titleInput = findViewById(R.id.title);
        deviceIdInput = findViewById(R.id.device_id);
        pinCodeInput = findViewById(R.id.pin_code);
        add_device_btn = findViewById(R.id.add_device);
        add_device_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceIdInput.setErrorEnabled(false);
                if (Credentials.checkEmpty(titleInput) & Credentials.checkEmpty(deviceIdInput) & Credentials.checkEmpty(pinCodeInput)){
                    checkDeviceCode();
                }
            }
        });
    }

    private void checkDeviceCode() {
        String deviceId = Credentials.getData(deviceIdInput);
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
                        addDevice(deviceId, Credentials.getData(titleInput));
                    }
                    
                } else {
                    exceptionCheck(task);
                }
            }
        });
    }

    private void addDevice(String deviceId, String deviceTitle) {
        DatabaseReference userDatabase = database.getReference("Users").child(userId);
        userDatabase.child(deviceId).setValue(deviceTitle);
        Toast.makeText(NewDeviceActivity.this, "Device was added", Toast.LENGTH_LONG).show();
        finish();
    }

    private void exceptionCheck(Task<DataSnapshot> task) {
        String errorMsg = task.getException().getMessage();
        if (errorMsg == "Client is offline"){
            Toast.makeText(NewDeviceActivity.this, "Check your internet connection to add device", Toast.LENGTH_LONG).show();
        } else{
            Toast.makeText(NewDeviceActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}