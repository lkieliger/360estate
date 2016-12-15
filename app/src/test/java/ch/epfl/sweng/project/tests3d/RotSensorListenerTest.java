package ch.epfl.sweng.project.tests3d;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;
import android.view.Surface;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.engine3d.PanoramaRenderer;
import ch.epfl.sweng.project.engine3d.listeners.RotSensorListener;

import static ch.epfl.sweng.project.util.DoubleArrayConverter.doubleToFloatArray;
import static ch.epfl.sweng.project.util.UnitTestUtilityFunctions.assertQuaternionEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class RotSensorListenerTest {

    private static final String TAG = "RotSensorListenerTest";
    @Mock
    PanoramaRenderer mockedRenderer;
    @Captor
    ArgumentCaptor<Quaternion> argumentCaptor;

    @Mock
    Sensor mockedSensor;
    @InjectMocks
    SensorEvent mockedEvent;

    RotSensorListener rotSensorListener;

    private float[] values;
    private Quaternion q1;
    private Quaternion q2;
    private Quaternion q3;
    private Quaternion q4;
    private Quaternion q5;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);

        Quaternion rot90AlongX = new Quaternion().fromAngleAxis(Vector3.Axis.X, 90);
        Quaternion rot90AlongZ = new Quaternion().fromAngleAxis(Vector3.Axis.Z, 90);
        q1 = new Quaternion();
        q2 = new Quaternion(q1).multiplyLeft(rot90AlongX);
        q3 = new Quaternion(q2).multiplyLeft(rot90AlongZ);
        q4 = new Quaternion(q3).multiplyLeft(rot90AlongZ);
        q5 = new Quaternion(q4).multiplyLeft(rot90AlongZ);
        values = doubleToFloatArray(new double[]{q1.x, q1.y, q1.z, -q1.w});

        float[] v = {0.1f, 0.2f, 0.3f, 0.4f};

        try {
            Field valuesField = SensorEvent.class.getField("values");
            valuesField.setAccessible(true);
            try {
                valuesField.set(mockedEvent, v);
            } catch (IllegalAccessException e) {
                Log.d(TAG, e.getMessage());
            }
        } catch (NoSuchFieldException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Test
    public void onSensorChangedIsCorrect() {

        when(mockedSensor.getType()).thenReturn(Sensor.TYPE_GAME_ROTATION_VECTOR, Sensor.TYPE_GYROSCOPE, Sensor
                .TYPE_ACCELEROMETER, Sensor.TYPE_ROTATION_VECTOR, Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);

        rotSensorListener = new RotSensorListener(Surface.ROTATION_0, mockedRenderer);
        rotSensorListener.onSensorChanged(mockedEvent);
        rotSensorListener.onSensorChanged(mockedEvent);
        rotSensorListener.onSensorChanged(mockedEvent);
        rotSensorListener.onSensorChanged(mockedEvent);
        rotSensorListener.onSensorChanged(mockedEvent);
        verify(mockedRenderer, times(1)).setDeviceYaw(anyDouble());
        verify(mockedRenderer, times(1)).setSensorRotation(any(Quaternion.class));
    }

    @Test
    public void onAccuracyChangedIsCorrect() {

        rotSensorListener = new RotSensorListener(Surface.ROTATION_0, mockedRenderer);

        rotSensorListener.onAccuracyChanged(mockedSensor, 0);
        rotSensorListener.onAccuracyChanged(mockedSensor, 1);
        rotSensorListener.onAccuracyChanged(mockedSensor, 100);

        verifyNoMoreInteractions(mockedSensor);
        verifyNoMoreInteractions(mockedRenderer);
    }

    @Test
    public void coordMappingIsCorrectForPortrait() {

        rotSensorListener = new RotSensorListener(Surface.ROTATION_0, mockedRenderer);
        rotSensorListener.sensorChanged(values);

        verify(mockedRenderer).setSensorRotation(argumentCaptor.capture());
        assertQuaternionEquals(q2, argumentCaptor.getValue());
    }

    @Test
    public void coordMappingIsCorrectForLandscape() {

        rotSensorListener = new RotSensorListener(Surface.ROTATION_90, mockedRenderer);
        rotSensorListener.sensorChanged(values);

        verify(mockedRenderer).setSensorRotation(argumentCaptor.capture());
        assertQuaternionEquals(q3, argumentCaptor.getValue());
    }

    @Test
    public void coordMappingIsCorrectForInversePortrait() {

        rotSensorListener = new RotSensorListener(Surface.ROTATION_180, mockedRenderer);
        rotSensorListener.sensorChanged(values);

        verify(mockedRenderer).setSensorRotation(argumentCaptor.capture());
        assertQuaternionEquals(q4, argumentCaptor.getValue());

    }

    @Test
    public void coordMappingIsCorrectForInverseLandscape() {

        rotSensorListener = new RotSensorListener(Surface.ROTATION_270, mockedRenderer);
        rotSensorListener.sensorChanged(values);

        verify(mockedRenderer).setSensorRotation(argumentCaptor.capture());
        assertQuaternionEquals(q5, argumentCaptor.getValue());
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

        rotSensorListener = new RotSensorListener(i, mockedRenderer);
    }
}
