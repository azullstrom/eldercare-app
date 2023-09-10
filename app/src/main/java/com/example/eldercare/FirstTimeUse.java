package com.example.eldercare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FirstTimeUse extends AppCompatActivity {

    Button elderlyButton, caregiverButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_use);

        elderlyButton = findViewById(R.id.elderlyButton);
        caregiverButton = findViewById(R.id.caregiverButton);

        elderlyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFirstTimeUsePreferences(false, false);
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
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
}