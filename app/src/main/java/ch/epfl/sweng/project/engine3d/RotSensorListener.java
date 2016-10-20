package ch.epfl.sweng.project.engine3d;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Display;
import android.view.Surface;

import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;

import ch.epfl.sweng.project.BuildConfig;


public class RotSensorListener implements SensorEventListener {

    private static final String TAG = "RotSensorListener";

    private final PanoramaRenderer mRenderer;
    private final SensorManager mSensorManager;
    private final Display mDisplay;
    private float[] mRotationMatrixIn;
    private float[] mRotationMatrixOut;
    private int counter;

    public RotSensorListener(Display display, PanoramaRenderer renderer, SensorManager sensorManager) {
        if (renderer == null) {
            throw new IllegalArgumentException("Renderer reference was null");
        }
        counter = 0;
        mDisplay = display;
        mRenderer = renderer;
        mSensorManager = sensorManager;
        mRotationMatrixIn = new float[16];
        mRotationMatrixOut = new float[16];
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() != Sensor.TYPE_GAME_ROTATION_VECTOR) {
            return;
        }

        counter++;

        float w = event.values[3];
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        if (BuildConfig.DEBUG && counter == 100) {
            counter = 0;
            Log.d(TAG, "Sensor quaternion: w=" + w + " x=" + x + " y=" + y + "z=" + z);
        }


        event.values[3] = -event.values[3];


        SensorManager.getRotationMatrixFromVector(mRotationMatrixIn, event.values);
        SensorManager.remapCoordinateSystem(mRotationMatrixIn, SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Z,
                mRotationMatrixOut);
        Quaternion q = new Quaternion().fromMatrix(floatToDoubleArray(mRotationMatrixOut));

        switch (mDisplay.getRotation()) {
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

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private double[] floatToDoubleArray(float[] a) {
        double[] ret = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            ret[i] = Double.valueOf(a[i]);
        }
        return ret;
    }
}
