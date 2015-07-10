package com.example.riva.excitweet;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;

public class CameraAndTwitter extends Service {
    Notification notification;
    NotificationManagerCompat notificationManager;
    int notificationId = 1;

    //    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentCamera, 0);

        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.common_signin_btn_icon_dark,
                        getString(R.string.notifi_title), pendingIntent)
                        .build();

        notification =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.common_signin_btn_icon_dark)
                        .setContentTitle(getString(R.string.notifi_title))
                        .setContentText(getString(R.string.notifi_text))
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .addAction(R.drawable.common_signin_btn_icon_dark, getString(R.string.camera), pendingIntent)
                        .extend(new WearableExtender())
                        .build();

        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_SOUND;

        notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, notification);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
