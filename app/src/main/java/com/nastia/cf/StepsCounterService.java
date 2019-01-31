package com.nastia.cf;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StepsCounterService extends Service implements SensorEventListener, StepListener {


    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    public static int numSteps;
    private SharedPreferences sharedPref;
    public static SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/YYYY");
    private String date;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPref = getSharedPreferences("steps_sp", MODE_PRIVATE);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        numSteps = sharedPref.getInt("steps", 0);
        sensorManager.registerListener(StepsCounterService.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void step(long timeNs) {

        if(sharedPref.getString("lastTime",null) == null){
            Date d = new Date();
            date = sfd.format(d);
            sharedPref.edit().putString("lastTime", date).commit();
    }
        else{
            Date today = new Date();
            String stringNow = sfd.format(today);
            if(!sharedPref.getString("lastTime","").equals(stringNow)){
                numSteps=0;
                sharedPref.edit().putInt("steps", numSteps).commit();
                sharedPref.edit().putString("lastTime", stringNow).commit();
            }
        }

        numSteps++;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);

    }
}
