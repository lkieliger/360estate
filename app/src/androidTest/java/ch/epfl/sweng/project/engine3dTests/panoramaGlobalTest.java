package ch.epfl.sweng.project.engine3dTests;


import android.support.test.rule.ActivityTestRule;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.rajawali3d.cameras.Camera;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.engine3d.PanoramaActivity;
import ch.epfl.sweng.project.engine3d.PanoramaRenderer;
import ch.epfl.sweng.project.engine3d.PanoramaTouchListener;
import ch.epfl.sweng.project.engine3d.RotSensorListener;

import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_HOVER_ENTER;
import static android.view.MotionEvent.ACTION_HOVER_EXIT;
import static android.view.MotionEvent.ACTION_HOVER_MOVE;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_OUTSIDE;
import static android.view.MotionEvent.ACTION_POINTER_DOWN;
import static android.view.MotionEvent.ACTION_POINTER_UP;
import static android.view.MotionEvent.ACTION_UP;
import static ch.epfl.sweng.project.util.TestUtilityFunctions.wait1s;
import static ch.epfl.sweng.project.util.TestUtilityFunctions.wait250ms;
import static ch.epfl.sweng.project.util.TestUtilityFunctions.wait500ms;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class PanoramaGlobalTest {

    private static final String TAG = "PanoramaGlobalTest";

    @Rule
    public ActivityTestRule<PanoramaActivity> mActivityTestRule = new ActivityTestRule<>(PanoramaActivity.class);
    private PanoramaRenderer renderer = null;
    private DisplayMetrics metrics = null;
    private Camera cam = null;
    private double errorEpsilon = -1;

    private View view = null;


    @Before
    public void initMembers() {
        renderer = new PanoramaRenderer(
                mActivityTestRule.getActivity().getApplicationContext(),
                mActivityTestRule.getActivity().getWindowManager().getDefaultDisplay());
        wait1s(TAG);

        errorEpsilon = 0.1d;
        metrics = renderer.getContext().getResources().getDisplayMetrics();
        cam = renderer.getCurrentCamera();

        view = new View(mActivityTestRule.getActivity().getApplicationContext());

        wait500ms(TAG);
    }

    @After
    public void finishActivity() {
        mActivityTestRule.getActivity().getAssociatedRenderer().onPause();

        wait250ms(TAG);

        mActivityTestRule.getActivity().finish();

        renderer = null;
        wait1s(TAG);
    }


    @Test
    public void panoramaRendererBigTest() {

        wait500ms(TAG);
        // cameraConfigIsCorrect test
        assertFalse(cam.isLookAtEnabled());


        // setSensorRotIsCorrect test

        Quaternion q1 = new Quaternion().fromAngleAxis(Vector3.Axis.X, 96);

        //Check for defensive copy on render side
        renderer.setSensorRotation(q1);
        q1.multiply(new Quaternion().fromAngleAxis(Vector3.Axis.Y, 90));

        wait500ms(TAG);
        assertQuaternionEquals(q1, renderer.getSensorRot(), false);


    /*
     *  * cameraSensitivityIsCorrect test
     * The camera sensitivity should depend on the dpi of the device
     * so that a swipe has the same effect regardless of the dx or dy
     * reported by the touch listener
     */

        double angleChange = 90;

        Quaternion newRot = renderer.getUserRotation().
                multiplyLeft(new Quaternion().fromAngleAxis(Vector3.Axis.Y, -angleChange));
        wait500ms(TAG);


        float phi = angleToPixelDelta(angleChange, true);
        renderer.updateCameraRotation(phi, 0);

        wait1s(TAG);
        assertQuaternionEquals(newRot, renderer.getUserRotation(), true);

        // consumes valid input

        View.OnTouchListener touchListener = new PanoramaTouchListener(renderer);
        wait250ms(TAG);

        assertTrue(touchListener.onTouch(view, genEvent(ACTION_DOWN)));
        assertTrue(touchListener.onTouch(view, genEvent(ACTION_UP)));
        assertTrue(touchListener.onTouch(view, genEvent(ACTION_MOVE)));
        assertTrue(touchListener.onTouch(view, genEvent(ACTION_CANCEL)));
        assertTrue(touchListener.onTouch(view, genEvent(ACTION_POINTER_UP)));

        // dontConsumeInvalidInput test

        assertFalse(touchListener.onTouch(view, genEvent(ACTION_HOVER_ENTER)));
        assertFalse(touchListener.onTouch(view, genEvent(ACTION_HOVER_EXIT)));
        assertFalse(touchListener.onTouch(view, genEvent(ACTION_HOVER_MOVE)));
        assertFalse(touchListener.onTouch(view, genEvent(ACTION_OUTSIDE)));
        assertFalse(touchListener.onTouch(view, genEvent(ACTION_POINTER_DOWN)));

        // Instantiate the RotSensor class

        wait1s(TAG);

    }


    private MotionEvent genEvent(int action) {
        return MotionEvent.obtain(0, 0, action, 0, 0, 0);
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
