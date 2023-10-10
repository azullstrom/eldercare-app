package com.example.eldercare;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class NotificationLib extends TimerTask {

    private Context context;
    private String channelId, channelName, title, text;

    /**
     * Constructor
     *
     * @param context Just write the class you're in: "this" should be fine.
     */
    public NotificationLib(Context context, String title, String text) {
        this.context = context;
        this.channelId = "elderCareId";
        this.channelName = "elderCareChannel";
        this.title = title;
        this.text = text;
        createNotificationChannel();
    }

    /**
     * Creates a notification channel of which to put notifications in.
     * If a channel with the same Id and name exists, it does not create a new channel
     * and instead uses the existing channel.
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        else{
            Toast.makeText(context, "Android version not supported", Toast.LENGTH_SHORT);
        }
    }

    /**
     * Creates and shows a notification. If permission for notification is not allowed no
     * notification is sent. All notifications share the same ID which means they will replace
     * the previous notification when a new one is created.
     */
    public void createAndShowNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    /**
     * Schedules a notification at specific time which will be repeated at the same time every
     * day until it is cancelled. To cancel the notification, call .cancel() on the returned timer
     * object.
     *
     * @param scheduleHour The hour of which the notification is sent
     * @param scheduleMinute The minute of which the notificaion is sent
     * @return A timer object which can be used to cancel the scheduled notification
     */
    public Timer scheduleRepeatableNotification(int scheduleHour, int scheduleMinute){
        LocalDateTime currentDate, scheduleDate;
        //Amount of milliseconds in one day: 1000*60*60*24 = 86400000
        long millisOneDay = 86400000;
        long timeDeltaMillis;
        //Check if android version is high enough
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Get current date and time
            currentDate = LocalDateTime.now();
            //Get scheduled date and time
            scheduleDate = LocalDateTime.of(currentDate.getYear(), currentDate.getMonth(),
                    currentDate.getDayOfMonth(), scheduleHour, scheduleMinute);
            //Get amount of milliseconds between current date and scheduled date
            timeDeltaMillis = Duration.between(currentDate, scheduleDate).toMillis();
            if(timeDeltaMillis < 0){
                //If scheduled time is negative (in the past), add a day to timeDelta
                timeDeltaMillis += millisOneDay;
            }
        }
        else{
            Toast.makeText(context, "Android version not supported", Toast.LENGTH_SHORT);
            return null;
        }
        Timer scheduleTimer = new Timer();
        scheduleTimer.scheduleAtFixedRate(this, timeDeltaMillis, millisOneDay);
        return scheduleTimer;
    }

    /**
     * Sends notification to a user with the specified firebase receiver-token.
     * @param receiverToken firebase device unique token.
     */
    public void sendNotification(String receiverToken){

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject jsonNotification = new JSONObject();
        JSONObject jsonContainer = new JSONObject();
        try{
            jsonNotification.put("title", title);
            jsonNotification.put("body", text);
            jsonContainer.put("to", receiverToken);
            jsonContainer.put("notification", jsonNotification);
        }
        catch (JSONException e){
            Log.d("CREATION", e.toString());
        }

        RequestBody rBody = RequestBody.create(mediaType, jsonContainer.toString());
        Request request = new Request.Builder().url("https://fcm.googleapis.com/fcm/send")
                .post(rBody)
                .addHeader("Authorization", "key=AAAAF5gIvAY:APA91bEu3HAXShyisIo3DpvSxWpGUDU3DJK_nVlrOB4wLrw-Kw87j7rxqHFPWEUPPGNpOmKbk2Fy5VkvlePSTqwzbucDTj9ia10nagDqut7hXv-Z0Yhs9bZrOz25CQNoT9zqIvzM36Uh")
                .addHeader("Content-Type", "application/json")
                .build();
        Thread thread = new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
            } catch (IOException e) {
                Log.d("CREATION", e.toString());
            }
        });
        thread.start();
    }

    /**
     * Override from the extended class TimerTask, and is called by the function
     * scheduleRepeatableNotification when scheduleAtFixedRate delay is 0, once a day.
     *
     */
    @Override
    public void run() {
        createAndShowNotification();
    }
}
