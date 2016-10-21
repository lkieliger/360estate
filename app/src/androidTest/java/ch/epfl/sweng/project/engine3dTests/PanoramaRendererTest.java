package ch.epfl.sweng.project.engine3dTests;


import android.support.test.rule.ActivityTestRule;
import android.util.DisplayMetrics;
import android.util.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.rajawali3d.cameras.Camera;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.engine3d.PanoramaActivity;
import ch.epfl.sweng.project.engine3d.PanoramaRenderer;

import static ch.epfl.sweng.project.util.TestUtilityFunctions.wait1s;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class PanoramaRendererTest {

    private static final String TAG = "PanoramaRendererTest";

    @Rule
    public ActivityTestRule<PanoramaActivity> mActivityTestRule = new ActivityTestRule<>(PanoramaActivity.class);
    private PanoramaRenderer renderer;
    private DisplayMetrics metrics;
    private Camera cam;
    private double errorEpsilon;

    @Before
    public void initMembers() {
        renderer = new PanoramaRenderer(
                mActivityTestRule.getActivity().getApplicationContext(),
                mActivityTestRule.getActivity().getWindowManager().getDefaultDisplay());
        errorEpsilon = 0.1;
        metrics = renderer.getContext().getResources().getDisplayMetrics();
        cam = renderer.getCurrentCamera();
        wait1s(TAG);
    }


    @Test
    public void cameraConfigIsCorrect() {

        assertFalse(cam.isLookAtEnabled());
    }

    @Test
    public void setSensorRotIsCorrect() {
        Quaternion q1 = new Quaternion().fromAngleAxis(Vector3.Axis.X, 96);

        //Check for defensive copy on render side
        renderer.setSensorRotation(q1);
        q1.multiply(new Quaternion().fromAngleAxis(Vector3.Axis.Y, 90));
        assertQuaternionEquals(q1, renderer.getSensorRot(), false);

    }

    @Test
    /**
     * The camera sensitivity should depend on the dpi of the device
     * so that a swipe has the same effect regardless of the dx or dy
     * reported by the touch listener
     */
    public void cameraSensitivityIsCorrect() {

        double angleChange = 90;

        Quaternion newRot = renderer.getUserRotation().
                multiplyLeft(new Quaternion().fromAngleAxis(Vector3.Axis.Y, -angleChange));

        float phi = angleToPixelDelta(angleChange, true);
        renderer.updateCameraRotation(phi, 0);

        assertQuaternionEquals(newRot, renderer.getUserRotation(), true);

    }


    private float angleToPixelDelta(double angle, boolean isAlongXAxis) {
        if (isAlongXAxis) {
            return (float) ((angle / PanoramaRenderer.SENSITIVITY) * metrics.xdpi);
        } else {
            return (float) ((angle / PanoramaRenderer.SENSITIVITY) * metrics.ydpi);
        }
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
