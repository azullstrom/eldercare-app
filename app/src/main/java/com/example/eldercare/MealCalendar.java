package com.example.eldercare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MealCalendar extends AppCompatActivity {

    FirebaseAuth auth;
    Button backButton;
    Button[] mealButtons;
    FirebaseUser user;

    LinearLayout mealButtonsLayout;

    String fakeMeals[][];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_calendar);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        fakeMeals = new String[][] {{"Breakfast", "Apple"}, {"Lunch", "Lemon"}, {"Dinner", "Nuggets"}};

        backButton = findViewById(R.id.backButton);
        mealButtonsLayout = findViewById(R.id.mealButtonsLayout);

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        for(int i = 0; i < fakeMeals.length; i++){
            Button mealButton = new Button(this);
            mealButton.setId(View.generateViewId());
            mealButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
            mealButton.setText(fakeMeals[i][0] + ": " + fakeMeals[i][1]);
            mealButtonsLayout.addView(mealButton);
            int finalI = i;
            mealButton.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), MealCalendarEdit.class);
                    intent.putExtra("mealName", fakeMeals[finalI][1]);
                    intent.putExtra("mealPos", finalI);
                    startActivity(intent);
                }
            });
        }


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Change intent to patient home page
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}