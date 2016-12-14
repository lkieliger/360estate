package ch.epfl.sweng.project.engine3d.listeners;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Surface;

import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;

import java.util.Arrays;

import ch.epfl.sweng.project.engine3d.PanoramaRenderer;

import static ch.epfl.sweng.project.util.DoubleArrayConverter.floatToDoubleArray;


public class RotSensorListener implements SensorEventListener {

    private static final String TAG = "RotSensorListener";

    private final PanoramaRenderer mRenderer;
    private float[] mRotationMatrixIn;
    private float[] mRotationMatrixOut;
    private int mScreenRotation = 0;

    public RotSensorListener(int rotation, PanoramaRenderer renderer) {
        if (renderer == null)
            throw new IllegalArgumentException("Renderer reference was null");

        if (rotation != Surface.ROTATION_0 && rotation != Surface.ROTATION_90 && rotation != Surface.ROTATION_180 &&
                rotation != Surface.ROTATION_270)
            throw new IllegalArgumentException("Surface.ROTATION value is invalid");

        mRenderer = renderer;
        mRotationMatrixIn = new float[16];
        mRotationMatrixOut = new float[16];
        mScreenRotation = rotation;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {
            sensorChanged(event.values);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void sensorChanged(float[] sensorValues) {
        float[] values = Arrays.copyOf(sensorValues, sensorValues.length);
        values[3] = -values[3];

        SensorManager.getRotationMatrixFromVector(mRotationMatrixIn, values);
        SensorManager.remapCoordinateSystem(mRotationMatrixIn, SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Z,
                mRotationMatrixOut);

        mRenderer.setDeviceYaw(Math.atan2(mRotationMatrixOut[1], mRotationMatrixOut[5]));

        Quaternion q = new Quaternion().fromMatrix(floatToDoubleArray(mRotationMatrixOut));

        switch (mScreenRotation) {
            case Surface.ROTATION_0:
                break;
            case Surface.ROTATION_90:
                q.multiplyLeft(new Quaternion().fromAngleAxis(Vector3.Axis.Z, 90));
                break;
            case Surface.ROTATION_180:
                q.multiplyLeft(new Quaternion().fromAngleAxis(Vector3.Axis.Z, 180));
                break;
            case Surface.ROTATION_270:
                q.multiplyLeft(new Quaternion().fromAngleAxis(Vector3.Axis.Z, 270));
                break;
        }

        mRenderer.setSensorRotation(q);
    }
}
