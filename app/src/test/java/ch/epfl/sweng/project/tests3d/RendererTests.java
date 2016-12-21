package ch.epfl.sweng.project.tests3d;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.Surface;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.rajawali3d.cameras.Camera;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.util.ObjectColorPicker;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.data.ImageMgmt;
import ch.epfl.sweng.project.data.panorama.HouseManager;
import ch.epfl.sweng.project.data.panorama.adapters.SpatialData;
import ch.epfl.sweng.project.data.panorama.adapters.TransitionObject;
import ch.epfl.sweng.project.engine3d.PanoramaRenderer;
import ch.epfl.sweng.project.engine3d.PanoramaRenderer.RenderingLogic;
import ch.epfl.sweng.project.engine3d.components.PanoramaComponentType;
import ch.epfl.sweng.project.engine3d.components.PanoramaInfoDisplay;
import ch.epfl.sweng.project.engine3d.components.PanoramaInfoObject;
import ch.epfl.sweng.project.engine3d.components.PanoramaObject;
import ch.epfl.sweng.project.engine3d.components.PanoramaSphere;
import ch.epfl.sweng.project.engine3d.listeners.RotSensorListener;
import ch.epfl.sweng.project.util.Tuple;
import edu.emory.mathcs.backport.java.util.Arrays;

import static ch.epfl.sweng.project.engine3d.PanoramaRenderer.CAM_TRAVEL_DISTANCE;
import static ch.epfl.sweng.project.engine3d.PanoramaRenderer.DISTANCE_TO_DISPLAY;
import static ch.epfl.sweng.project.engine3d.PanoramaRenderer.FOV_LANDSCAPE;
import static ch.epfl.sweng.project.engine3d.PanoramaRenderer.FOV_PORTRAIT;
import static ch.epfl.sweng.project.engine3d.PanoramaRenderer.LERP_FACTOR;
import static ch.epfl.sweng.project.engine3d.PanoramaRenderer.ORIGIN;
import static ch.epfl.sweng.project.util.UnitTestUtilityFunctions.assertQuaternionEquals;
import static ch.epfl.sweng.project.util.UnitTestUtilityFunctions.assertQuaternionNotEquals;
import static ch.epfl.sweng.project.util.UnitTestUtilityFunctions.inject;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
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
    private TransitionObject mockedSpatialData;
    @Mock
    private SensorManager mockedSensorManager;
    @Mock
    private Sensor mockedSensor;
    @Mock
    private Bitmap mockedBitmap;
    @Mock
    private PanoramaSphere mockedSphere;
    @Mock
    private Camera mockedCamera;
    @Captor
    private ArgumentCaptor<PanoramaInfoDisplay> infoDisplayCaptor;
    @Captor
    private ArgumentCaptor<List<SpatialData>> spatialDataListCaptor;
    @Captor
    private ArgumentCaptor<Integer> intCaptor;
    @Captor
    private ArgumentCaptor<Bitmap> bitmapCaptor;
    @Captor
    private ArgumentCaptor<Quaternion> quaternionCaptor;
    @Captor
    private ArgumentCaptor<Float> floatCaptor1;
    @Captor
    private ArgumentCaptor<Float> floatCaptor2;
    @Captor
    private ArgumentCaptor<Vector3> vectorCaptor;
    @Captor
    private ArgumentCaptor<Double> doubleCaptor;
    @Captor
    private ArgumentCaptor<Target> picassoTargetCaptor;


    private PanoramaRenderer panoramaRenderer;
    private DisplayMetrics metrics;
    private Context spiedContext;
    private boolean testLogicIsCalled = false;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        spiedContext = Mockito.spy(RuntimeEnvironment.application);
        doReturn(mockedSensorManager).when(spiedContext).getSystemService(Context.SENSOR_SERVICE);
        metrics = RuntimeEnvironment.application.getResources().getDisplayMetrics();

        //noinspection ConfusingArgumentToVarargsMethod
        when(mockedSpatialData.getType()).thenReturn(PanoramaComponentType.TRANSITION, null);

        List<SpatialData> spatialDataList = new ArrayList<>();
        spatialDataList.add(mockedSpatialData);
        spatialDataList.add(mockedSpatialData); //Second instance will return null
        when(mockedHouseManager.getAttachedDataFromId(anyInt()))
                .thenReturn(spatialDataList);

        panoramaRenderer = new PanoramaRenderer(spiedContext, Surface.ROTATION_0, mockedHouseManager);
    }

    @Test
    public void adjustsFOVBasedOnScreenOrientation() {

        assertEquals(FOV_PORTRAIT, panoramaRenderer.getCurrentCamera().getFieldOfView());

        panoramaRenderer = new PanoramaRenderer(RuntimeEnvironment.application, Surface.ROTATION_90,
                mockedHouseManager);
        assertEquals(FOV_LANDSCAPE, panoramaRenderer.getCurrentCamera().getFieldOfView());

        panoramaRenderer = new PanoramaRenderer(RuntimeEnvironment.application, Surface.ROTATION_180,
                mockedHouseManager);
        assertEquals(FOV_PORTRAIT, panoramaRenderer.getCurrentCamera().getFieldOfView());

        panoramaRenderer = new PanoramaRenderer(RuntimeEnvironment.application, Surface.ROTATION_270,
                mockedHouseManager);
        assertEquals(FOV_LANDSCAPE, panoramaRenderer.getCurrentCamera().getFieldOfView());

        panoramaRenderer = new PanoramaRenderer(RuntimeEnvironment.application, -12345,
                mockedHouseManager);
        assertEquals(FOV_PORTRAIT, panoramaRenderer.getCurrentCamera().getFieldOfView());

    }

    @Test
    public void handleAvailableSensor() {
        when(mockedSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)).thenReturn(mockedSensor);
        panoramaRenderer = new PanoramaRenderer(spiedContext, Surface.ROTATION_0, mockedHouseManager);

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
        Quaternion newRot = modifyUserRot(angleChange);
        assertQuaternionEquals(newRot, panoramaRenderer.getUserRotation());
    }

    @Test
    public void getUserRotPerformDefensiveCopying() {
        Quaternion userRot = panoramaRenderer.getUserRotation();
        userRot.multiply(123);

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

        new InjectedRendererBuilder(panoramaRenderer).withMockedPanoSphere();

        panoramaRenderer.deleteInfo(mockedInfoDisplay);
        verify(mockedSphere).deleteTextToDisplay(infoDisplayCaptor.capture());
        assertEquals(mockedInfoDisplay, infoDisplayCaptor.getValue());
    }

    @Test
    public void rotateDisplayInfoObjectIsCorrect() {
        PanoramaInfoObject mockedPanoramaInfo = Mockito.mock(PanoramaInfoObject.class);
        when(mockedPanoramaInfo.isDisplay()).thenReturn(true, false);

        panoramaRenderer.rotateDisplayInfoObject(mockedPanoramaInfo);
        panoramaRenderer.rotateDisplayInfoObject(mockedPanoramaInfo);
        assertEquals(panoramaRenderer.getRotateObjectRendering(), panoramaRenderer.getCurrentRenderingLogic());
    }

    @Test
    public void zoomOutAndRotateIsCorrect() {
        PanoramaInfoObject mockedPanoramaInfo = Mockito.mock(PanoramaInfoObject.class);

        panoramaRenderer.zoomOutAndRotate(0, mockedPanoramaInfo);
        verify(mockedPanoramaInfo, atLeastOnce()).isDisplay();
        assertEquals(panoramaRenderer.getRotateObjectAndZoomOutRendering(),
                panoramaRenderer.getCurrentRenderingLogic());
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

        new InjectedRendererBuilder(panoramaRenderer).withMockedPanoSphere();

        panoramaRenderer.updateScene();
        verify(mockedSphere, times(1)).detachPanoramaComponents();
        verify(mockedSphere, times(1)).setPhotoTexture(bitmapCaptor.capture());
        assertEquals(mockedBitmap, bitmapCaptor.getValue());
        verify(mockedHouseManager, times(1)).getAttachedDataFromId(intCaptor.capture());
        assertEquals(Integer.valueOf(123), intCaptor.getValue());
        verify(mockedSphere, times(1)).attachPanoramaComponents(spatialDataListCaptor.capture());
        List<SpatialData> spatialDataList = new ArrayList<>();
        spatialDataList.add(mockedSpatialData);
        spatialDataList.add(mockedSpatialData);
        assertEquals(spatialDataList, spatialDataListCaptor.getValue());
    }

    @Test
    public void cancelPanoramaUpdateResetsBuilder() {
        panoramaRenderer.cancelPanoramaUpdate();
        assertTrue(PanoramaRenderer.NextPanoramaDataBuilder.isReset());
    }

    @Test
    public void correctlyHandlesFailedPicassoLoad() {
        new InjectedRendererBuilder(panoramaRenderer)
                .withMockedCamera()
                .withSlidingRendering();
        panoramaRenderer.handleFailure();
        verify(mockedCamera).setPosition(vectorCaptor.capture());
        assertEquals(ORIGIN, vectorCaptor.getValue());
        assertEquals(panoramaRenderer.getIdleRendering(), panoramaRenderer.getCurrentRenderingLogic());
        assertTrue(PanoramaRenderer.NextPanoramaDataBuilder.isReset());
    }

    @Test
    public void updateCameraIsCorrect() {
        Camera mockedCamera = Mockito.mock(Camera.class);

        inject(panoramaRenderer, mockedCamera, "mCamera");

        panoramaRenderer.updateCameraRotation(-100, 0);
        panoramaRenderer.setSensorRotation(new Quaternion(0.5, 0.5, 0.5, 0.5));
        panoramaRenderer.updateCamera();
        verify(mockedCamera, times(1)).setCameraOrientation(quaternionCaptor.capture());
        assertQuaternionEquals(panoramaRenderer.getSensorRot().multiply(panoramaRenderer.getUserRotation()),
                quaternionCaptor.getValue());
    }

    @Test
    public void getSensorRotPerformsDefensiveCopying() {
        Quaternion rot = panoramaRenderer.getSensorRot();
        rot.multiply(123);

        assertQuaternionNotEquals(panoramaRenderer.getSensorRot(), rot);
    }

    @Test
    public void setSensorRotationIsCorrect() {

        Quaternion q = new Quaternion().fromAngleAxis(Vector3.Axis.Y, 120);

        panoramaRenderer.setSensorRotation(q);

        assertQuaternionEquals(q, panoramaRenderer.getSensorRot());
        assertNotSame(q, panoramaRenderer.getSensorRot());
    }

    @Test
    public void getPanoramaShereIsCorrect() {
        new InjectedRendererBuilder(panoramaRenderer).withMockedPanoSphere();
        assertSame(mockedSphere, panoramaRenderer.getPanoramaSphere());
    }

    @Test
    public void getObjectAtIsCorrect() {
        ObjectColorPicker mockedPicker = Mockito.mock(ObjectColorPicker.class);
        inject(panoramaRenderer, mockedPicker, "mPicker");
        panoramaRenderer.getObjectAt(34, 56);

        verify(mockedPicker, times(1)).getObjectAt(floatCaptor1.capture(), floatCaptor2.capture());
        assertEquals(34.0f, floatCaptor1.getValue());
        assertEquals(56.0f, floatCaptor2.getValue());
    }

    @Test
    public void onRenderCallsRespectiveRenderLogic() {

        RenderingLogic testLogic = new RenderingLogic() {
            @Override
            public void render() {
                callFromTestLogic();
            }
        };

        inject(panoramaRenderer, testLogic, "mRenderLogic");
        panoramaRenderer.onRender(0, 0);
        assertTrue(testLogicIsCalled);
    }

    private void callFromTestLogic() {
        testLogicIsCalled = true;
    }

    @Test
    public void onRenderUpdatesCameraOnCorrectRenderLogics() {

        modifyUserRot(35);
        panoramaRenderer.setSensorRotation(new Quaternion().fromAngleAxis(Vector3.Axis.Z, 47));

        Quaternion ref = panoramaRenderer.getSensorRot().multiply(panoramaRenderer.getUserRotation());

        Camera mockedCamera = Mockito.mock(Camera.class);
        when(mockedCamera.getPosition()).thenReturn(new Vector3(1, 0, 0));


        inject(panoramaRenderer, mockedCamera, "mCamera");
        //Idle sensor + user
        panoramaRenderer.onRender(0, 0);
        verify(mockedCamera, atLeastOnce()).setCameraOrientation(quaternionCaptor.capture());
        assertQuaternionEquals(ref, quaternionCaptor.getValue());

        //Sliding to text means special rotation
        new InjectedRendererBuilder(panoramaRenderer).withSlidingToTextRendering();
        panoramaRenderer.onRender(0, 0);
        verify(mockedCamera, atLeastOnce()).setCameraOrientation(quaternionCaptor.capture());
        assertQuaternionNotEquals(ref, quaternionCaptor.getValue());

        //Sliding out, special rotation
        new InjectedRendererBuilder(panoramaRenderer).withSlidingOutOfTextRendering();
        panoramaRenderer.onRender(0, 0);
        verify(mockedCamera, atLeastOnce()).setCameraOrientation(quaternionCaptor.capture());
        assertQuaternionNotEquals(ref, quaternionCaptor.getValue());

        //Sliding out and rotate, special rotation
        new InjectedRendererBuilder(panoramaRenderer).withRotateObjectAndZoomOutRendering();
        PanoramaInfoObject mockedObjectToRotate = Mockito.mock(PanoramaInfoObject.class);
        when(mockedObjectToRotate.getX()).thenReturn(1.0);
        when(mockedObjectToRotate.getY()).thenReturn(2.0);
        when(mockedObjectToRotate.getZ()).thenReturn(3.0);
        inject(panoramaRenderer, mockedObjectToRotate, "objectToRotate");
        panoramaRenderer.onRender(0, 0);
        verify(mockedCamera, atLeastOnce()).setCameraOrientation(quaternionCaptor.capture());
        assertQuaternionNotEquals(ref, quaternionCaptor.getValue());

        //All other should update camera
        new InjectedRendererBuilder(panoramaRenderer).withSlidingRendering();
        panoramaRenderer.onRender(0, 0);
        verify(mockedCamera, atLeastOnce()).setCameraOrientation(quaternionCaptor.capture());
        assertQuaternionEquals(ref, quaternionCaptor.getValue());

        PanoramaRenderer.NextPanoramaDataBuilder.setNextPanoId(1);
        PanoramaRenderer.NextPanoramaDataBuilder.setNextPanoBitmap(mockedBitmap);
        new InjectedRendererBuilder(panoramaRenderer).withTransitioningRendering().withMockedPanoSphere();
        panoramaRenderer.onRender(0, 0);
        verify(mockedCamera, atLeastOnce()).setCameraOrientation(quaternionCaptor.capture());
        assertQuaternionEquals(ref, quaternionCaptor.getValue());

    }

    @Test
    public void onObjectPickedIsCorrect() {
        PanoramaObject mockPanoObject = Mockito.mock(PanoramaObject.class);
        PanoramaObject mockPanoObject2 = Mockito.mock(PanoramaObject.class);
        when(mockPanoObject.getWorldPosition()).thenReturn(new Vector3(0, 0, 0));

        panoramaRenderer.onObjectPicked(mockPanoObject);
        verify(mockPanoObject, times(1)).reactWith(panoramaRenderer);

        panoramaRenderer.zoomOnText(0, 0, 0);
        panoramaRenderer.onObjectPicked(mockPanoObject);
        verify(mockPanoObject, times(2)).reactWith(panoramaRenderer);

        panoramaRenderer.zoomOut(0);
        panoramaRenderer.onObjectPicked(mockPanoObject2);
        verifyNoMoreInteractions(mockPanoObject2);

        //TODO: complete with more rendering logics
    }

    @Test
    public void idleRenderingIsCorrect() {
        //Compute 3 secs worth of frames
        for (int i = 0; i < 60 * 3; i++)
            panoramaRenderer.onRender(i, 1);
        assertNoSideEffects();
    }

    @Test
    public void transitioningRenderingIsCorrect() {
        setupPanoramaBuilder();
        new InjectedRendererBuilder(panoramaRenderer).withTransitioningRendering()
                .withMockedPanoSphere()
                .withMockedCamera();
        panoramaRenderer.onRender(0, 0);

        verify(mockedCamera, times(1)).setPosition(PanoramaRenderer.ORIGIN);
        assertEquals(panoramaRenderer.getIdleRendering(), panoramaRenderer.getCurrentRenderingLogic());

    }

    @Test
    public void slidingRenderingIsCorrect() {
        Vector3 target = new Vector3(0, 0, 0);
        Vector3 pos1 = new Vector3(5, 0, 0);
        Vector3 pos2 = new Vector3(20, 0, 0);
        Vector3 pos3 = new Vector3(CAM_TRAVEL_DISTANCE, 0, 0);
        //Simulate a movement
        when(mockedCamera.getPosition()).thenReturn(pos1, pos1, pos2, pos2, pos3, pos3);

        new InjectedRendererBuilder(panoramaRenderer)
                .withSlidingRendering()
                .withMockedCamera();

        panoramaRenderer.resetTargetPos();
        panoramaRenderer.onRender(0, 0);
        verify(mockedCamera, atLeastOnce()).setPosition(vectorCaptor.capture());
        assertEquals(pos1.lerp(target, LERP_FACTOR), vectorCaptor.getValue());

        panoramaRenderer.resetTargetPos();
        panoramaRenderer.onRender(0, 0);
        verify(mockedCamera, atLeastOnce()).setPosition(vectorCaptor.capture());
        assertEquals(pos2.lerp(target, LERP_FACTOR), vectorCaptor.getValue());

        //Camera is at destination, but pano data is not ready
        panoramaRenderer.onRender(0, 0);
        assertEquals(panoramaRenderer.getSlidingRendering(), panoramaRenderer.getCurrentRenderingLogic());
        //Now should be ready
        setupPanoramaBuilder();
        panoramaRenderer.onRender(0, 0);
        assertEquals(panoramaRenderer.getTransitioningRendering(), panoramaRenderer.getCurrentRenderingLogic());
    }

    @Test
    public void slidingToTextRenderingIsCorrect() {
        Vector3 target = new Vector3(0, 0, 0);
        Vector3 pos1 = new Vector3(1, 0, 0);
        Vector3 pos2 = new Vector3(DISTANCE_TO_DISPLAY, 0, 0);
        //Simulate a movement
        when(mockedCamera.getPosition()).thenReturn(pos1, pos1, pos2, pos2);

        new InjectedRendererBuilder(panoramaRenderer)
                .withMockedCamera()
                .withSlidingToTextRendering();

        panoramaRenderer.resetTargetPos();
        panoramaRenderer.onRender(0, 0);
        verify(mockedCamera, atLeastOnce()).setPosition(vectorCaptor.capture());
        assertEquals(pos1.lerp(target, LERP_FACTOR), vectorCaptor.getValue());

        panoramaRenderer.onRender(0, 0);
        verify(mockedCamera, atLeastOnce()).getPosition();
        verify(mockedCamera, atLeastOnce()).setCameraOrientation(any(Quaternion.class));
        verifyNoMoreInteractions(mockedCamera);
    }

    @Test
    public void slidingOutOfTextRenderingIsCorrect() {
        Vector3 pos1 = new Vector3(DISTANCE_TO_DISPLAY, 0, 0);
        Vector3 pos2 = new Vector3(0, 0, 0);
        //Simulate a movement
        when(mockedCamera.getPosition()).thenReturn(pos1, pos1, pos2, pos2);

        new InjectedRendererBuilder(panoramaRenderer)
                .withMockedCamera()
                .withSlidingOutOfTextRendering();

        panoramaRenderer.resetTargetPos();
        panoramaRenderer.onRender(0, 0);
        verify(mockedCamera, atLeastOnce()).setPosition(vectorCaptor.capture());
        assertEquals(pos1.lerp(ORIGIN, LERP_FACTOR * 5), vectorCaptor.getValue());

        panoramaRenderer.onRender(0, 0);
        verify(mockedCamera, atLeastOnce()).getPosition();
        verify(mockedCamera, atLeastOnce()).setCameraOrientation(any(Quaternion.class));
        verifyNoMoreInteractions(mockedCamera);

        assertEquals(panoramaRenderer.getIdleRendering(), panoramaRenderer.getCurrentRenderingLogic());
    }

    @Test
    public void rotateObjectRenderingIsCorrect() {
        PanoramaInfoObject mockedObjectToRotate = Mockito.mock(PanoramaInfoObject.class);
        List<Double> refVal1s = Arrays.asList(new Double[]{-1.0, -2.0, -3.0, 180.0 / (4.0 * 15)});
        when(mockedObjectToRotate.getX()).thenReturn(1.0);
        when(mockedObjectToRotate.getY()).thenReturn(2.0);
        when(mockedObjectToRotate.getZ()).thenReturn(3.0);
        inject(panoramaRenderer, mockedObjectToRotate, "objectToRotate");

        panoramaRenderer.rotateDisplayInfoObject(mockedObjectToRotate);

        new InjectedRendererBuilder(panoramaRenderer).withRotateObjectRendering();

        panoramaRenderer.onRender(0, 0);
        verify(mockedObjectToRotate).rotate(doubleCaptor.capture(),
                doubleCaptor.capture(),
                doubleCaptor.capture(),
                doubleCaptor.capture());
        List<Double> vals = doubleCaptor.getAllValues();
        verify(mockedObjectToRotate, atLeastOnce()).setColor(anyInt());
        assertEquals(refVal1s, vals);

        for (int i = 1; i < 15; i++) {
            assertEquals(panoramaRenderer.getRotateObjectRendering(), panoramaRenderer.getCurrentRenderingLogic());
            panoramaRenderer.onRender(0, 0);
        }
        assertEquals(panoramaRenderer.getIdleRendering(), panoramaRenderer.getCurrentRenderingLogic());
    }

    @Test
    public void rotateObjectAndZoomOutRenderingIsCorrectRotateFinishFirst() {
        PanoramaInfoObject mockedObjectToRotate = Mockito.mock(PanoramaInfoObject.class);
        List<Double> refVal1s = Arrays.asList(new Double[]{-1.0, -2.0, -3.0, 180.0 / (4.0 * 15)});
        when(mockedObjectToRotate.getX()).thenReturn(1.0);
        when(mockedObjectToRotate.getY()).thenReturn(2.0);
        when(mockedObjectToRotate.getZ()).thenReturn(3.0);

        Vector3 pos1 = new Vector3(DISTANCE_TO_DISPLAY, 0, 0);
        Vector3 pos2 = new Vector3(0, 0, 0);
        //Simulate a movement
        Vector3[] pos3 = new Vector3[31];
        for (int i = 0; i < pos3.length - 2; i++) {
            pos3[i] = pos1;
        }
        pos3[pos3.length - 2] = pos2;
        pos3[pos3.length - 1] = pos2;

        when(mockedCamera.getPosition()).thenReturn(pos1, pos3);

        inject(panoramaRenderer, mockedObjectToRotate, "objectToRotate");

        panoramaRenderer.zoomOutAndRotate(0, mockedObjectToRotate);
        new InjectedRendererBuilder(panoramaRenderer).withMockedCamera().withRotateObjectAndZoomOutRendering();

        panoramaRenderer.resetTargetPos();
        panoramaRenderer.onRender(0, 0);
        verify(mockedCamera, atLeastOnce()).setPosition(vectorCaptor.capture());

        assertEquals(pos1.lerp(ORIGIN, LERP_FACTOR * 5), vectorCaptor.getValue());
        verify(mockedObjectToRotate).rotate(doubleCaptor.capture(),
                doubleCaptor.capture(),
                doubleCaptor.capture(),
                doubleCaptor.capture());
        List<Double> vals = doubleCaptor.getAllValues();
        verify(mockedObjectToRotate, atLeastOnce()).setColor(anyInt());
        assertEquals(refVal1s, vals);

        for (int i = 1; i < 15; i++) {
            assertEquals(panoramaRenderer.getRotateObjectAndZoomOutRendering(), panoramaRenderer
                    .getCurrentRenderingLogic());
            panoramaRenderer.onRender(0, 0);
        }
        assertEquals(panoramaRenderer.getSlidingOutOfTextRendering(), panoramaRenderer.getCurrentRenderingLogic());

        panoramaRenderer.onRender(0, 0);
        verify(mockedCamera, atLeastOnce()).getPosition();
        verify(mockedCamera, atLeastOnce()).setCameraOrientation(any(Quaternion.class));

        assertEquals(panoramaRenderer.getIdleRendering(), panoramaRenderer.getCurrentRenderingLogic());

    }

    @Test
    public void rotateObjectAndZoomOutRenderingIsCorrectZoomFinishFirst() {
        PanoramaInfoObject mockedObjectToRotate = Mockito.mock(PanoramaInfoObject.class);
        List<Double> refVal1s = Arrays.asList(new Double[]{-1.0, -2.0, -3.0, 180.0 / (4.0 * 15)});
        when(mockedObjectToRotate.getX()).thenReturn(1.0);
        when(mockedObjectToRotate.getY()).thenReturn(2.0);
        when(mockedObjectToRotate.getZ()).thenReturn(3.0);

        Vector3 pos1 = new Vector3(DISTANCE_TO_DISPLAY, 0, 0);
        Vector3 pos2 = new Vector3(0, 0, 0);
        //Simulate a movement

        when(mockedCamera.getPosition()).thenReturn(pos1, pos1, pos2, pos2);

        inject(panoramaRenderer, mockedObjectToRotate, "objectToRotate");

        panoramaRenderer.zoomOutAndRotate(0, mockedObjectToRotate);
        new InjectedRendererBuilder(panoramaRenderer).withMockedCamera().withRotateObjectAndZoomOutRendering();

        panoramaRenderer.resetTargetPos();
        panoramaRenderer.onRender(0, 0);
        verify(mockedCamera, atLeastOnce()).setPosition(vectorCaptor.capture());

        assertEquals(pos1.lerp(ORIGIN, LERP_FACTOR * 5), vectorCaptor.getValue());
        verify(mockedObjectToRotate).rotate(doubleCaptor.capture(),
                doubleCaptor.capture(),
                doubleCaptor.capture(),
                doubleCaptor.capture());
        List<Double> vals = doubleCaptor.getAllValues();
        verify(mockedObjectToRotate, atLeastOnce()).setColor(anyInt());
        assertEquals(refVal1s, vals);
        assertEquals(panoramaRenderer.getRotateObjectAndZoomOutRendering(), panoramaRenderer.getCurrentRenderingLogic());

        panoramaRenderer.onRender(0, 0);
        verify(mockedCamera, atLeastOnce()).getPosition();
        verify(mockedCamera, atLeastOnce()).setCameraOrientation(any(Quaternion.class));
        assertEquals(panoramaRenderer.getRotateObjectRendering(), panoramaRenderer.getCurrentRenderingLogic());

        for (int i = 1; i < 14; i++) {
            assertEquals(panoramaRenderer.getRotateObjectRendering(), panoramaRenderer
                    .getCurrentRenderingLogic());
            panoramaRenderer.onRender(0, 0);
        }
        assertEquals(panoramaRenderer.getIdleRendering(), panoramaRenderer.getCurrentRenderingLogic());
    }

    @Test
    public void testFetchPhotoTask() {
        ImageMgmt mockedImageManager = Mockito.mock(ImageMgmt.class);

        inject(panoramaRenderer, mockedImageManager, "mImageManager");

        //Verify behavior for success
        panoramaRenderer.initiatePanoramaTransition("dummyUrl", 987);
        verify(mockedImageManager, atLeastOnce())
                .getBitmapFromUrl(any(Context.class), anyString(), picassoTargetCaptor.capture());

        Target picassoTarget = picassoTargetCaptor.getValue();
        picassoTarget.onBitmapLoaded(mockedBitmap, Picasso.LoadedFrom.DISK);

        Tuple<Integer, Bitmap> t = PanoramaRenderer.NextPanoramaDataBuilder.build();
        assertEquals(Integer.valueOf(987), t.getX());
        assertEquals(mockedBitmap, t.getY());

        //Verify behavior for failure
        panoramaRenderer.initiatePanoramaTransition("dummyUrl", 987);
        verify(mockedImageManager, atLeastOnce())
                .getBitmapFromUrl(any(Context.class), anyString(), picassoTargetCaptor.capture());
        picassoTarget = picassoTargetCaptor.getValue();
        Drawable mockedDrawable = Mockito.mock(Drawable.class);

        assertFalse(PanoramaRenderer.NextPanoramaDataBuilder.isReset());
        picassoTarget.onBitmapFailed(mockedDrawable);
        assertTrue(PanoramaRenderer.NextPanoramaDataBuilder.isReset());
    }

    @Test
    public void unusedMethodsHaveNoSideEffect() {
        panoramaRenderer.onNoObjectPicked();
        panoramaRenderer.onOffsetsChanged(0, 0, 0, 0, 0, 0);

        MotionEvent mockedEvent = Mockito.mock(MotionEvent.class);
        panoramaRenderer.onTouchEvent(mockedEvent);

        verifyNoMoreInteractions(mockedEvent);
        assertNoSideEffects();
    }

    private void assertNoSideEffects() {
        verifyNoMoreInteractions(mockedHouseManager);
        verifyNoMoreInteractions(mockedSensor);
        verifyNoMoreInteractions(mockedSphere);
        verifyNoMoreInteractions(mockedCamera);
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

    private void setupPanoramaBuilder() {
        PanoramaRenderer.NextPanoramaDataBuilder.setNextPanoId(1);
        PanoramaRenderer.NextPanoramaDataBuilder.setNextPanoBitmap(mockedBitmap);
    }

    /**
     * Rotates the camera of a given angle and return a reference quaternion
     *
     * @param angleChange
     * @return
     */
    private Quaternion modifyUserRot(double angleChange) {
        //Rotate the camera counter clockwise to simulate a swipe to the right
        Quaternion newRot = panoramaRenderer.getUserRotation().
                multiplyLeft(new Quaternion().fromAngleAxis(Vector3.Axis.Y, -angleChange));
        /*
             formula
             angle = (Math.cos(yaw) * xComp) + (Math.sin(yaw) * yComp);
         */
        float dx = angleToPixelDelta(angleChange / Math.cos(panoramaRenderer.getDeviceYaw()), true);
        panoramaRenderer.updateCameraRotation(dx, 0);
        return newRot;
    }


    private class InjectedRendererBuilder {

        private PanoramaRenderer pr;

        public InjectedRendererBuilder(PanoramaRenderer pr) {
            this.pr = pr;
        }

        public InjectedRendererBuilder withSlidingRendering() {
            inject(pr, pr.getSlidingRendering(), "mRenderLogic");
            return this;
        }

        public InjectedRendererBuilder withTransitioningRendering() {
            inject(pr, pr.getTransitioningRendering(), "mRenderLogic");
            return this;
        }

        public InjectedRendererBuilder withSlidingToTextRendering() {
            inject(pr, pr.getSlidingToTextRendering(), "mRenderLogic");
            return this;
        }

        public InjectedRendererBuilder withSlidingOutOfTextRendering() {
            inject(pr, pr.getSlidingOutOfTextRendering(), "mRenderLogic");
            return this;
        }

        public InjectedRendererBuilder withRotateObjectRendering() {
            inject(pr, pr.getRotateObjectRendering(), "mRenderLogic");
            return this;
        }

        public InjectedRendererBuilder withRotateObjectAndZoomOutRendering() {
            inject(pr, pr.getRotateObjectAndZoomOutRendering(), "mRenderLogic");
            return this;
        }

        public InjectedRendererBuilder withMockedPanoSphere() {
            inject(pr, mockedSphere, "mPanoSphere");
            return this;
        }

        public InjectedRendererBuilder withMockedCamera() {
            inject(pr, mockedCamera, "mCamera");
            return this;
        }
    }
}
