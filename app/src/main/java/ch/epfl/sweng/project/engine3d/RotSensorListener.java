package ch.epfl.sweng.project.engine3d;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;


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
        Quaternion q = new Quaternion(-val[3], val[0], val[1], val[2]);
        Quaternion rot90X = new Quaternion().fromAngleAxis(Vector3.Axis.X, 90.0);
        q.multiply(rot90X);
        mRenderer.setSensorRotation(q);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
