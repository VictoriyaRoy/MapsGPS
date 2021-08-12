package com.example.mapsgps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    private EditText email_et, password_et;
    private Button sign_in, sign_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_password);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    //User is signed in

                } else{
                    //User is signed out
                }
            }
        };

        email_et = findViewById(R.id.email_et);
        password_et = findViewById(R.id.password_et);

        sign_in = findViewById(R.id.sign_in);
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signing(email_et.getText().toString(), password_et.getText().toString());
            }
        });

        sign_up = findViewById(R.id.sign_up);
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registration(email_et.getText().toString(), password_et.getText().toString());
            }
        });

    }

    private void signing(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(EmailPasswordActivity.this, "Authorization is successful", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(EmailPasswordActivity.this, "Authorization is failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registration(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(EmailPasswordActivity.this, "Registration is successful", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(EmailPasswordActivity.this, "Registration is failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}