package com.example.eldercare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
        dateOfBirth  = getIntent().getStringExtra("elderlyYear");


        ////////// image views for the language switcher //////////
        ImageView englishLang = findViewById(R.id.englishLang);
        ImageView swedishLang = findViewById(R.id.swedishLang);

        ////////// Set languageSwitcher visibility //////////
        String currentLanguage = LanguageManager.getLanguage(this);
        englishLang.setVisibility(currentLanguage.equals("sv") ? View.VISIBLE : View.GONE);
        swedishLang.setVisibility(currentLanguage.equals("en") ? View.VISIBLE : View.GONE);



        // Set Elderly name in the layout
        patientNameTextView.setText(elderlyName);

        // Find clickable LinearLayouts
        LinearLayout backToPatientListLinearLayout   = findViewById(R.id.backToPatientsList);
        LinearLayout mealCardLinearLayout            = findViewById(R.id.mealsCard);
        LinearLayout allergiesCardLinearLayout       = findViewById(R.id.allergiesCard);
        LinearLayout alertHistoryCardLinearLayout    = findViewById(R.id.alertHistoryCard);
        LinearLayout elderlySettingsCardLinearLayout = findViewById(R.id.elderlySettingsCard);



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
            // TODO: waiting for the allergies class
            // startTargetActivity(allergies.class);
        });

        // OnClickListener for the alertHistoryCardLinearLayout
        alertHistoryCardLinearLayout.setOnClickListener(v -> {
            // Start alertHistory activity
            // TODO: waiting for the alertHistory class
            // startTargetActivity(alertHistory.class);
        });

        // OnClickListener for the elderlySettingsCardLinearLayout
        elderlySettingsCardLinearLayout.setOnClickListener(v -> {
            // Start elderlySettings activity
            startTargetActivity(CaregiverElderlySettingsActivity.class);
        });

    }

    // startTargetActivity method is used to send the elderly name and date of birth to the targeted class
    private void startTargetActivity(Class<?> targetClass) {
        Intent intent = new Intent(CaregiverElderlyOverviewActivity.this, targetClass);
        intent.putExtra("elderlyName", elderlyName);
        intent.putExtra("dateOfBirth", dateOfBirth);
        startActivity(intent);

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
