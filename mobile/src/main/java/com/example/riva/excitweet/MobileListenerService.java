package com.example.riva.excitweet;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class MobileListenerService extends WearableListenerService {
    Notification notification;
    NotificationManagerCompat notificationManager;
    int notificationId = 1;

    private static final String RECEIVER_SERVICE_PATH = "/mobile-listener-service";

    @Override
    public void onCreate() {

        super.onCreate();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        startService(new Intent(this, CameraAndTwitter.class));
    }

}
