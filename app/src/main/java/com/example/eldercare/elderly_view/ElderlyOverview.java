package com.example.eldercare.elderly_view;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.eldercare.modules.Meal;
import com.example.eldercare.R;
import com.example.eldercare.modules.DatabaseLib;
import com.example.eldercare.modules.NotificationLib;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
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
    ArrayList<Timer> timerList;
    Context elderlyOverviewContext;
    boolean cancelAlarm;
    ProgressBar progressBar;
    AlertDialog dialog;
    CountDownTimer countDownTimer;

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
        patientTitle.setText(getString(R.string.hi, elderlyName));
        timerList = new ArrayList<>();


        mealButton = findViewById(R.id.mealButtonElderly);
        alarmButton = findViewById(R.id.buttonAlarm);

        refreshMealButtonEveryTenSeconds();
        markMealsAsUneatenAtMidnight();

        alarmButton.setOnClickListener(view -> {
            showCountDownPopup();
        });


    }

    private void showCountDownPopup(){
        cancelAlarm = false;
        dialog = null;
        if(countDownTimer != null){
            countDownTimer.cancel();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        builder.setTitle("Alarm");
        builder.setView(dialogView);
        progressBar = dialogView.findViewById(R.id.progressBar);
        progressBar.setMax(100);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cancelAlarm = true;
            }
        });

        dialog = builder.create();
        dialog.setMessage("TEST");
        dialog.show();
        if (dialog != null && dialog.isShowing()) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.GONE);
        }
        countDownTimer = new CountDownTimer(11000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                if (dialog != null) {
                    dialog.setMessage(getString(R.string.countdown, secondsLeft));
                    progressBar.setProgress((int) (millisUntilFinished / 100)-10);
                }
            }

            @Override
            public void onFinish() {
                if (dialog != null) {
                    dialog.setMessage(getString(R.string.countdown, 0));
                    progressBar.setProgress(0);
                    if(!cancelAlarm){
                        sendNotificationToAllCaregivers("Elderly Alarm", elderlyId + " has requested help");
                    }
                    dialog.hide();
                }
            }
        }.start();

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // Handle cancel events if necessary
            }
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
    private void scheduleMealNotifications(Meal nextMeal) {
        if(nextMeal.isEaten()){
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            for(Timer timer: timerList){
                timer.cancel();
            }

            timerList = new ArrayList<>();
            LocalTime mealTime = LocalTime.parse(nextMeal.getTime());
            LocalTime mealTime2 = mealTime.plusMinutes(1);
            LocalTime mealTime3 = mealTime2.plusMinutes(1);

            scheduleElderlyMealNotification(nextMeal, mealTime, false, false);
            scheduleElderlyMealNotification(nextMeal, mealTime2, true, false);
            scheduleElderlyMealNotification(nextMeal, mealTime3, true, true);
        }
    }

    /**
     * Displays the next meal on mealButton, and changes mealButton to be clickable when a meal
     * is to be registered. Also cancels all remaining scheduled notifications when
     * mealButton is clicked.
     */
    private void updateMeals(){
        databaseLib.getMeals(elderlyId, new DatabaseLib.ArrayListMealCallback() {
            @Override
            public void onFound(ArrayList<Meal> meals) {
                mealList = meals;
                Meal nextMeal = getNextMeal();
                if(nextMeal == null){
                    mealButton.setText(R.string.no_planned_meals);
                }
                else{
                    scheduleMealNotifications(nextMeal);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        LocalTime currentTime = LocalTime.now();
                        LocalTime nextMealTime = LocalTime.parse(nextMeal.getTime());
                        if(Duration.between(currentTime, nextMealTime).toMillis() < 0 &&
                        !nextMeal.isEaten()){
                            mealButton.setText(getString(R.string.register_eaten, nextMeal.getToEat()));
                            mealButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.purple));
                            mealButton.setTextColor(Color.parseColor("#ffffff"));
                            mealButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                                    databaseLib.setMealEaten(elderlyName, elderlyYear,
                                            nextMeal.getMealType(),true);
                                    databaseLib.addMealHistoryElderly(elderlyName, elderlyYear,
                                            nextMeal, df.format(new Date()));
                                    //cancel all reminder notifications
                                    for(Timer timer: timerList){
                                        timer.cancel();
                                    }
                                    timerList = new ArrayList<>();
                                    recreate();
                                }
                            });
                        }
                        else{
                            mealButton.setText(getString(R.string.next_meal, nextMeal.getToEat(), nextMeal.getTime()));
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
        }
        TimerTask scheduleSchedule = new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    notificationLib = new NotificationLib(elderlyOverviewContext,
                            getString(R.string.reminder_to_eat, meal.getToEat()),
                            getString(R.string.click_here_meal));
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
        timerList.add(timer);
    }

    public void sendNotificationToAllCaregivers(String title, String text){
        notificationLib = new NotificationLib(elderlyOverviewContext, title,
                text);
        databaseLib.getCaregiverUsernamesByElderlyId(elderlyId, new DatabaseLib.ListCallback() {
            @Override
            public void onFound(List<String> usernames) {
                notificationLib.sendNotificationUsernameList(usernames, elderlyName, elderlyYear);
            }

            @Override
            public void onNotFound() {
                Toast.makeText(elderlyOverviewContext, R.string.error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(elderlyOverviewContext, R.string.error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
