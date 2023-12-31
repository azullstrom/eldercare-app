package com.example.eldercare.account_view;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.eldercare.R;
import com.example.eldercare.modules.DatabaseLib;
import com.example.eldercare.modules.FireBaseMessageReceiver;
import com.example.eldercare.modules.LanguageManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FirstTimeUse extends AppCompatActivity {

    Button elderlyButton, caregiverButton;
    FirebaseAuth mAuth;
    DatabaseLib databaseLib ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Ask for permission to use notifications (only asks if permission not granted)
        if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{POST_NOTIFICATIONS}, 1);
        }
        //FireBaseMessageReceiver is initialized so notifications can be received
        FireBaseMessageReceiver rec = new FireBaseMessageReceiver();

        setContentView(R.layout.activity_first_time_use);

        mAuth = FirebaseAuth.getInstance();
        elderlyButton = findViewById(R.id.elderlyButton);
        caregiverButton = findViewById(R.id.caregiverButton);
        databaseLib= new DatabaseLib(this);


        // Code to manage changing the language
        ////////// image views for the language switcher //////////
        ImageView englishLang = findViewById(R.id.englishLang);
        ImageView swedishLang = findViewById(R.id.swedishLang);

        ////////// Set languageSwitcher visibility //////////
        String currentLanguage = LanguageManager.getLanguage(this);
        englishLang.setVisibility(currentLanguage.equals("sv") ? View.VISIBLE : View.GONE);
        swedishLang.setVisibility(currentLanguage.equals("en") ? View.VISIBLE : View.GONE);

        elderlyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showElderlyMailDialog();
            }
        });

        caregiverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFirstTimeUsePreferences(false, true);
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setFirstTimeUsePreferences(boolean isFirstTimeUse, boolean isCaregiver) {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isFirstTimeUse", isFirstTimeUse);
        editor.putBoolean("isCaregiver", isCaregiver);
        editor.apply();
    }

    private void setFirstTimeUsePreferences(boolean isFirstTimeUse, boolean isCaregiver, String elderlyMail) {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("elderlyMail", elderlyMail);
        editor.putBoolean("isFirstTimeUse", isFirstTimeUse);
        editor.putBoolean("isCaregiver", isCaregiver);
        editor.apply();
    }

    private void showElderlyMailDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.activity_elderly_emailpopup, null);

        TextInputEditText username = dialogView.findViewById(R.id.username);
        TextInputEditText pin = dialogView.findViewById(R.id.pin);

        new AlertDialog.Builder(FirstTimeUse.this)
                .setView(dialogView)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String usernameElderly = username.getText().toString().trim();
                        String elderlyMail =  usernameElderly + "@elderly.eldercare.com";
                        String elderlyPin = pin.getText().toString().trim() + "00";
                        if (TextUtils.isEmpty(usernameElderly) || TextUtils.isEmpty(elderlyPin)) {
                            Toast.makeText(FirstTimeUse.this, "All fields required.", Toast.LENGTH_SHORT).show();
                        } else {
                            mAuth.signInWithEmailAndPassword(elderlyMail, elderlyPin)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                setFirstTimeUsePreferences(false, false, elderlyMail);
                                                //Add getElderlyIdByEmail and send Id by Intent to ElderlyOverview
                                                databaseLib.getElderlyIdByEmail(elderlyMail, new DatabaseLib.StringCallback() {
                                                    @Override
                                                    public void onFound(String elderlyId) {
                                                        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = prefs.edit();
                                                        editor.putString("elderlyId", elderlyId);
                                                        editor.apply();

                                                        Intent intent = new Intent(getApplicationContext(), Login.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onNotFound() {

                                                    }

                                                    @Override
                                                    public void onError(String errorMessage) {

                                                    }
                                                });

                                            } else {
                                                // If sign in fails, display a message to the user.
                                                Toast.makeText(FirstTimeUse.this, "Authentication failed.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    // Code to manage changing the language
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