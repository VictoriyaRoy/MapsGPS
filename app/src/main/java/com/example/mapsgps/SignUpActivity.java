package com.example.mapsgps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText email_et, password_et;
    private Button sign_up_btn;
    private TextView login_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        email_et = findViewById(R.id.email_et);
        password_et = findViewById(R.id.password_et);

        login_tv = findViewById(R.id.login);
        login_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sign_up_btn = findViewById(R.id.sign_up);
        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpWithEmail(email_et.getText().toString(), password_et.getText().toString());
            }
        });
    }

    private void signUpWithEmail(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Registration is successful", Toast.LENGTH_SHORT).show();
                    finish();
                } else{
                    Toast.makeText(SignUpActivity.this, "Registration is failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}