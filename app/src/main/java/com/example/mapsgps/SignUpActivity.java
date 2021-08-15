package com.example.mapsgps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextInputLayout emailInput, passwordInput;
    private Button sign_up_btn;
    private TextView login_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);

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
                if (Credentials.checkEmailEmpty(emailInput) & Credentials.validatePassword(passwordInput)){
                    signUpWithEmail(Credentials.getData(emailInput).trim(), Credentials.getData(passwordInput));
                }
            }
        });
    }

    private void signUpWithEmail(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Registration is successful", Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                    finish();
                } else{
                    try {
                        throw task.getException();
                    } catch(FirebaseAuthInvalidCredentialsException e) {
                        emailInput.setError("Invalid email");
                        emailInput.setErrorEnabled(true);
                    } catch(FirebaseAuthUserCollisionException e) {
                        emailInput.setError("User with this email already exists");
                        emailInput.setErrorEnabled(true);
                    } catch (FirebaseNetworkException e) {
                        Toast.makeText(SignUpActivity.this, "Check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    } catch(Exception e) {
                        Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}