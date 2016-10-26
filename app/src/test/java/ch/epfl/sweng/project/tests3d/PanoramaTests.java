package ch.epfl.sweng.project.tests3d;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.rajawali3d.cameras.Camera;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.internal.Shadow;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.data.HouseManager;
import ch.epfl.sweng.project.engine3d.PanoramaActivity;
import ch.epfl.sweng.project.engine3d.PanoramaRenderer;
import ch.epfl.sweng.project.engine3d.PanoramaTouchListener;
import ch.epfl.sweng.project.engine3d.RotSensorListener;
import ch.epfl.sweng.project.user.LoginActivity;

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
import static ch.epfl.sweng.project.util.UnitTestUtilityFunctions.wait1s;
import static ch.epfl.sweng.project.util.UnitTestUtilityFunctions.wait250ms;
import static ch.epfl.sweng.project.util.UnitTestUtilityFunctions.wait500ms;
import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)

public class PanoramaTests {

    private static final String TAG = "Panorama unit tests";
    private static double errorEpsilon = 0.1d;
    private DisplayMetrics metrics = null;


    @SuppressWarnings("ObjectEqualsNull")
    @Test
    public void panoramaRenderTests() {

        LoginActivity loginActivity = Robolectric.buildActivity(LoginActivity.class).
                create().get();

        Display display = Shadow.newInstanceOf(Display.class);

        PanoramaRenderer panoramaRenderer = new PanoramaRenderer(loginActivity.getBaseContext(), display, null);

        RotSensorListener rotSensorListener = new RotSensorListener(display, panoramaRenderer);
        assertTrue(rotSensorListener.getDummyRotation().equals(new Quaternion()));

        Camera cam = panoramaRenderer.getCurrentCamera();
        assertFalse(cam.isLookAtEnabled());

        metrics = panoramaRenderer.getContext().getResources().getDisplayMetrics();

        // setSensorRotIsCorrect test

        Quaternion q1 = new Quaternion().fromAngleAxis(Vector3.Axis.X, 96);

        //Check for defensive copy on render side
        panoramaRenderer.setSensorRotation(q1);
        q1.multiply(new Quaternion().fromAngleAxis(Vector3.Axis.Y, 90));

        wait500ms(TAG);
        assertQuaternionEquals(q1, panoramaRenderer.getSensorRot(), false);




    /*
     *  * cameraSensitivityIsCorrect test
     * The camera sensitivity should depend on the dpi of the device
     * so that a swipe has the same effect regardless of the dx or dy
     * reported by the touch listener
     */

        double angleChange = 90;

        Quaternion newRot = panoramaRenderer.getUserRotation().
                multiplyLeft(new Quaternion().fromAngleAxis(Vector3.Axis.Y, -angleChange));
        wait500ms(TAG);


        float phi = angleToPixelDelta(angleChange, true);
        panoramaRenderer.updateCameraRotation(phi, 0);

        wait1s(TAG);
        assertQuaternionEquals(newRot, panoramaRenderer.getUserRotation(), true);

        // consumes valid input

        View.OnTouchListener touchListener = new PanoramaTouchListener(panoramaRenderer);
        wait250ms(TAG);

        View view = new View(loginActivity.getApplicationContext());


        Assert.assertTrue(touchListener.onTouch(view, genEvent(ACTION_DOWN)));
        Assert.assertTrue(touchListener.onTouch(view, genEvent(ACTION_UP)));
        Assert.assertTrue(touchListener.onTouch(view, genEvent(ACTION_MOVE)));
        Assert.assertTrue(touchListener.onTouch(view, genEvent(ACTION_CANCEL)));
        Assert.assertTrue(touchListener.onTouch(view, genEvent(ACTION_POINTER_UP)));

        // does not ConsumeInvalidInput test

        assertFalse(touchListener.onTouch(view, genEvent(ACTION_HOVER_ENTER)));
        assertFalse(touchListener.onTouch(view, genEvent(ACTION_HOVER_EXIT)));
        assertFalse(touchListener.onTouch(view, genEvent(ACTION_HOVER_MOVE)));
        assertFalse(touchListener.onTouch(view, genEvent(ACTION_OUTSIDE)));
        assertFalse(touchListener.onTouch(view, genEvent(ACTION_POINTER_DOWN)));


    }




    private void assertQuaternionEquals(Quaternion v1, Quaternion v2, boolean shouldBeEqual) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, String.format("V1: %1$.2f, %2$.2f, %3$.2f, %4$.2f", v1.w, v1.x, v1.y, v1.z));
            Log.d(TAG, String.format("V2: %1$.2f, %2$.2f, %3$.2f, %4$.2f", v2.w, v2.x, v2.y, v2.z));
        }
        if (shouldBeEqual) {
            Assert.assertTrue(v1.equals(v2, errorEpsilon));
        } else {
            assertFalse(v1.equals(v2, errorEpsilon));
        }
    }

    private float angleToPixelDelta(double angle, boolean isAlongXAxis) {
        if (isAlongXAxis) {
            return (float) ((angle / PanoramaRenderer.SENSITIVITY) * metrics.xdpi);
        } else {
            return (float) ((angle / PanoramaRenderer.SENSITIVITY) * metrics.ydpi);
        }
    }

    private MotionEvent genEvent(int action) {
        return MotionEvent.obtain(0, 0, action, 0, 0, 0);
    }

}
