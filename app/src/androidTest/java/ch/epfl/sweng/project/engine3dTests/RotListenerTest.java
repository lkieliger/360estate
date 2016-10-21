package ch.epfl.sweng.project.engine3dTests;


import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.view.Surface;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;

import java.util.Arrays;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.engine3d.PanoramaRenderer;
import ch.epfl.sweng.project.engine3d.RotSensorListener;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class RotListenerTest {

    private static final String TAG = "RotListenerTest";
    private RotSensorListener rotListener;
    private PanoramaRenderer renderer;
    private View view;
    private double errorEpsilon;

    @Before
    public void initMembers() {
        rotListener = new RotSensorListener();
        errorEpsilon = 0.1;
    }

    @Test
    public void rotListenerIsCorrect() {
        Quaternion q1 = new Quaternion();
        Quaternion q2 = new Quaternion().fromAngleAxis(Vector3.Axis.X, 90);
        Quaternion q3 = new Quaternion(0.5, 0.5, 0.5, 0.5);
        Quaternion q4 = new Quaternion(0, 0, 0.71, 0.71);
        Quaternion q5 = new Quaternion(0.5, 0.5, -0.5, -0.5);

        float[] values = rotListener.doubleToFloatArray(new double[]{q1.x, q1.y, q1.z, -q1.w});

        rotListener.sensorChanged(Arrays.copyOf(values, values.length));
        assertQuaternionEquals(q2, rotListener.getDummyRotation(), true);

        rotListener.setScreenRotation(Surface.ROTATION_90);
        rotListener.sensorChanged(Arrays.copyOf(values, values.length));
        assertQuaternionEquals(q3, rotListener.getDummyRotation(), true);

        rotListener.setScreenRotation(Surface.ROTATION_180);
        rotListener.sensorChanged(Arrays.copyOf(values, values.length));
        assertQuaternionEquals(q4, rotListener.getDummyRotation(), true);

        rotListener.setScreenRotation(Surface.ROTATION_270);
        rotListener.sensorChanged(Arrays.copyOf(values, values.length));
        assertQuaternionEquals(q5, rotListener.getDummyRotation(), true);
    }

    private void assertQuaternionEquals(Quaternion v1, Quaternion v2, boolean shouldBeEqual) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, String.format("V1: %1$.2f, %2$.2f, %3$.2f, %4$.2f", v1.w, v1.x, v1.y, v1.z));
            Log.d(TAG, String.format("V2: %1$.2f, %2$.2f, %3$.2f, %4$.2f", v2.w, v2.x, v2.y, v2.z));
        }
        if (shouldBeEqual) {
            assertTrue(v1.equals(v2, errorEpsilon));
        } else {
            assertFalse(v1.equals(v2, errorEpsilon));
        }
    }

}
