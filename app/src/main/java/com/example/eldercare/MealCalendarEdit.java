package com.example.eldercare;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MealCalendarEdit extends AppCompatActivity {
    TextInputEditText editMealType, editMealToEat;
    TextInputLayout layoutMealToEat, layoutMealType;
    ImageView exit;
    RelativeLayout saveButton, deleteButton;
    CheckBox eatenBox;
    Meal meal;
    String typeInput, eatInput, elderlyName, elderlyYear;
    DatabaseLib database;
    int width, height;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_calendar_edit);
        database = new DatabaseLib(this);
        meal = new Meal();

        meal.setToEat(getIntent().getStringExtra("mealToEat"));
        meal.setTime(getIntent().getStringExtra("mealTime"));
        meal.setDate(getIntent().getStringExtra("mealDate"));
        meal.setMealType(getIntent().getStringExtra("mealType"));
        elderlyYear = getIntent().getStringExtra("elderlyYear");
        elderlyName = getIntent().getStringExtra("elderlyName");

        editMealType = findViewById(R.id.editMealType);
        layoutMealType = findViewById(R.id.layoutMealType);
        editMealToEat = findViewById(R.id.editMealToEat);
        layoutMealToEat = findViewById(R.id.layoutMealToEat);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);
        eatenBox = findViewById(R.id.eatenBox);

        layoutMealType.setHint(meal.getMealType());
        layoutMealToEat.setHint(meal.getToEat());
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;

        getWindow().setLayout((int) (width*0.9), (int) (height*0.41));

        exit = findViewById(R.id.exitEditMeal);
        exit.setOnClickListener(view -> finish());

        saveButton.setOnClickListener(view -> {
            typeInput = String.valueOf(editMealType.getText());
            eatInput = String.valueOf(editMealToEat.getText());
            if(typeInput.matches("")){
                typeInput = meal.getMealType();
            }
            if(eatInput.matches("")){
                eatInput = meal.getToEat();
            }
            if(!eatInput.matches(meal.getToEat())){
                database.setToEat(eatInput, elderlyName, elderlyYear, meal.getDate(), meal.getTime(), meal.getMealType());
            }
            if(!typeInput.matches(meal.getMealType())){
                //TODO: set meal type to new type, not added in database api yet
            }
            finish();
        });
        deleteButton.setOnClickListener(view -> {
            //TODO: add confirmation to delete
            Intent intent = new Intent(getApplicationContext(), MealCalendarConfirmDelete.class);
            intent.putExtra("elderlyName", elderlyName);
            intent.putExtra("elderlyYear", elderlyYear);
            intent.putExtra("mealDate", meal.getDate());
            intent.putExtra("mealType", meal.getMealType());
            startActivity(intent);
            finish();
        });

        eatenBox.setOnClickListener(view -> {
            //TODO: set eaten to true/false, not added in database structure yet
        });
    }
}