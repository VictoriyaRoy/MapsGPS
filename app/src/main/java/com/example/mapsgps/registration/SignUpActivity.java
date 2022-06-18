package com.example.mapsgps.registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

/**
 * Activity for creations user's account
 */
public class SignUpActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth mAuth;
    private TextInputLayout emailInput, passwordInput;
    private GoogleRegistration googleRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);

        TextView login_tv = findViewById(R.id.login);
        login_tv.setOnClickListener(v -> finish());

        Button sign_up_btn = findViewById(R.id.sign_up);
        sign_up_btn.setOnClickListener(v -> {
            if (Credentials.checkEmailEmpty(emailInput) & Credentials.validatePassword(passwordInput)) {
                signUpWithEmail(Credentials.getData(emailInput).trim(), Credentials.getData(passwordInput));
            }
        });

        String token = getString(getResources().getIdentifier("default_web_client_id", "string", getPackageName()));
        googleRegistration = new GoogleRegistration(token, mAuth, SignUpActivity.this) {
            @Override
            void doSignIn() {
                signIn();
            }
        };

        Button google_btn = findViewById(R.id.google);
        google_btn.setOnClickListener(v -> {
            Intent signInIntent = googleRegistration.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    /**
     * If credentials is correct, create new account and verify email
     *
     * @param email    Entered email
     * @param password Entered password
     */
    private void signUpWithEmail(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    verifyEmail();
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        emailInput.setError("Invalid email");
                        emailInput.setErrorEnabled(true);
                    } catch (FirebaseAuthUserCollisionException e) {
                        //TODO: If email is not verified you can sign up again
                        emailInput.setError("User with this email already exists");
                        emailInput.setErrorEnabled(true);
                    } catch (FirebaseNetworkException e) {
                        Toast.makeText(SignUpActivity.this, "Check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * Send verification letter to entered email
     */
    private void verifyEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SignUpActivity.this, "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        mAuth.signOut();
        finish();
    }


    /**
     * Sign in to system
     * Open Maps Activity
     */
    private void signIn() {
        googleRegistration.signOut();
        Intent intent = new Intent(SignUpActivity.this, MapsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        googleRegistration.processRequest(requestCode, data);
    }
}