package ch.epfl.sweng.project.tests3d;


import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.data.panorama.HouseManager;
import ch.epfl.sweng.project.data.panorama.adapters.SpatialData;
import ch.epfl.sweng.project.engine3d.PanoramaRenderer;
import ch.epfl.sweng.project.engine3d.components.PanoramaInfoCloser;
import ch.epfl.sweng.project.engine3d.components.PanoramaInfoDisplay;
import ch.epfl.sweng.project.engine3d.components.PanoramaSphere;
import ch.epfl.sweng.project.engine3d.listeners.RotSensorListener;

import static ch.epfl.sweng.project.tests3d.AssertUtils.assertQuaternionEquals;
import static ch.epfl.sweng.project.tests3d.AssertUtils.assertQuaternionNotEquals;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
    @Mock
    private Bitmap mockedBitmap;
    @Mock
    private PanoramaSphere mockedSphere;
    @Captor
    private ArgumentCaptor<PanoramaInfoDisplay> infoDisplayCaptor;
    @Captor
    private ArgumentCaptor<PanoramaInfoCloser> infoCloserCaptor;
    @Captor
    private ArgumentCaptor<List<SpatialData>> spatialDataListCaptor;
    @Captor
    private ArgumentCaptor<Integer> intCaptor;
    @Captor
    private ArgumentCaptor<Bitmap> bitmapCaptor;

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
        when(mockedHouseManager.getAttachedDataFromId(anyInt())).thenReturn(new ArrayList<SpatialData>());
        panoramaRenderer = new PanoramaRenderer(spiedContext, roboDisplay, mockedHouseManager);
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
    public void userRotationIsCorrect() {


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


        Quaternion userRot = panoramaRenderer.getUserRotation();
        userRot.multiplyLeft(new Quaternion().fromAngleAxis(Vector3.Axis.Y, 30));

        assertQuaternionNotEquals(panoramaRenderer.getUserRotation(), userRot);
    }

    @Test
    public void setDeviceYawIsCorrect() {


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


        panoramaRenderer.initiatePanoramaTransition("dummyUrl", 1);
        assertFalse(PanoramaRenderer.NextPanoramaDataBuilder.isReset());
        panoramaRenderer.cancelPanoramaUpdate();
        assertSame(panoramaRenderer.getSlidingRendering(), panoramaRenderer.getCurrentRenderingLogic());
    }

    @Test
    public void deleteInfoIsCorrect() {
        PanoramaInfoDisplay mockedInfoDisplay = Mockito.mock(PanoramaInfoDisplay.class);
        PanoramaInfoCloser mockedInfoCloser = Mockito.mock(PanoramaInfoCloser.class);


        inject(panoramaRenderer, mockedSphere, "mPanoSphere");

        panoramaRenderer.deleteInfo(mockedInfoDisplay, mockedInfoCloser);
        verify(mockedSphere).deleteTextToDisplay(infoDisplayCaptor.capture(),
                infoCloserCaptor.capture());
        assertEquals(mockedInfoDisplay, infoDisplayCaptor.getValue());
        assertEquals(mockedInfoCloser, infoCloserCaptor.getValue());
    }

    @Test
    public void zoomOnTextIsCorrect() {


        panoramaRenderer.zoomOnText(0, 0, 0);
        assertSame(panoramaRenderer.getSlidingToTextRendering(), panoramaRenderer.getCurrentRenderingLogic());
    }

    @Test
    public void zoomOutIsCorrect() {


        panoramaRenderer.zoomOut(0);
        assertSame(panoramaRenderer.getSlidingOutOfTextRendering(), panoramaRenderer.getCurrentRenderingLogic());
    }

    @Test
    public void prepareSceneIsCorrect() {

        panoramaRenderer.prepareScene(null);
        assertTrue(PanoramaRenderer.NextPanoramaDataBuilder.isReset());
        assertSame(panoramaRenderer.getIdleRendering(), panoramaRenderer.getCurrentRenderingLogic());


        panoramaRenderer.prepareScene(mockedBitmap);
        assertFalse(PanoramaRenderer.NextPanoramaDataBuilder.isReset());
        verifyNoMoreInteractions(mockedBitmap);
    }

    @Test
    public void updateSceneIsCorrect() {

        PanoramaRenderer.NextPanoramaDataBuilder.setNextPanoId(123);
        PanoramaRenderer.NextPanoramaDataBuilder.setNextPanoBitmap(mockedBitmap);

        inject(panoramaRenderer, mockedSphere, "mPanoSphere");

        panoramaRenderer.updateScene();
        verify(mockedSphere, times(1)).detachPanoramaComponents();
        verify(mockedSphere, times(1)).setPhotoTexture(bitmapCaptor.capture());
        assertEquals(mockedBitmap, bitmapCaptor.getValue());
        verify(mockedHouseManager, times(1)).getAttachedDataFromId(intCaptor.capture());
        assertEquals(Integer.valueOf(123), intCaptor.getValue());
        verify(mockedSphere, times(1)).attachPanoramaComponents(spatialDataListCaptor.capture());
        assertEquals(new ArrayList<SpatialData>(), spatialDataListCaptor.getValue());
    }

    @Test
    public void cancelPanoramaUpdateResetsBuilder() {
        panoramaRenderer.cancelPanoramaUpdate();
        assertTrue(PanoramaRenderer.NextPanoramaDataBuilder.isReset());
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

    private <E, S> E inject(E intoObj, S injection, String fieldName) {
        try {
            Field valueField = intoObj.getClass().getDeclaredField(fieldName);
            valueField.setAccessible(true);
            try {
                valueField.set(intoObj, injection);
            } catch (IllegalAccessException e) {
                Log.e("INJECTOR", e.getMessage());
            }
        } catch (NoSuchFieldException e) {
            Log.e("INJECTOR", e.getMessage());
        }

        return intoObj;
    }
}
