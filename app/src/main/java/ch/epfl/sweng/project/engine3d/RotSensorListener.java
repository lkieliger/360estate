package ch.epfl.sweng.project.engine3d;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.Surface;

import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;


public class RotSensorListener implements SensorEventListener {

    private static final String TAG = "RotSensorListener";

    private final PanoramaRenderer mRenderer;
    private final SensorManager mSensorManager;
    private final Display mDisplay;
    private float[] mRotationMatrixIn;
    private float[] mRotationMatrixOut;
    private Vector3 mLastDir;
    private int mLastScreenOrientation;

    public RotSensorListener(Display display, PanoramaRenderer renderer, SensorManager sensorManager) {
        if (renderer == null) {
            throw new IllegalArgumentException("Renderer reference was null");
        }
        mLastScreenOrientation = display.getRotation();
        mDisplay = display;
        mRenderer = renderer;
        Quaternion q = new Quaternion(mRenderer.getCurrentCamera().getOrientation());
        mLastDir = getAxis(q);
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
        SensorManager.remapCoordinateSystem(
                mRotationMatrixIn,
                SensorManager.AXIS_X,
                SensorManager.AXIS_MINUS_Z,
                mRotationMatrixOut);

        Quaternion q = new Quaternion().fromMatrix(floatToDoubleArray(mRotationMatrixOut));

        switch (mDisplay.getRotation()) {
            case Surface.ROTATION_0:
                if (mLastScreenOrientation != Surface.ROTATION_0) {
                    mLastScreenOrientation = Surface.ROTATION_0;
                    Vector3 newDir = getAxis(q);
                    Quaternion transition = Quaternion.createFromRotationBetween(newDir, mLastDir);
                    mLastDir = new Vector3(newDir);
                    mRenderer.fixScreenRotation(transition);
                }
                break;
            case Surface.ROTATION_90:
                q.multiplyLeft(new Quaternion().fromAngleAxis(Vector3.Axis.Z, 90));

                if (mLastScreenOrientation != Surface.ROTATION_90) {
                    mLastScreenOrientation = Surface.ROTATION_90;
                    Vector3 newDir = getAxis(q);
                    Quaternion transition = Quaternion.createFromRotationBetween(newDir, mLastDir);
                    mLastDir = new Vector3(newDir);
                    mRenderer.fixScreenRotation(transition);
                }
                break;
            case Surface.ROTATION_180:
                q.multiplyLeft(new Quaternion().fromAngleAxis(Vector3.Axis.Z, 180));

                if (mLastScreenOrientation != Surface.ROTATION_180) {
                    mLastScreenOrientation = Surface.ROTATION_180;
                    Vector3 newDir = getAxis(q);
                    Quaternion transition = Quaternion.createFromRotationBetween(newDir, mLastDir);
                    mLastDir = new Vector3(newDir);
                    mRenderer.fixScreenRotation(transition);
                }
                break;
            case Surface.ROTATION_270:
                q.multiplyLeft(new Quaternion().fromAngleAxis(Vector3.Axis.Z, 270));

                if (mLastScreenOrientation != Surface.ROTATION_270) {
                    mLastScreenOrientation = Surface.ROTATION_270;
                    Vector3 newDir = getAxis(q);
                    Quaternion transition = Quaternion.createFromRotationBetween(newDir, mLastDir);
                    mLastDir = new Vector3(newDir);
                    mRenderer.fixScreenRotation(transition);
                }
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

    /**
     * CODE FROM <a href=http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToAngle/>
     * EXTERNAL SOURCE</a>
     *
     * @param q1
     */
    private Vector3 getAxis(Quaternion q1) {
        // if w>1 acos and sqrt will produce errors, this cant happen if quaternion is normalised
        if (q1.w > 1) q1.normalize();
        double angle = 2 * Math.acos(q1.w);
        // assuming quaternion normalised then w is less than 1, so term always positive.
        double s = Math.sqrt(1 - q1.w * q1.w);
        double x = 0;
        double y = 0;
        double z = 0;
        if (s < 0.001) { // test to avoid divide by zero, s is always positive due to sqrt
            // if s close to zero then direction of axis not important
            x = q1.x; // if it is important that axis is normalised then replace with x=1; y=z=0;
            y = q1.y;
            z = q1.z;
        } else {
            x = q1.x / s; // normalise axis
            y = q1.y / s;
            z = q1.z / s;
        }

        return new Vector3(x, y, z);
    }
}
