package com.example.eldercare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eldercare.libs.DatabaseLib;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private static final boolean TEST_MODE = false;
    Button loginButton;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    DatabaseLib databaseLib;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseLib = new DatabaseLib();
        mAuth = FirebaseAuth.getInstance();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstTimeUse = prefs.getBoolean("isFirstTimeUse", true);

        // If the coder wants to test the FirstTimeUse page each time the app starts
        if(TEST_MODE) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isFirstTimeUse", true);
            editor.apply();
        }

        if(firstTimeUse) {
            showFirstTimeUseLayout();
        }
        boolean isCaregiver = prefs.getBoolean("isCaregiver", true);

        if(isCaregiver) {
            showLoginCaregiverLayout();
        } else {
            showLoginElderlyLayout();
        }
    }

    private void showLoginCaregiverLayout() {
        setContentView(R.layout.activity_login_caregiver);

        TextInputEditText editTextEmail, editTextPassword;
        TextView registerNow;

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
        registerNow = findViewById(R.id.registerNow);

        registerNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                if (TextUtils.isEmpty(email) || !email.contains("caregiver")) {
                    Toast.makeText(Login.this, "Enter Caregiver email", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Enter password", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Login Successful.", Toast.LENGTH_SHORT).show();
                                    //TODO: testing
                                    Intent intent = new Intent(getApplicationContext(), MealCalendar.class);
                                    //Intent intent = new Intent(getApplicationContext(), CaregiverMainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(Login.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void showLoginElderlyLayout() {
        setContentView(R.layout.activity_login_elderly);

        TextInputEditText editTextPin;

        editTextPin = findViewById(R.id.pin);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String pin, email;
                pin = String.valueOf(editTextPin.getText());
                SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                email = prefs.getString("elderlyMail", "");

                if (TextUtils.isEmpty(pin)) {
                    Toast.makeText(Login.this, "Enter PIN code", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, pin)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Login Successful.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Login.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void showFirstTimeUseLayout() {
        Intent intent = new Intent(getApplicationContext(), FirstTimeUse.class);
        startActivity(intent);
        finish();
    }
}