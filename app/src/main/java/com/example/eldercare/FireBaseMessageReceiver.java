package com.example.eldercare;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FireBaseMessageReceiver extends FirebaseMessagingService {

    // Override onNewToken to get new token
    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("CREATION", "Refreshed token: " + token);
    }

    // Override onMessageReceived() method to extract the
    // title and
    // body from the message passed in FCM
    @Override
    public void
    onMessageReceived(RemoteMessage remoteMessage)
    {
        if (remoteMessage.getNotification() != null) {
            // Since the notification is received directly
            // from FCM, the title and the body can be
            // fetched directly as below.
            NotificationLib notificationLib = new NotificationLib(this, "NotificationId",
                    "NotificationChannel", remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody());
            notificationLib.createAndShowNotification();
        }
    }
}
