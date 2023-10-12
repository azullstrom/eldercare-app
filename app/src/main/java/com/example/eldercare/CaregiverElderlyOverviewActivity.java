package com.example.eldercare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;


public class CaregiverElderlyOverviewActivity extends AppCompatActivity {

    // Strings to store the date of birth and elderly/patient name
    private String elderlyName, dateOfBirth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set current activity to activity_caregiver_elderly_overview
        setContentView(R.layout.activity_caregiver_elderly_overview);

        // Get the name of the Elderly/patient from Intent
        TextView patientNameTextView = findViewById(R.id.patient_name_placeholder);
        elderlyName  = getIntent().getStringExtra("elderlyName");
        // Anders: Ändrade strängen så att den stämmer överens med den som skickar.
        dateOfBirth  = getIntent().getStringExtra("dateOfBirth");



        // Set Elderly name in the layout
        patientNameTextView.setText(elderlyName);

        // Find clickable LinearLayouts
        LinearLayout backToPatientListLinearLayout   = findViewById(R.id.backToPatientsList);
        LinearLayout mealCardLinearLayout            = findViewById(R.id.mealsCard);
        LinearLayout allergiesCardLinearLayout       = findViewById(R.id.allergiesCard);
        LinearLayout alertHistoryCardLinearLayout    = findViewById(R.id.alertHistoryCard);
        //LinearLayout elderlySettingsCardLinearLayout = findViewById(R.id.elderlySettingsCard);



        // OnClickListener for the backToPatientListLinearLayout
        backToPatientListLinearLayout.setOnClickListener(v -> {
            // Close and finish the current activity
            finish();
        });

        // OnClickListener for the mealCardLinearLayout
        mealCardLinearLayout.setOnClickListener(v -> {
            // Start MealCalender
            startTargetActivity(MealCalendar.class);
        });

        // OnClickListener for the allergiesCardLinearLayout
        allergiesCardLinearLayout.setOnClickListener(v -> {
            // Start allergies activity
            startTargetActivity(AllergiesActivity.class);
        });

        // OnClickListener for the alertHistoryCardLinearLayout
        alertHistoryCardLinearLayout.setOnClickListener(v -> {
            // Start alertHistory activity
            startTargetActivity(CaregiverElderlyNotifications.class);
        });

        // OnClickListener for the elderlySettingsCardLinearLayout
        /*
        elderlySettingsCardLinearLayout.setOnClickListener(v -> {
            // Start elderlySettings activity
            startTargetActivity(CaregiverElderlySettingsActivity.class);
        });*/

    }

    // startTargetActivity method is used to send the elderly name and date of birth to the targeted class
    private void startTargetActivity(Class<?> targetClass) {
        Intent intent = new Intent(CaregiverElderlyOverviewActivity.this, targetClass);
        intent.putExtra("elderlyName", elderlyName);
        intent.putExtra("dateOfBirth", dateOfBirth);
        startActivity(intent);

    }

}
