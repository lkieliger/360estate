package ch.epfl.sweng.project.tests3d;

import android.util.DisplayMetrics;
import android.view.Display;

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

import java.util.Locale;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.engine3d.PanoramaRenderer;
import ch.epfl.sweng.project.engine3d.listeners.PanoramaTouchListener;
import ch.epfl.sweng.project.features.SplashActivity;

import static ch.epfl.sweng.project.util.DebugPrinter.printRendererDebug;
import static ch.epfl.sweng.project.util.UnitTestUtilityFunctions.wait1s;
import static ch.epfl.sweng.project.util.UnitTestUtilityFunctions.wait500ms;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)

public class PanoramaRendererTests {

    private static final String TAG = "Panorama unit tests";
    private static double errorEpsilon = 0.1d;
    private SplashActivity dummyActivity;
    private PanoramaRenderer panoramaRenderer;
    private PanoramaTouchListener panoramaTouchListener;
    private DisplayMetrics metrics = null;

    @SuppressWarnings("ObjectEqualsNull")
    @Test
    public void panoramaRenderTests() {

        dummyActivity = Robolectric.buildActivity(SplashActivity.class).
                create().get();
        Display display = Shadow.newInstanceOf(Display.class);
        panoramaRenderer = new PanoramaRenderer(dummyActivity.getBaseContext(), display, null);
        panoramaTouchListener = new PanoramaTouchListener(panoramaRenderer);

        sensorRotHandlingTest();
        userRotHandlingTest();
//Tests yaw
        panoramaRenderer.setDeviceYaw(123.456);
        assertEquals(123.456, panoramaRenderer.getDeviceYaw());
        printRendererDebug("PanoramaRendererTests", panoramaRenderer);
    }

    private void sensorRotHandlingTest() {

        assertTrue(panoramaRenderer.getSensorRot().equals(new Quaternion()));

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

    }

    private void userRotHandlingTest() {
        double angleChange = 91;
        //Rotate the camera counter clockwise to simulate a swipe to the right
        Quaternion newRot = panoramaRenderer.getUserRotation().
                multiplyLeft(new Quaternion().fromAngleAxis(Vector3.Axis.Y, -angleChange));
        wait500ms(TAG);

        /*
             formula
             angle = (Math.cos(yaw) * xComp) + (Math.sin(yaw) * yComp);
         */
        float dx = angleToPixelDelta(angleChange / Math.cos(panoramaRenderer.getDeviceYaw()), true);
        panoramaRenderer.updateCameraRotation(dx, 0);

        wait1s(TAG);
        assertQuaternionEquals(newRot, panoramaRenderer.getUserRotation(), true);
    }


    private void assertQuaternionEquals(Quaternion v1, Quaternion v2, boolean shouldBeEqual) {

        System.out.println(String.format(Locale.getDefault(), "V1: %1$.2f, %2$.2f, %3$.2f, %4$.2f", v1.w, v1.x, v1.y,
                v1.z));
        System.out.println(String.format(Locale.getDefault(), "V2: %1$.2f, %2$.2f, %3$.2f, %4$.2f", v2.w, v2.x, v2.y,
                v2.z));
        if (shouldBeEqual) {
            Assert.assertTrue(v1.equals(v2, errorEpsilon));
        } else {
            assertFalse(v1.equals(v2, errorEpsilon));
        }
    }

    /**
     * Compute the number of pixels needed for a user swipe to turn the camera a given angle
     *
     * @param angle        in degrees
     * @param isAlongXAxis true if the swipe is along x axis, false otherwise
     * @return The pixel number
     */
    private float angleToPixelDelta(double angle, boolean isAlongXAxis) {
        if (isAlongXAxis) {
            return (float) ((angle / PanoramaRenderer.SENSITIVITY) * metrics.xdpi);
        } else {
            return (float) ((angle / PanoramaRenderer.SENSITIVITY) * metrics.ydpi);
        }
    }



}
