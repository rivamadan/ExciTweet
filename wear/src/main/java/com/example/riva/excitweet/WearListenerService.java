package com.example.riva.excitweet;

import android.content.Intent;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class WearListenerService extends WearableListenerService {

    private static final String RECEIVER_SERVICE_PATH = "/wear-lisetner-service";

    @Override
    public void onCreate() {

        super.onCreate();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        startService(new Intent(this, SensorService.class));
    }

}