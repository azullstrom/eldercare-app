package com.example.eldercare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MealCalendar extends AppCompatActivity{
    FirebaseAuth auth;
    ImageView backButton;
    FirebaseUser currentCareGiver;
    LinearLayout mealButtonsLayout;
    ImageView addMealButton;
    RelativeLayout dimLayout;
    DatabaseLib database;
    CalendarView calendar;
    String elderlyName, elderlyYear;
    String selectedDate;
    
    /** Animates activity foreground alpha from startAlpha to endAlpha
     *
     * @param startAlpha value between 0-255 where 0 is transparent and 255 is opaque
     * @param endAlpha   value between 0-255 where 0 is transparent and 255 is opaque
     */
    void animateActivityAlpha(int startAlpha, int endAlpha){
        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(startAlpha, endAlpha);
        anim.setEvaluator(new ArgbEvaluator());
        anim.addUpdateListener(valueAnimator ->
                dimLayout.getForeground().setAlpha((Integer)valueAnimator.getAnimatedValue()));
        anim.setDuration(300);
        anim.start();
    }

    /** Displays buttons in a linear view for all the meals an elderly has that date
     *
     * @param elderlyName the name of the elderly
     * @param elderlyYear the year which the elderly is born
     * @param date the date of which meals to display
     */
    void displayMealsDate(String elderlyName, String elderlyYear, String date){
        mealButtonsLayout.removeAllViews();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("elderly-users/" + elderlyName + elderlyYear + "/meals/" + date);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //TODO: should meals have id? Right now there can only be one meal in each mealType
                //
                for (DataSnapshot mealTypeSnapshot: snapshot.getChildren()){
                    Meal meal = mealTypeSnapshot.getValue(Meal.class);
                    createMealButton(meal);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MealCalendar.this, R.string.error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Creates a button for a specific meal
     *
     * @param meal the meal to create a button for
     */
    void createMealButton(Meal meal){
        LinearLayout mealButton = new LinearLayout(this);
        mealButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300, 0));
        mealButton.setBackgroundResource(R.drawable.meal_card);
        mealButton.setOrientation(LinearLayout.VERTICAL);
        mealButton.setPadding(60,50,0,40);

        TextView mealType = new TextView(this);
        switch (meal.getMealType()){
            case "breakfast":
                mealType.setText(R.string.breakfast);
                break;
            case "dinner":
                mealType.setText(R.string.dinner);
                break;
            case "lunch":
                mealType.setText(R.string.lunch);
                break;
        }
        mealType.setTextSize(20);
        mealType.setTextColor(Color.parseColor("#432c81"));
        mealType.setPadding(0,0,0,10);
        mealButton.addView(mealType);

        TextView mealToEat = new TextView(this);
        mealToEat.setText(meal.getToEat());
        mealType.setTextColor(Color.parseColor("#7b6ba8"));
        mealButton.addView(mealToEat);

        mealButtonsLayout.addView(mealButton);
        mealButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MealCalendarEdit.class);
            intent.putExtra("mealToEat", meal.getToEat());
            intent.putExtra("mealTime", meal.getTime());
            intent.putExtra("mealDate", meal.getDate());
            intent.putExtra("mealType", meal.getMealType());
            intent.putExtra("elderlyName", elderlyName);
            intent.putExtra("elderlyYear", elderlyYear);
            animateActivityAlpha(0, 255);
            startActivity(intent);
        });
    }

    /** Returns a string of the current date in the format "yyyy-mm-dd"
     *
     * @return current date in format "yyyy-mm-dd"
     */
    String getCalendarDate(){
        Date date = new Date(calendar.getDate());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_calendar);

        //TODO: get elderlyName/year from intent.getExtra() from previous activity
        elderlyName = "Greger";
        elderlyYear = "1922";


        auth = FirebaseAuth.getInstance();
        currentCareGiver = auth.getCurrentUser();
        database = new DatabaseLib(this);

        backButton = findViewById(R.id.backButton);
        mealButtonsLayout = findViewById(R.id.mealButtonsLayout);
        calendar = findViewById(R.id.mealCalendar);
        addMealButton = findViewById(R.id.addMealButton);

        selectedDate = getCalendarDate();

        dimLayout = findViewById(R.id.dimLayout);
        dimLayout.getForeground().setAlpha(0);
        if (currentCareGiver == null) {
            Intent intent = new Intent(getApplicationContext(), ElderlyMainActivity.class);
            startActivity(intent);
            finish();
        }

        displayMealsDate(elderlyName, elderlyYear, getCalendarDate());

        backButton.setOnClickListener(view -> {
            //TODO: Change intent to patient home page
            Intent intent = new Intent(getApplicationContext(), CaregiverMainActivity.class);
            startActivity(intent);
            finish();
        });

        addMealButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MealCalendarAdd.class);
            intent.putExtra("elderlyName", elderlyName);
            intent.putExtra("elderlyYear", elderlyYear);
            animateActivityAlpha(0, 255);
            startActivity(intent);
        });

        calendar.setOnDateChangeListener((calendarView, i, i1, i2) -> {
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
            selectedDate = date;
            displayMealsDate(elderlyName, elderlyYear, date);
        });
    }

    /** Animates activity color when returning from edit/create
     *
     */
    @Override
    protected void onResume() {
        super.onResume();
        animateActivityAlpha(255, 0);
        displayMealsDate(elderlyName, elderlyYear, selectedDate);
    }
}