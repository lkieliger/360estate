package ch.epfl.sweng.project.tests3d;

import android.view.Display;
import android.view.Surface;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.data.panorama.HouseManager;
import ch.epfl.sweng.project.engine3d.PanoramaRenderer;
import ch.epfl.sweng.project.engine3d.listeners.RotSensorListener;

import static ch.epfl.sweng.project.util.DoubleArrayConverter.doubleToFloatArray;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class RotSensorListenerTest {

    private static double errorEpsilon = 0.1d;
    float[] values;
    PanoramaRenderer panoramaRenderer;
    RotSensorListener rotSensorListener;
    private Display display = Mockito.mock(Display.class);
    private HouseManager houseManager = Mockito.mock(HouseManager.class);
    private Quaternion q1;
    private Quaternion q2;
    private Quaternion q3;
    private Quaternion q4;
    private Quaternion q5;

    @Before
    public void initMocks() {
        panoramaRenderer = new PanoramaRenderer(RuntimeEnvironment.application, display, houseManager);
    }

    @Before
    public void initVariables() {
        Quaternion rot90AlongX = new Quaternion().fromAngleAxis(Vector3.Axis.X, 90);
        Quaternion rot90AlongZ = new Quaternion().fromAngleAxis(Vector3.Axis.Z, 90);
        q1 = new Quaternion();
        q2 = new Quaternion(q1).multiplyLeft(rot90AlongX);
        q3 = new Quaternion(q2).multiplyLeft(rot90AlongZ);
        q4 = new Quaternion(q3).multiplyLeft(rot90AlongZ);
        q5 = new Quaternion(q4).multiplyLeft(rot90AlongZ);
        values = doubleToFloatArray(new double[]{q1.x, q1.y, q1.z, -q1.w});
    }

    @Test
    public void coordMappingIsCorrectForPortrait() {

        rotSensorListener = new RotSensorListener(Surface.ROTATION_0, panoramaRenderer);
        rotSensorListener.sensorChanged(values);

        assertQuaternionEquals(q2, panoramaRenderer.getSensorRot());
    }

    @Test
    public void coordMappingIsCorrectForLandscape() {

        rotSensorListener = new RotSensorListener(Surface.ROTATION_90, panoramaRenderer);

        rotSensorListener.sensorChanged(values);
        assertQuaternionEquals(q3, panoramaRenderer.getSensorRot());
    }

    @Test
    public void coordMappingIsCorrectForInversePortrait() {

        rotSensorListener = new RotSensorListener(Surface.ROTATION_180, panoramaRenderer);

        rotSensorListener.sensorChanged(values);
        assertQuaternionEquals(q4, panoramaRenderer.getSensorRot());
    }

    @Test
    public void coordMappingIsCorrectForInverseLandscape() {

        rotSensorListener = new RotSensorListener(Surface.ROTATION_270, panoramaRenderer);

        rotSensorListener.sensorChanged(values);
        assertQuaternionEquals(q5, panoramaRenderer.getSensorRot());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullArgumentThrowsException() {
        rotSensorListener = new RotSensorListener(Surface.ROTATION_0, null);
        rotSensorListener = new RotSensorListener(Surface.ROTATION_90, null);
        rotSensorListener = new RotSensorListener(Surface.ROTATION_180, null);
        rotSensorListener = new RotSensorListener(Surface.ROTATION_270, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidRotationValueThrowsException() {
        int i = 0;
        while (i == Surface.ROTATION_0 || i == Surface.ROTATION_90 || i == Surface.ROTATION_180 || i == Surface
                .ROTATION_270)
            i++;

        rotSensorListener = new RotSensorListener(i, panoramaRenderer);
    }

    private void assertQuaternionEquals(Quaternion q1, Quaternion q2) {
        System.out.println("Expected: " + q1);
        System.out.println("Got     : " + q2);

        Assert.assertTrue(q1.equals(q2, errorEpsilon));
    }
}
