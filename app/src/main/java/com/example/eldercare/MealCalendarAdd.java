package com.example.eldercare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
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
    String mealToEat, mealType, mealTime, mealDate, elderlyName, elderlyYear;
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
        mealDate = df.format(Calendar.getInstance().getTime());
        mealTime = timePicker.getHour() + ":" + timePicker.getMinute();

        exitButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Exits the activity and goes back to mealCalendar
             * @param view view object
             */
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        addMealButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Adds a meal to the database
             * @param view view object
             */
            @Override
            public void onClick(View view) {
                mealType = String.valueOf(editAddMealType.getText());
                mealToEat = String.valueOf(editMealName.getText());
                if(mealToEat.matches("") || mealType.matches("")){
                    Toast.makeText(MealCalendarAdd.this, R.string.please_enter_all_info, Toast.LENGTH_SHORT).show();
                    return;
                }
                database.addMealToElderly(mealToEat, elderlyName, elderlyYear, mealDate, mealTime, mealType);
                finish();
            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            /**
             * Sets the selected time to mealTime in the format "hh:mm"
             *
             * @param timePicker timePicker object
             * @param i selected hour from timepicker
             * @param i1 selected minutes from timepicker
             */
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                String time = "";
                if(i < 10){
                    time += "0";
                }
                time += i + ":";
                if(i1 < 10){
                    time +=  "0";
                }
                time += i1;
                mealTime = time;
            }
        });
        addMealCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            /**
             * Sets the selected date to mealDate in the format "yyyy-MM-dd"
             *
             * @param calendarView Calendar object
             * @param i selected year from calendar
             * @param i1 selected month from calendar
             * @param i2 selected day from calendar
             */
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
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
                mealDate = date;
            }
        });
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;
        getWindow().setLayout((int) (width*0.9), (int) (height*0.9));
    }
}