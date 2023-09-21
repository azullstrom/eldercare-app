package com.example.eldercare;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class MealCalendarEdit extends AppCompatActivity {

    TextInputEditText editMealName;
    ImageView exit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_calendar_edit);

        editMealName = findViewById(R.id.editMealName);
        //TODO: add all getStringExtra
        //editMealName.setText(getIntent().getStringExtra("mealName"));
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        //TODO: change so that save and edit button have dynamic text so that it can be translated
        getWindow().setLayout((int) (width*0.9), (int) (height*0.5));

        exit = findViewById(R.id.exitEditMeal);
        exit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}