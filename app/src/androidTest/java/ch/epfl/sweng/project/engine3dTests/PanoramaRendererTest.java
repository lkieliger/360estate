package ch.epfl.sweng.project.engine3dTests;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.DisplayMetrics;
import android.util.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rajawali3d.cameras.Camera;
import org.rajawali3d.math.vector.Vector3;

import ch.epfl.sweng.project.engine3d.PanoramaActivity;
import ch.epfl.sweng.project.engine3d.PanoramaRenderer;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class PanoramaRendererTest {

    @Rule
    public ActivityTestRule<PanoramaActivity> mActivityTestRule = new ActivityTestRule<>(PanoramaActivity.class);
    private PanoramaRenderer renderer;
    private DisplayMetrics metrics;
    private Camera cam;
    private double errorEpsilon;

    @Before
    public void initMembers() {
        errorEpsilon = 1e-5;
        renderer = new PanoramaRenderer(mActivityTestRule.getActivity().getApplicationContext());
        metrics = mActivityTestRule.getActivity().getApplicationContext().getResources()
                .getDisplayMetrics();
        cam = renderer.getCurrentCamera();
    }


    @Test
    public void cameraConfigIsCorrect() {

        assertTrue(cam.isLookAtEnabled());
    }

    @Test
    public void cameraLookatIsCorrectAfterRot() {
        Vector3 expectedLookat = new Vector3(0, 0, 1);

        renderer.updateLookAt();
        assertAllCompononentEquals(expectedLookat, cam.getLookAt());

        Log.d("cameraLookatTest", "<0,0,1> passed");

        renderer.updateCameraRotation(angleToPixelDelta(Math.PI / 2, true), 0);
        renderer.updateLookAt();
        expectedLookat = new Vector3(1, 0, 0);
        assertAllCompononentEquals(expectedLookat, cam.getLookAt());

        Log.d("cameraLookatTest", "<1,0,0> passed");

        renderer.updateCameraRotation(angleToPixelDelta(Math.PI / 2, true), 0);
        renderer.updateLookAt();
        expectedLookat = new Vector3(0, 0, -1);
        assertAllCompononentEquals(expectedLookat, cam.getLookAt());

        Log.d("cameraLookatTest", "<0,0,-1> passed");

        renderer.updateCameraRotation(angleToPixelDelta(Math.PI / 2, true), 0);
        renderer.updateLookAt();
        expectedLookat = new Vector3(-1, 0, 0);
        assertAllCompononentEquals(expectedLookat, cam.getLookAt());

        Log.d("cameraLookatTest", "<-1,0,0> passed");

        renderer.updateCameraRotation(angleToPixelDelta(Math.PI / 2, true), 0);
        renderer.updateLookAt();
        expectedLookat = new Vector3(0, 0, 1);
        assertAllCompononentEquals(expectedLookat, cam.getLookAt());

    }

    /**
     * We need this method since the lookat vector is computed using
     * sines and cosines and inherently has some numerical errors
     *
     * @param v1 the expected value
     * @param v2 the value to test
     */
    private void assertAllCompononentEquals(Vector3 v1, Vector3 v2) {
        assertEquals(v1.x, v2.x, errorEpsilon);
        assertEquals(v1.y, v2.y, errorEpsilon);
        assertEquals(v1.z, v2.z, errorEpsilon);

    }

    @Test
    public void cameraPhiIsBounded() {
        int turns = 3;
        float steps = 30f;
        float delta = 6.28319f / steps;

        for (int i = 0; i < turns * steps; i++) {
            renderer.updateCameraRotation(angleToPixelDelta(delta, true), 0);
            double phi = renderer.getCameraRotationPhi();
            assertTrue(0 <= phi && phi <= PanoramaRenderer.MAX_PHI);

        }

        for (int i = 0; i < 2 * turns * steps; i++) {
            renderer.updateCameraRotation(angleToPixelDelta(-delta, true), 0);
            double phi = renderer.getCameraRotationPhi();
            assertTrue(0 <= phi && phi <= PanoramaRenderer.MAX_PHI);
        }
    }

    @Test
    public void cameraThetaIsBounded() {
        int turns = 3;
        float steps = 15f;
        float delta = 3.14159f / steps;

        for (int i = 0; i < turns * steps; i++) {
            renderer.updateCameraRotation(0, angleToPixelDelta(delta, false));
            double theta = renderer.getCameraRotationTheta();
            assertTrue(PanoramaRenderer.EPSILON <= theta && theta <= PanoramaRenderer.MAX_THETA);
        }

        for (int i = 0; i < 2 * turns * steps; i++) {
            renderer.updateCameraRotation(0, angleToPixelDelta(-delta, false));
            double theta = renderer.getCameraRotationTheta();
            assertTrue(PanoramaRenderer.EPSILON <= theta && theta <= PanoramaRenderer.MAX_THETA);
        }
    }

    @Test
    /**
     * The camera sensitivity should depend on the dpi of the device
     * so that a swipe has the same effect regardless of the dx or dy
     * reported by the touch listener
     */
    public void cameraSensitivityIsCorrect() {

        double phi = renderer.getCameraRotationPhi();
        double theta = renderer.getCameraRotationTheta();

        renderer.updateCameraRotation(10, -10);

        double camPhi = renderer.getCameraRotationPhi();
        double camTheta = renderer.getCameraRotationTheta();

        double newPhi = phi + ((10d / metrics.xdpi) * PanoramaRenderer.SENSITIVITY);
        double newTheta = theta + ((10d / metrics.ydpi) * PanoramaRenderer.SENSITIVITY);

        assertEquals(newPhi, camPhi);
        assertEquals(newTheta, camTheta);

    }

    private float angleToPixelDelta(double angle, boolean isAlongXAxis) {
        if (isAlongXAxis) {
            return (float) ((angle / PanoramaRenderer.SENSITIVITY) * metrics.xdpi);
        } else {
            return (float) ((angle / PanoramaRenderer.SENSITIVITY) * metrics.ydpi);
        }
    }
}
