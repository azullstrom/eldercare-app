package com.example.eldercare.caregiver_view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.eldercare.R;
import com.example.eldercare.modules.DatabaseLib;

public class CaregiverElderlyNotifications extends AppCompatActivity {

    String elderlyName, elderlyYear;
    DatabaseLib databaseLib;
    ImageView backButton;
    LinearLayout alertLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_elderly_notifications);
        elderlyName = getIntent().getStringExtra("elderlyName");
        elderlyYear = getIntent().getStringExtra("dateOfBirth");

        databaseLib = new DatabaseLib(this);

        backButton = findViewById(R.id.alert_back_button);
        alertLayout = findViewById(R.id.alertLayout);

        backButton.setOnClickListener(view -> finish());

        databaseLib.getNotificationHistoryElderly(elderlyName, elderlyYear, notifications -> {
            for(int i = notifications.size()-1; i > 0; i--){
                //Notifcations consist of list like this {title, text, title, text} which
                //means that we create an alert card every third time (i%3 == 0)
                if(i%3 == 0){
                    createAlertCard(notifications.get(i), notifications.get(i+1), notifications.get(i+2));
                }
            }
        });
    }

    private void createAlertCard(String date, String title, String text){
        LinearLayout alertLayout = findViewById(R.id.alertLayout);

        LayoutInflater inflater = LayoutInflater.from(CaregiverElderlyNotifications.this);
        View customView;
        TextView dateText, titleText, textText;

        if(title.matches("Elderly Alarm")){
            customView = inflater.inflate(R.layout.alert_card, null);
            dateText = customView.findViewById(R.id.alert_date);
            titleText = customView.findViewById(R.id.alert_title);
            textText = customView.findViewById(R.id.alert_text);
            customView.setTag(dateText);
        }
        else{
            customView = inflater.inflate(R.layout.missed_meal_card, null);
            dateText = customView.findViewById(R.id.missed_meal_date);
            titleText = customView.findViewById(R.id.missed_meal_title);
            textText = customView.findViewById(R.id.missed_meal_text);
            customView.setTag(dateText);
        }

        alertLayout.addView(customView);
        dateText.setText(date);
        titleText.setText(title);
        textText.setText(text);
    }
}