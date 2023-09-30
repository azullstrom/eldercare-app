package com.example.eldercare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
    ImageView backButton;
    LinearLayout mealButtonsLayout;
    ImageView addMealButton;
    RelativeLayout dimLayout;
    DatabaseLib database;
    String elderlyName, elderlyYear;
    
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
     */
    void displayMealsDate(String elderlyName, String elderlyYear){
        mealButtonsLayout.removeAllViews();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("elderly-users/" + elderlyName + elderlyYear + "/meals");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
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
            case "snack1":
                mealType.setText(R.string.snack1);
                break;
            case "snack2":
                mealType.setText(R.string.snack2);
                break;
            case "snack3":
                mealType.setText(R.string.snack3);
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
            intent.putExtra("mealType", meal.getMealType());
            intent.putExtra("mealEaten", meal.isEaten());
            intent.putExtra("elderlyName", elderlyName);
            intent.putExtra("elderlyYear", elderlyYear);
            animateActivityAlpha(0, 255);
            startActivity(intent);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_calendar);

        //TODO: when all intents are pushed, this will work without substring
        String elderlyInfo = getIntent().getStringExtra("elderlyName");
        elderlyName = elderlyInfo.substring(0, elderlyInfo.length() - 4);
        elderlyYear = elderlyInfo.substring(elderlyInfo.length()-4);

        database = new DatabaseLib(this);

        backButton = findViewById(R.id.backButton);
        mealButtonsLayout = findViewById(R.id.mealButtonsLayout);
        addMealButton = findViewById(R.id.addMealButton);


        dimLayout = findViewById(R.id.dimLayout);
        dimLayout.getForeground().setAlpha(0);

        backButton.setOnClickListener(view -> {
            finish();
        });

        addMealButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MealCalendarAdd.class);
            intent.putExtra("elderlyName", elderlyName);
            intent.putExtra("elderlyYear", elderlyYear);
            animateActivityAlpha(0, 255);
            startActivity(intent);
        });
    }

    /** Animates activity color when returning from edit/create
     *  displays meals after a short delay
     */
    @Override
    protected void onResume() {
        super.onResume();
        animateActivityAlpha(255, 0);
        mealButtonsLayout.removeAllViews();
        final Handler handler = new Handler();
        //delay before viewing meals so that database can catch up
        handler.postDelayed(() -> {
            displayMealsDate(elderlyName, elderlyYear);
        }, 1000);
    }
}