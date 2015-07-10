package com.example.riva.excitweet;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class MobileListenerService extends WearableListenerService {
    NotificationManagerCompat notificationManager;
    int notificationId = 1;

    private static final String RECEIVER_SERVICE_PATH = "/mobile-listener-service";

    @Override
    public void onCreate() {

        super.onCreate();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.v("myTag", "Message received");

        Intent tweetIntent= new Intent(this, TweetActivity.class);
        tweetIntent.putExtra("nid", notificationId);

        tweetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, tweetIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.jump);

        NotificationCompat.Builder nBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.common_signin_btn_icon_dark)
                        .setContentTitle(getString(R.string.notifi_title))
                        .setContentText(getString(R.string.notifi_text))
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .addAction(R.drawable.common_signin_btn_icon_dark, getString(R.string.camera), pendingIntent)
                        .extend(new NotificationCompat.WearableExtender().setBackground(bitmap));

        Notification notification = nBuilder.build();

        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_SOUND;

        notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, notification);
    }

}
