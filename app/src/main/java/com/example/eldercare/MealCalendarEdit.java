package com.example.eldercare;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
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

    String mealToEat, mealTime, mealDate, mealType;
    String typeInput, eatInput, elderlyName, elderlyYear;
    DatabaseLib database;
    int width, height;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_calendar_edit);

        database = new DatabaseLib(this);


        mealToEat = getIntent().getStringExtra("mealToEat");
        mealTime = getIntent().getStringExtra("mealTime");
        mealDate = getIntent().getStringExtra("mealDate");
        mealType = getIntent().getStringExtra("mealType");
        elderlyYear = getIntent().getStringExtra("elderlyYear");
        elderlyName = getIntent().getStringExtra("elderlyName");

        editMealType = findViewById(R.id.editMealType);
        layoutMealType = findViewById(R.id.layoutMealType);
        editMealToEat = findViewById(R.id.editMealToEat);
        layoutMealToEat = findViewById(R.id.layoutMealToEat);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);
        eatenBox = findViewById(R.id.eatenBox);

        layoutMealType.setHint(mealType);
        layoutMealToEat.setHint(mealToEat);
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
                typeInput = mealType;
            }
            if(eatInput.matches("")){
                eatInput = mealToEat;
            }
            //We have changed something, need to update meal
            if(!eatInput.matches(mealToEat)){
                database.setToEat(eatInput, elderlyName, elderlyYear, mealDate, mealTime, mealType);
            }
            if(!typeInput.matches(mealType)){
                //TODO: set meal type to new type, not added in database api yet
            }
            finish();
        });
        deleteButton.setOnClickListener(view -> {
            //TODO: add confirmation to delete
            database.removeMealFromElderly(elderlyName, elderlyYear, mealDate, mealType);
            finish();
        });

        eatenBox.setOnClickListener(view -> {
            //TODO: set eaten to true/false, not added in database structure yet "EATEN"
        });
    }
}