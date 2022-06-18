package com.example.mapsgps.registration;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mapsgps.MapsActivity;
import com.example.mapsgps.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

/**
 * Activity for logging user to system
 */
public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth mAuth;
    private TextInputLayout emailInput, passwordInput;
    private GoogleRegistration googleRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);

        Button login_btn = findViewById(R.id.login);
        login_btn.setOnClickListener(v -> {
            if (Credentials.checkEmailEmpty(emailInput) & Credentials.checkEmpty(passwordInput)) {
                signInWithEmail(Credentials.getData(emailInput).trim(), Credentials.getData(passwordInput));
            }
        });

        TextView sign_up_tv = findViewById(R.id.sign_up);
        sign_up_tv.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
            clearFields();
        });

        String token = getString(getResources().getIdentifier("default_web_client_id", "string", getPackageName()));
        googleRegistration = new GoogleRegistration(token, mAuth, LoginActivity.this) {
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
     * If credentials is correct, sign in with user's email and password
     * Otherwise, show error message
     *
     * @param email    Entered email
     * @param password Entered password
     */
    private void signInWithEmail(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user.isEmailVerified()) {
                    signIn();
                } else {
                    Toast.makeText(LoginActivity.this, "Email " + user.getEmail() + " is not verified", Toast.LENGTH_SHORT).show();
                }

            } else {
                try {
                    throw task.getException();
                } catch (FirebaseAuthInvalidUserException e) {
                    emailInput.setError("User with this email doesn't exist");
                    emailInput.setErrorEnabled(true);
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    Toast.makeText(LoginActivity.this, "Check your email and password and try again", Toast.LENGTH_SHORT).show();
                } catch (FirebaseNetworkException e) {
                    Toast.makeText(LoginActivity.this, "Check your internet connection and try again", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, e.getClass().toString(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    /**
     * Sign in to system
     * Open Maps Activity
     */
    private void signIn() {
        googleRegistration.signOut();
        Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /**
     * Delete text from fields
     */
    private void clearFields() {
        emailInput.getEditText().setText("");
        emailInput.setErrorEnabled(false);

        passwordInput.getEditText().setText("");
        passwordInput.setErrorEnabled(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        googleRegistration.processRequest(requestCode, data);
    }
}
