package com.example.mapsgps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mapsgps.registration.LoginActivity;
import com.example.mapsgps.registration.SignUpActivity;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button goToLogin = findViewById(R.id.go_to_login);
        Button goToRegister = findViewById(R.id.go_to_register);

        goToLogin.setOnClickListener(this);
        goToRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.go_to_login:
                intent = new Intent(this, LoginActivity.class);
                break;
            case R.id.go_to_register:
                intent = new Intent(this, SignUpActivity.class);
        }
        startActivity(intent);
    }
}