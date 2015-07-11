package com.example.riva.excitweet;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.Gravity;

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
        tweetIntent.setAction(Long.toString(System.currentTimeMillis()));

        tweetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, tweetIntent, 0);

        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.chibi_excited);
        Bitmap icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_thumbsup);

        NotificationCompat.Builder nBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_thumbsup)
                        .setLargeIcon(icon)
                        .setContentTitle("    EXCITED!?")
                        .setContentText("    Tweet it!")
                        .setContentIntent(pendingIntent)
                        .addAction(R.drawable.ic_stat_image_photo_camera, getString(R.string.camera), pendingIntent)
                        .setOnlyAlertOnce(true)
                        .extend(new NotificationCompat.WearableExtender()
                                .setBackground(bitmap)
                                .setContentIcon(R.drawable.ic_thumbsup)
                                .setContentIconGravity(Gravity.START)
                                .setHintHideIcon(true));

        Notification notification = nBuilder.build();

        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_SOUND;

        notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, notification);
    }

}
