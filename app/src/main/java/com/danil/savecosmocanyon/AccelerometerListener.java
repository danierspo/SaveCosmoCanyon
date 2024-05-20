package com.danil.savecosmocanyon;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * Created by mfaella on 28/02/16.
 */
public class AccelerometerListener implements SensorEventListener {
    private final GameWorld gw;

    public AccelerometerListener(GameWorld gw)
    {
        this.gw = gw;
        gw.setGravity(0,15);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // NOP
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // NOP
    }
}
