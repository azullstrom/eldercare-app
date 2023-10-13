package com.example.eldercare;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

        databaseLib.getMealHistoryElderly(elderlyName, elderlyYear, new DatabaseLib.MealHistoryCallback() {
            @Override
            public void onNotificationReceived(ArrayList<String> notifications) {
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
        LinearLayout alertCard = new LinearLayout(this);
        alertCard.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300, 0));
        alertCard.setBackgroundResource(R.drawable.meal_card);
        alertCard.setOrientation(LinearLayout.VERTICAL);
        alertCard.setPadding(60,50,0,40);

        TextView alertDate = new TextView(this);
        alertDate.setText(date);
        TextView alertTitle = new TextView(this);
        alertTitle.setText(title);
        TextView alertText = new TextView(this);
        alertText.setText(text);

        alertDate.setTextSize(20);
        alertDate.setTextColor(Color.parseColor("#432c81"));
        alertDate.setPadding(0,0,0,10);
        alertCard.addView(alertDate);
        alertCard.addView(alertTitle);
        alertCard.addView(alertText);

        historyLayout.addView(alertCard);
    }
}