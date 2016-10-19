package ch.epfl.sweng.project.engine3d;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.Surface;

import org.rajawali3d.math.Quaternion;


public class RotSensorListener implements SensorEventListener {

    private static final String TAG = "RotSensorListener";

    private final PanoramaRenderer mRenderer;
    private final SensorManager mSensorManager;
    private final Display mDisplay;
    private float[] mRotationMatrixIn;
    private float[] mRotationMatrixOut;

    public RotSensorListener(Display display, PanoramaRenderer renderer, SensorManager sensorManager) {
        if (renderer == null) {
            throw new IllegalArgumentException("Renderer reference was null");
        }
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

        event.values[3] = -event.values[3];
        SensorManager.getRotationMatrixFromVector(mRotationMatrixIn, event.values);


        switch (mDisplay.getRotation()) {
            case Surface.ROTATION_0:
                SensorManager.remapCoordinateSystem(mRotationMatrixIn, SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Z,
                        mRotationMatrixOut);
                break;
            case Surface.ROTATION_90:
                SensorManager.remapCoordinateSystem(mRotationMatrixIn, SensorManager.AXIS_Y, SensorManager
                                .AXIS_MINUS_Z,
                        mRotationMatrixOut);
                break;
            case Surface.ROTATION_180:
                SensorManager.remapCoordinateSystem(mRotationMatrixIn, SensorManager.AXIS_MINUS_X, SensorManager.AXIS_Z,
                        mRotationMatrixOut);
                break;
            case Surface.ROTATION_270:
                SensorManager.remapCoordinateSystem(mRotationMatrixIn, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X,
                        mRotationMatrixOut);
                break;
        }

        Quaternion q = new Quaternion().fromMatrix(floatToDoubleArray(mRotationMatrixOut));
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
