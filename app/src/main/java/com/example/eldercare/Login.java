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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    private static final boolean TEST_MODE = true;
    Button loginButton;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    DatabaseLib databaseLib;

    @Override
    public void onStart() { super.onStart(); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        databaseLib = new DatabaseLib(this);

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

        TextInputEditText editTextUsername, editTextPassword;
        TextView registerNow, forgotButton;

        editTextUsername = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
        registerNow = findViewById(R.id.registerNow);
        forgotButton = findViewById(R.id.forgotPasswordButton);

        registerNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });

        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);
                startActivity(intent);
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username, password;
                username = String.valueOf(editTextUsername.getText()).trim();
                password = String.valueOf(editTextPassword.getText()).trim();

                if(username.contains("@") || username.contains(".")) {
                    Toast.makeText(Login.this, "Invalid username", Toast.LENGTH_SHORT).show();
                    return;
                }

                databaseLib.getEmailByUsername(username, new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String email = dataSnapshot.getValue(String.class);

                            databaseLib.loginUser(username, email, password, "caregiver");
                        } else {
                            Toast.makeText(Login.this, "Username not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle the database error
                        Toast.makeText(Login.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
                String pin, email;
                pin = String.valueOf(editTextPin.getText());
                SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                email = prefs.getString("elderlyMail", "");

                databaseLib.loginUser("", email, pin, "elderly");
            }
        });
    }

    private void showFirstTimeUseLayout() {
        Intent intent = new Intent(getApplicationContext(), FirstTimeUse.class);
        startActivity(intent);
        finish();
    }
}