package com.example.eldercare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MealCalendarConfirmDelete extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_calendar_confirm_delete);

        RelativeLayout deleteConfirmButton = findViewById(R.id.deleteConfirmButton);
        ImageView exitConfirmDeleteMeal = findViewById(R.id.exitConfirmDeleteMeal);

        DatabaseLib database = new DatabaseLib(this);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width*0.9), (int) (height*0.41));

        exitConfirmDeleteMeal.setOnClickListener(view -> finish());
        deleteConfirmButton.setOnClickListener(view -> {
            database.removeMealFromElderly(getIntent().getStringExtra("elderlyName"),
                    getIntent().getStringExtra("elderlyYear"),
                    getIntent().getStringExtra("mealType"));
            finish();
        });
    }
}