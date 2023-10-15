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

import java.util.ArrayList;

public class CaregiverElderlyMealHistory extends AppCompatActivity {
    String elderlyName, elderlyYear;
    DatabaseLib databaseLib;
    ImageView backButton;
    LinearLayout historyLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_elderly_meal_history);
        elderlyName = getIntent().getStringExtra("elderlyName");
        elderlyYear = getIntent().getStringExtra("dateOfBirth");

        databaseLib = new DatabaseLib(this);

        backButton = findViewById(R.id.history_back_button);
        historyLayout = findViewById(R.id.historyLayout);

        databaseLib.getMealHistoryElderly(elderlyName, elderlyYear, new DatabaseLib.ArrayListStringCallback() {
            @Override
            public void onFound(ArrayList<String> notifications) {
                for(int i = notifications.size()-1; i > 0; i--){
                    //Notifcations consist of list like this {title, text, title, text} which
                    //means that we create an alert card every third time (i%3 == 0)
                    if(i%3 == 0){
                        createAlertCard(notifications.get(i), notifications.get(i+2), notifications.get(i+1));
                    }
                }
            }
        });

        backButton.setOnClickListener(view -> finish());
    }
    private void createAlertCard(String date, String title, String text){
        LinearLayout historyLayout = findViewById(R.id.historyLayout);

        LayoutInflater inflater = LayoutInflater.from(CaregiverElderlyMealHistory.this);
        View customView;
        TextView dateText, titleText, textText;

        customView = inflater.inflate(R.layout.missed_meal_card, null);
        dateText = customView.findViewById(R.id.missed_meal_date);
        titleText = customView.findViewById(R.id.missed_meal_title);
        textText = customView.findViewById(R.id.missed_meal_text);
        customView.setTag(dateText);

        historyLayout.addView(customView);
        dateText.setText(date);
        titleText.setText(title);
        textText.setText(text);
    }
}