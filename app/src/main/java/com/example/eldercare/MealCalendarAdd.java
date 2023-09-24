package com.example.eldercare;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MealCalendarAdd extends AppCompatActivity {
    TextInputEditText editMealName, editAddMealType;
    CalendarView addMealCalendar;
    TimePicker timePicker;
    RelativeLayout addMealButton;
    ImageView exitButton;
    Meal meal;
    String elderlyName, elderlyYear;
    DatabaseLib database;
    int width, height;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mealcalendar_add);

        editMealName = findViewById(R.id.editMealName);
        editAddMealType = findViewById(R.id.editAddMealType);
        addMealCalendar = findViewById(R.id.addMealCalendar);
        timePicker = findViewById(R.id.timePicker);
        exitButton = findViewById(R.id.exitAddMeal);
        addMealButton = findViewById(R.id.addNewMealButton);

        elderlyName = getIntent().getStringExtra("elderlyName");
        elderlyYear = getIntent().getStringExtra("elderlyYear");

        database = new DatabaseLib(this);

        timePicker.setIs24HourView(true);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        
        meal = new Meal();
        meal.setDate(df.format(Calendar.getInstance().getTime()));
        meal.setTime(timePicker.getHour() + ":" + timePicker.getMinute());

        exitButton.setOnClickListener(view -> finish());

        addMealButton.setOnClickListener(view -> {
            meal.setMealType(String.valueOf(editAddMealType.getText()));
            meal.setToEat(String.valueOf(editMealName.getText()));
            if(meal.getToEat().matches("") || meal.getMealType().matches("")){
                Toast.makeText(MealCalendarAdd.this, R.string.please_enter_all_info, Toast.LENGTH_SHORT).show();
                return;
            }
            database.addMealToElderly(meal.getToEat(), elderlyName, elderlyYear, meal.getDate(), meal.getTime(), meal.getMealType());
            finish();
        });

        timePicker.setOnTimeChangedListener((timePicker, i, i1) -> {
            String time = "";
            if(i < 10){
                time += "0";
            }
            time += i + ":";
            if(i1 < 10){
                time +=  "0";
            }
            time += i1;
            meal.setTime(time);
        });
        addMealCalendar.setOnDateChangeListener((calendarView, i, i1, i2) -> {
            String date = i + "-";

            //Weird bug where i1 (month) is -1 what its supposed to be
            i1 += 1;

            if(i1 < 10){
                date += 0;
            }
            date += i1 + "-";
            if(i2 < 10){
                date += 0;
            }
            date += i2;
            meal.setDate(date);
        });
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;
        getWindow().setLayout((int) (width*0.9), (int) (height*0.9));
    }
}