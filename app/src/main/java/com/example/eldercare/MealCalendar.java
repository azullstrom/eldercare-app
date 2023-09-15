package com.example.eldercare;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MealCalendar extends AppCompatActivity {

    FirebaseAuth auth;
    Button backButton;
    Button[] mealButtons;
    FirebaseUser user;

    LinearLayout mealButtonsLayout;

    LinearLayout dimLayout;
    String fakeMeals[][];

    void animateBackground(int from, int to){
        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(from, to);
        anim.setEvaluator(new ArgbEvaluator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
               dimLayout.setBackgroundColor((Integer)valueAnimator.getAnimatedValue());
            }
        });
        anim.setDuration(300);
        anim.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_calendar);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        fakeMeals = new String[][] {{"Breakfast", "Apple"}, {"Lunch", "Lemon"}, {"Dinner", "Nuggets"}};

        backButton = findViewById(R.id.backButton);
        mealButtonsLayout = findViewById(R.id.mealButtonsLayout);

        dimLayout = findViewById(R.id.dimLayout);
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }



        for(int i = 0; i < fakeMeals.length; i++){
            Button mealButton = new Button(this);
            mealButton.setId(View.generateViewId());
            mealButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
            mealButton.setText(fakeMeals[i][0] + ": " + fakeMeals[i][1]);
            mealButtonsLayout.addView(mealButton);
            int finalI = i;
            mealButton.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), MealCalendarEdit.class);
                    intent.putExtra("mealName", fakeMeals[finalI][1]);
                    intent.putExtra("mealPos", finalI);
                    //dimLayout.setBackgroundColor(0x80000000);
                    animateBackground(0xffffff, 0x80000000);
                    startActivity(intent);
                }
            });
        }


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Change intent to patient home page
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        animateBackground(0x80000000, 0xffffff);
    }
}