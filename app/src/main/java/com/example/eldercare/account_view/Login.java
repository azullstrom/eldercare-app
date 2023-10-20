package com.example.eldercare.account_view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eldercare.R;
import com.example.eldercare.modules.DatabaseLib;
import com.example.eldercare.modules.LanguageManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private static final boolean TEST_MODE = false;
    Button loginButton;
    ProgressBar progressBar;
    DatabaseLib databaseLib;
    boolean rememberMe;

    @Override
    public void onStart() { super.onStart(); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseLib = new DatabaseLib(this);



        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstTimeUse = prefs.getBoolean("isFirstTimeUse", true);
        rememberMe = prefs.getBoolean("rememberMe", false);

        // language default //////////////////////////////////////////////////////////
        if(LanguageManager.getLanguageFromsharedprefs(this) != "en" && !prefs.getBoolean("rememberme", false)){
            LanguageManager.setDefaultLanguage(this);
            LanguageManager.setLanguage(this, LanguageManager.getLanguageFromsharedprefs(this));
        }

        // If the coder wants to test the FirstTimeUse page each time the app starts
        if(TEST_MODE) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isFirstTimeUse", true);
            editor.putBoolean("rememberMe", false);
            editor.apply();
        }

        if(firstTimeUse) {
            showFirstTimeUseLayout();
        }
        boolean isCaregiver = prefs.getBoolean("isCaregiver", true);

        if(isCaregiver) {
            showLoginCaregiverLayout();
        } else {
            if(rememberMe) {
                String email = prefs.getString("elderlyMail", "");
                String pin = prefs.getString("elderlyPin", "");
                databaseLib.loginUser("", email, pin, "elderly", new DatabaseLib.SuccessCallback() {
                    @Override
                    public void onSuccess() {
                        // Handle success
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        // Handle failure
                    }
                });
            } else {
                showLoginElderlyLayout();
            }
        }

        // Code to manage changing the language
        ////////// image views for the language switcher //////////
        ImageView englishLang = findViewById(R.id.englishLang);
        ImageView swedishLang = findViewById(R.id.swedishLang);

        ////////// Set languageSwitcher visibility //////////
        String currentLanguage = LanguageManager.getLanguageFromsharedprefs(this);
        if(englishLang != null) {
            englishLang.setVisibility(currentLanguage.equals("sv") ? View.VISIBLE : View.GONE);
        }
        if(swedishLang != null) {
            swedishLang.setVisibility(currentLanguage.equals("en") ? View.VISIBLE : View.GONE);
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

                databaseLib.getCaregiverEmailByUsername(username, new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String email = dataSnapshot.getValue(String.class);

                            databaseLib.loginUser(username, email, password, "caregiver", new DatabaseLib.SuccessCallback() {
                                @Override
                                public void onSuccess() {
                                    // Handle success
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    // Handle failure
                                }
                            });
                        } else {
                            Toast.makeText(Login.this, "Username not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Login.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void showLoginElderlyLayout() {
        setContentView(R.layout.activity_login_elderly);

        TextInputEditText editTextPin;
        CheckBox checkBoxRememberMe;

        editTextPin = findViewById(R.id.pin);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
        checkBoxRememberMe = findViewById(R.id.rememberMeCheckbox);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pin, email;
                pin = String.valueOf(editTextPin.getText());
                SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                email = prefs.getString("elderlyMail", "");

                databaseLib.loginUser("", email, pin, "elderly", new DatabaseLib.SuccessCallback() {
                    @Override
                    public void onSuccess() {
                        // The user is successfully logged in, now check if "Remember Me" is checked
                        if (checkBoxRememberMe.isChecked()) {
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("rememberMe", true);
                            editor.putString("elderlyPin", pin);
                            editor.apply();
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                        // Handle login failure
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

    ////////// Change lang to the selected lang then refresh //////////
    public void changeLanguageToEnglish(View view) {
        LanguageManager.setLanguage(this, "en");
        recreate();
    }

    ////////// Change the app's locale to Swedish //////////
    public void changeLanguageToSwedish(View view) {
        LanguageManager.setLanguage(this, "sv");
        recreate();
    }
}