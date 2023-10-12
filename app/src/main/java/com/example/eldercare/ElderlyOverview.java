package com.example.eldercare;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ElderlyOverview extends AppCompatActivity {
    String elderlyId, elderlyName, elderlyYear;
    TextView patientTitle;
    Button mealButton, alarmButton;
    NotificationLib notificationLib;
    DatabaseLib databaseLib;
    ArrayList<Meal> mealList;
    ArrayList<ArrayList> localMealTimerList;
    Context elderlyOverviewContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly);
        elderlyOverviewContext = this;

        databaseLib = new DatabaseLib(this);
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        elderlyId = prefs.getString("elderlyId", "");
        elderlyName = elderlyId.substring(0, elderlyId.length()-4);
        elderlyYear = elderlyId.substring(elderlyId.length()-4);

        patientTitle = findViewById(R.id.patientTitle);
        //TODO: add to strings.xml
        patientTitle.setText("Hi "+ elderlyName + " \uD83D\uDC4B\uD83C\uDFFB");
        localMealTimerList = new ArrayList<>();


        mealButton = findViewById(R.id.mealButtonElderly);
        alarmButton = findViewById(R.id.buttonAlarm);

        refreshMealButtonEveryTenSeconds();
        markMealsAsUneatenAtMidnight();

        alarmButton.setOnClickListener(view -> {
            sendNotificationToAllCaregivers("Elderly Alarm", elderlyId + " has requested help");
        });


    }

    /**
     * Returns the next meal. If all meals of the day are eaten it returns the first meal to eat
     * the following day
     * @return Meal which is next in line to be eaten
     */
    private Meal getNextMeal() {
        Meal nextMeal = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalTime mealTime;
            long deltaTime;
            long shortestTime = 999999999;
            //set nextMeal to closest meal which is not eaten this day
            for (Meal meal : mealList) {
                    if(meal.isEaten()){
                        continue;
                    }
                    mealTime = LocalTime.parse(meal.getTime());
                    deltaTime = Duration.between(LocalTime.parse("00:00"), mealTime).toMillis();
                    if(deltaTime > 0 && deltaTime < shortestTime){
                        nextMeal = meal;
                        shortestTime = deltaTime;
                    }
            }
            //No meal this day, get first meal from next day
            if(nextMeal == null){
                for(Meal meal: mealList){
                    mealTime = LocalTime.parse(meal.getTime());
                    deltaTime = Duration.between(LocalTime.parse("00:00"), mealTime).toMillis();
                    if(deltaTime < shortestTime){
                        nextMeal = meal;
                        shortestTime = deltaTime;
                    }
                }
            }
        }
        return nextMeal;
    }

    /**
     * Schedules three notifications for each meal. The first notification only appears on the
     * elderly phone. The second notification appears on elderly phone and is also added to
     * notification history. Third notification is also added to notification history
     * and also sends a notification to all caregivers that the elderly has missed a meal.
     */
    private void scheduleMealNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            for (Meal meal : mealList) {
                for(ArrayList list: localMealTimerList){
                    Timer timer = (Timer) list.get(1);
                    timer.cancel();
                }
                if(meal.isEaten()){
                    continue;
                }
                localMealTimerList = new ArrayList<>();
                LocalTime mealTime = LocalTime.parse(meal.getTime());
                LocalTime mealTime2 = mealTime.plusMinutes(1);
                LocalTime mealTime3 = mealTime2.plusMinutes(1);

                scheduleElderlyMealNotification(meal, mealTime, false, false);
                scheduleElderlyMealNotification(meal, mealTime2, true, false);
                scheduleElderlyMealNotification(meal, mealTime3, true, true);
            }
        }
    }

    /**
     * Displays the next meal on mealButton, and changes mealButton to be clickable when a meal
     * is to be registered. Also cancels all remaining scheduled notifications when
     * mealButton is clicked.
     */
    private void updateMeals(){
        databaseLib.getMeals(elderlyId, new DatabaseLib.MealCallback() {
            @Override
            public void onMealsReceived(ArrayList<Meal> meals) {
                mealList = meals;
                scheduleMealNotifications();
                Meal nextMeal = getNextMeal();
                if(nextMeal == null){
                    //TODO: add to strings.xml
                    mealButton.setText("You have no planned meals");
                }
                else{
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        LocalTime currentTime = LocalTime.now();
                        LocalTime nextMealTime = LocalTime.parse(nextMeal.getTime());
                        if(Duration.between(currentTime, nextMealTime).toMillis() < 0 &&
                        !nextMeal.isEaten()){
                            //TODO: add do strings.xml
                            mealButton.setText("Register that you have eaten " +
                                    nextMeal.getToEat());
                            mealButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.purple));
                            mealButton.setTextColor(Color.parseColor("#ffffff"));
                            mealButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    databaseLib.setMealEaten(elderlyName, elderlyYear,
                                            nextMeal.getMealType(),true);
                                    //cancel all reminder notifications
                                    for(ArrayList list: localMealTimerList){
                                        Meal currMeal = (Meal) list.get(0);
                                        Timer currTimer = (Timer) list.get(1);
                                        if(currMeal.equals(nextMeal)){
                                            currTimer.cancel();
                                        }
                                    }
                                    recreate();
                                }
                            });
                        }
                        else{
                            //TODO: add to strings.xml
                            mealButton.setText("Next meal: " + nextMeal.getToEat() + " at "
                                    + nextMeal.getTime());
                            mealButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.light_gray));
                            mealButton.setTextColor(R.color.purple);
                        }
                    }
                }
            }
        });
    }

    private void refreshMealButtonEveryTenSeconds(){
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask refresh = new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> updateMeals());
            }
        };
        timer.schedule(refresh, 0, 10000);
    }

    private void markMealsAsUneatenAtMidnight(){
        final Handler handler = new Handler();
        Timer timer = new Timer();
        long delay = 0;
        long millisecondsInOneDay = 86400000;
        LocalTime currentTime;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentTime = LocalTime.now();
            delay = millisecondsInOneDay -
                    Duration.between(LocalTime.parse("00:00"), currentTime).toMillis();
        }
        TimerTask refresh = new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    for(Meal meal: mealList){
                        databaseLib.setMealEaten(elderlyName, elderlyYear, meal.getMealType(),
                                false);
                    }
                });
            }
        };
        timer.schedule(refresh, delay, millisecondsInOneDay);
    }

    /**
     *
     * @param meal meal to be scheduled
     * @param mealTime time of meal
     * @param addHistory boolean if notification should be added to notification history in database
     * @param notifyCaregivers boolean if notification should be sent to caregivers as well.
     */
    private void scheduleElderlyMealNotification(Meal meal, LocalTime mealTime, boolean addHistory,
                                                 boolean notifyCaregivers){
        final Handler handler = new Handler();
        Timer timer = new Timer();
        LocalTime currentTime;
        long delay = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentTime = LocalTime.now();
            delay = Duration.between(currentTime, mealTime).toMillis();
            if(delay < 0){
                return;
            }
            Log.d("CREATION", String.valueOf(delay));
        }
        TimerTask scheduleSchedule = new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    //TODO: add to strings.xml
                    notificationLib = new NotificationLib(elderlyOverviewContext,
                            "Reminder to eat " + meal.getMealType() + "!",
                            "click here to go to your meal");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        notificationLib.createAndShowNotification();
                        if(addHistory){
                            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                            databaseLib.addNotificationHistoryElderly(elderlyName,
                                    elderlyYear, "Missed to register meal",
                                    "Missed to register: " + meal.getMealType() +
                                    ", " + meal.getToEat(), df.format(new Date()));
                        }
                        if(notifyCaregivers){
                            sendNotificationToAllCaregivers(elderlyId + " no registration ",
                                    elderlyId + " missed to register three meal notifications");
                        }
                    }
                });
            }
        };
        timer.schedule(scheduleSchedule, delay);
        //add timer to array so that it can be cancelled later if elderly registers meal
        ArrayList<Object> mealTimerArrayList = new ArrayList<>();
        mealTimerArrayList.add(meal);
        mealTimerArrayList.add(timer);
        localMealTimerList.add(mealTimerArrayList);
    }

    public void sendNotificationToAllCaregivers(String title, String text){
        notificationLib = new NotificationLib(elderlyOverviewContext, title,
                text);
        databaseLib.getCaregiverUsernamesByElderlyId(elderlyId, new DatabaseLib.CaregiverUsernameCallback() {
            @Override
            public void onUsernameFound(List<String> usernames) {
                notificationLib.sendNotificationUsernameList(usernames, elderlyName, elderlyYear);
            }

            @Override
            public void onUsernameNotFound() {
                Toast.makeText(elderlyOverviewContext, R.string.error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(elderlyOverviewContext, R.string.error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
