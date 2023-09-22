package com.example.eldercare;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.eldercare.DatabaseLib;

public class MealCalendarEdit extends AppCompatActivity {

    TextInputEditText editMealType, editMealToEat;

    TextInputLayout layoutMealToEat, layoutMealType;
    ImageView exit;

    RelativeLayout saveButton, deleteButton;
    CheckBox eatenBox;

    String mealToEat, mealTime, mealDate, mealType;
    String typeInput, eatInput, elderlyId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_calendar_edit);

        DatabaseLib database = new DatabaseLib(this);


        mealToEat = getIntent().getStringExtra("mealToEat");
        mealTime = getIntent().getStringExtra("mealTime");
        mealDate = getIntent().getStringExtra("mealDate");
        mealType = getIntent().getStringExtra("mealType");
        elderlyId = getIntent().getStringExtra("elderlyId");

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
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        //TODO: change so that save and edit button have dynamic text so that it can be translated
        getWindow().setLayout((int) (width*0.9), (int) (height*0.4));

        exit = findViewById(R.id.exitEditMeal);
        exit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeInput = String.valueOf(editMealType.getText());
                eatInput = String.valueOf(editMealToEat.getText());
                if(typeInput.matches("")){
                    typeInput = mealType;
                }
                if(eatInput.matches("")){
                    eatInput = mealToEat;
                }
                //We have changed something, need to update meal
                if(!eatInput.matches(mealToEat) || !typeInput.matches(mealType)){
                    //TODO: removemealfromelderly should return true or false if it could be deleted
                    //TODO: add meal after remove is finished
                    database.removeMealFromElderly(elderlyId, mealDate, mealType);
                    database.addMealToElderly(eatInput, elderlyId, mealDate, mealTime, typeInput);
                }
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        eatenBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}