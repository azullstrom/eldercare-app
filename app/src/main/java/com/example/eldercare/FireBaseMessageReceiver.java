package com.example.eldercare;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FireBaseMessageReceiver extends FirebaseMessagingService {

    // Override onNewToken to get new token
    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("CREATION", "Refreshed token: " + token);
        //TODO: change token in database to new token
    }

    // Override onMessageReceived() method to extract the
    // title and
    // body from the message passed in FCM
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String text = data.get("text");
        NotificationLib notificationLib = new NotificationLib(this, title, text);
        notificationLib.createAndShowNotification();
    }
}
