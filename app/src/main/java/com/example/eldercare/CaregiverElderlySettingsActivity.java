package com.example.eldercare;


import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CaregiverElderlySettingsActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set current activity to activity_caregiver_elderly_settings
        setContentView(R.layout.activity_caregiver_elderly_settings);

        // Get the name of the Elderly/patient from Intent
        TextView patientNameTextView = findViewById(R.id.patient_name_placeholder);
        String elderlyName  = getIntent().getStringExtra("elderlyName");


        // Set Elderly name in the layout
        patientNameTextView.setText(elderlyName);

        LinearLayout backToPatientListLinearLayout   = findViewById(R.id.backToPatientsList);


        // OnClickListener for the backToPatientListLinearLayout
        backToPatientListLinearLayout.setOnClickListener(v -> {
            // Start CaregiverMainActivity
            finish();
        });
    }
}
