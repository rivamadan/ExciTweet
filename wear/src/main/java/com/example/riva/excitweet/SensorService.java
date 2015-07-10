package com.example.riva.excitweet;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.Set;

public class SensorService extends Service {

    private class SensorThread extends Thread implements SensorEventListener {
        GoogleApiClient mGoogleApiClient;
        private static final String CAPABILITY_NAME = "excited";
        private String nodeId = null;

        private Sensor accelSensor;
        private SensorManager mSensorManager;
        private long lastUpdate = 0;
        private float last_x, last_y, last_z;
        private static final int EXCITE_THRESHOLD = 2500;

        public void run() {
            this.mGoogleApiClient = new GoogleApiClient.Builder(SensorService.this)
                    .addApi(Wearable.API)
                    .build();
            this.mGoogleApiClient.connect();

            CapabilityApi.GetCapabilityResult capResult = Wearable.CapabilityApi.getCapability(
                    mGoogleApiClient, CAPABILITY_NAME, CapabilityApi.FILTER_REACHABLE)
                    .await();

            getNodeId(capResult.getCapability());

            Looper.prepare();
            Handler handler = new Handler();
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            accelSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensorManager.registerListener(this, accelSensor, mSensorManager.SENSOR_DELAY_NORMAL, handler);
            Looper.loop();
        }

        private void getNodeId(CapabilityInfo capabilityInfo) {
            Set<Node> connectedNodes = capabilityInfo.getNodes();

            for (Node node : connectedNodes) {
                if (node.isNearby()) {
                    nodeId = node.getId();
                }
                nodeId = node.getId();
            }
        }

        @Override
        public final void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Do nothing
        }

        @Override
        public final void onSensorChanged(SensorEvent event) {
            Sensor mySensor = event.sensor;

            if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                long curTime = System.currentTimeMillis();

                if (lastUpdate == 0 || ((curTime - lastUpdate) > 100)) {
                    long diffTime = (curTime - lastUpdate);
                    lastUpdate = curTime;

                    float excite = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

                    if (excite > EXCITE_THRESHOLD) {
                        Log.v("myTag", "inside: " + excite);
                        MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, "/mobile-listener-service", new byte[3]).await();
                        if (result.getStatus().isSuccess()) {
                            Log.v("myTag", "Sensor Message: sent to: " + nodeId);
                        }
                        else {
                            // Log an error
                            Log.v("myTag", "ERROR: failed to send Message");
                        }
                    }

                    last_x = x;
                    last_y = y;
                    last_z = z;

                }
            }

        }
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        new SensorThread().start();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
