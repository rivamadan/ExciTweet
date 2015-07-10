package com.example.riva.excitweet;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PictureService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String urlString = intent.getStringExtra("url");

        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.jump);

        //put this in separte thread?
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
        }


        NotificationManagerCompat notificationManager;
        int notificationId = 2;


        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                new Intent(),
                PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder nBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.common_signin_btn_icon_dark)
                        .setContentTitle(getString(R.string.notifi_title))
                        .setContentText(getString(R.string.notifi_text))
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .extend(new NotificationCompat.WearableExtender().setBackground(bitmap));


        Notification notification = nBuilder.build();

        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_SOUND;

        notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, notification);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
