package ch.epfl.sweng.project.engine3d;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import ch.epfl.sweng.project.BuildConfig;


public class RotSensorListener implements SensorEventListener {

    private static final String TAG = "RotSensorListener";

    private final PanoramaRenderer mRenderer;
    private final SensorManager mSensorManager;

    public RotSensorListener(PanoramaRenderer renderer, SensorManager sensorManager) {
        if (renderer == null) {
            throw new IllegalArgumentException("Renderer reference was null");
        }
        mRenderer = renderer;
        mSensorManager = sensorManager;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] val = event.values;
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "ROTATION SENSOR VALUE:");
            for (int i = 0; i < val.length; i++) {
                Log.d(TAG, "" + val[i]);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
