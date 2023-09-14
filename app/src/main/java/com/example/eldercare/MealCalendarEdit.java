package com.example.eldercare;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class MealCalendarEdit extends AppCompatActivity {

    TextInputEditText editMealName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_calendar_edit);

        editMealName = findViewById(R.id.editMealName);
        editMealName.setText(getIntent().getStringExtra("mealName"));
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width*0.9), (int) (height*0.5));
    }
}