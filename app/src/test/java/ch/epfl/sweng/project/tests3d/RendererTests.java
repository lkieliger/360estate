package ch.epfl.sweng.project.tests3d;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.internal.Shadow;

import java.lang.reflect.Field;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.data.panorama.HouseManager;
import ch.epfl.sweng.project.engine3d.PanoramaRenderer;
import ch.epfl.sweng.project.engine3d.PanoramaRenderer.RenderingLogic;
import ch.epfl.sweng.project.engine3d.listeners.RotSensorListener;

import static ch.epfl.sweng.project.tests3d.AssertUtils.assertQuaternionEquals;
import static ch.epfl.sweng.project.tests3d.AssertUtils.assertQuaternionNotEquals;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class RendererTests {

    private static final String TAG = "RendererTests";

    @Mock
    private HouseManager mockedHouseManager;
    @Mock
    private SensorManager mockedSensorManager;
    @Mock
    private Sensor mockedSensor;

    private Display roboDisplay;
    private PanoramaRenderer panoramaRenderer;
    private DisplayMetrics metrics;
    private Context spiedContext;


    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        roboDisplay = Shadow.newInstanceOf(Display.class);
        spiedContext = Mockito.spy(RuntimeEnvironment.application);
        doReturn(mockedSensorManager).when(spiedContext).getSystemService(Context.SENSOR_SERVICE);
        metrics = RuntimeEnvironment.application.getResources().getDisplayMetrics();
    }

    @Test
    public void handleAvailableSensor() {
        when(mockedSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)).thenReturn(mockedSensor);
        panoramaRenderer = new PanoramaRenderer(spiedContext, roboDisplay, mockedHouseManager);

        panoramaRenderer.onResume();
        verify(mockedSensorManager, times(1)).registerListener(
                any(RotSensorListener.class),
                eq(mockedSensor),
                eq(SensorManager.SENSOR_DELAY_GAME)
        );
        panoramaRenderer.onPause();
        verify(mockedSensorManager, times(1)).unregisterListener(
                any(RotSensorListener.class));
    }

    @Test
    public void handleUnavailableSensor() {
        panoramaRenderer = new PanoramaRenderer(spiedContext, roboDisplay, mockedHouseManager);

        panoramaRenderer.onResume();
        panoramaRenderer.onPause();
        verify(mockedSensorManager, never()).registerListener(
                any(RotSensorListener.class),
                eq(mockedSensor),
                eq(SensorManager.SENSOR_DELAY_GAME)
        );
        verify(mockedSensorManager, never()).unregisterListener(
                any(RotSensorListener.class));
    }

    @Test
    public void initSceneIsCorrect() {
        panoramaRenderer = new PanoramaRenderer(spiedContext, roboDisplay, mockedHouseManager);


    }

    @Test
    public void userRotationIsCorrect() {
        panoramaRenderer = new PanoramaRenderer(spiedContext, roboDisplay, mockedHouseManager);

        double angleChange = 91;
        //Rotate the camera counter clockwise to simulate a swipe to the right
        Quaternion newRot = panoramaRenderer.getUserRotation().
                multiplyLeft(new Quaternion().fromAngleAxis(Vector3.Axis.Y, -angleChange));
        /*
             formula
             angle = (Math.cos(yaw) * xComp) + (Math.sin(yaw) * yComp);
         */
        float dx = angleToPixelDelta(angleChange / Math.cos(panoramaRenderer.getDeviceYaw()), true);
        panoramaRenderer.updateCameraRotation(dx, 0);

        assertQuaternionEquals(newRot, panoramaRenderer.getUserRotation());
    }

    @Test
    public void getUserRotPerformDefensiveCopying() {
        panoramaRenderer = new PanoramaRenderer(spiedContext, roboDisplay, mockedHouseManager);

        Quaternion userRot = panoramaRenderer.getUserRotation();
        userRot.multiplyLeft(new Quaternion().fromAngleAxis(Vector3.Axis.Y, 30));

        assertQuaternionNotEquals(panoramaRenderer.getUserRotation(), userRot);
    }

    @Test
    public void setDeviceYawIsCorrect() {
        panoramaRenderer = new PanoramaRenderer(spiedContext, roboDisplay, mockedHouseManager);

        panoramaRenderer.setDeviceYaw(123.456);
        assertEquals(123.456, panoramaRenderer.getDeviceYaw());
        panoramaRenderer.setDeviceYaw(Double.MIN_VALUE);
        assertEquals(Double.MIN_VALUE, panoramaRenderer.getDeviceYaw());
        panoramaRenderer.setDeviceYaw(Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, panoramaRenderer.getDeviceYaw());
        panoramaRenderer.setDeviceYaw(0.0);
        assertEquals(0.0, panoramaRenderer.getDeviceYaw());
    }

    @Test
    public void initiatePanoramaTransitionIsCorrect() {
        panoramaRenderer = new PanoramaRenderer(spiedContext, roboDisplay, mockedHouseManager);

        RenderingLogic logic = null;

        try {
            Field valueField = PanoramaRenderer.class.getDeclaredField("mSlidingRendering");
            valueField.setAccessible(true);
            try {
                logic = (RenderingLogic) valueField.get(panoramaRenderer);
            } catch (IllegalAccessException e) {
                Log.d(TAG, e.getMessage());
            }
        } catch (NoSuchFieldException e) {
            Log.d(TAG, e.getMessage());
        }

        panoramaRenderer.initiatePanoramaTransition("dummyUrl", 1);
        assertSame(logic, panoramaRenderer.getCurrentRenderingLogic());

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
