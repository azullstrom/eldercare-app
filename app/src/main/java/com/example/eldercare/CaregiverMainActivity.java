package com.example.eldercare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class CaregiverMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_main);

        //Things that Will be moved somewhere else
        TextView welcomeTextView = findViewById(R.id.your_patients);
        welcomeTextView.setText("Your Patients");
        ImageView noPatientsImageView = findViewById(R.id.noPatientsImageView);
        TextView noPatientsTextView = findViewById(R.id.noPatientsTextView);
        ImageButton addPatientButton = findViewById(R.id.addImageButton);
        addPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open new activity (add etc)
                Intent intent = new Intent(CaregiverMainActivity.this, CaregiverMainActivity.class);
                startActivity(intent);
            }
        });

    }

    protected boolean patientsExists() {
        //If not exists -> Show image, "no patients text" and plus Imagebutton
        //If exists -> Show patients (another method)
        //TODO
        return true;
    }

    protected void addPatient() {
        //TODO
    }
}
