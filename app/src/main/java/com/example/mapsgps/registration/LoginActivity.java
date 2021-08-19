package com.example.mapsgps.registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mapsgps.MapsActivity;
import com.example.mapsgps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextInputLayout emailInput, passwordInput;
    private Button login_btn;
    private TextView sign_up_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);

        login_btn = findViewById(R.id.login);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Credentials.checkEmailEmpty(emailInput) & Credentials.checkEmpty(passwordInput)){
                    loginWithEmail(Credentials.getData(emailInput).trim(), Credentials.getData(passwordInput));
                }
            }
        });

        sign_up_tv = findViewById(R.id.sign_up);
        sign_up_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else{
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e){
                        emailInput.setError("User with this email doesn't exist");
                        emailInput.setErrorEnabled(true);
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        Toast.makeText(LoginActivity.this, "Check your email and password and try again", Toast.LENGTH_SHORT).show();
                    } catch (FirebaseNetworkException e) {
                        Toast.makeText(LoginActivity.this, "Check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    } catch (Exception e){
                        Toast.makeText(LoginActivity.this, e.getClass().toString(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        emailInput.getEditText().setText("");
        emailInput.setErrorEnabled(false);

        passwordInput.getEditText().setText("");
        passwordInput.setErrorEnabled(false);
    }
}