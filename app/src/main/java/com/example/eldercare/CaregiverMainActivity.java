package com.example.eldercare;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class CaregiverMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_main);

        TextView welcomeTextView = findViewById(R.id.your_patients);
        welcomeTextView.setText("Your Patients");
    }
}
