package com.example.eldercare;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class CaregiverElderlyOverviewActivity extends AppCompatActivity {

    private TextView patientNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_elderly_overview);

        patientNameTextView = findViewById(R.id.patient_name_placeholder);
        // Expecting to send name and dateOfBirth when anybody calls this activity/class
        String elderlyName = getIntent().getStringExtra("elderlyName");
        patientNameTextView.setText(elderlyName);



    }
}
