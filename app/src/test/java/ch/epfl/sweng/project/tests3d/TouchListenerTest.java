package ch.epfl.sweng.project.tests3d;

import android.view.MotionEvent;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.engine3d.PanoramaRenderer;
import ch.epfl.sweng.project.engine3d.listeners.PanoramaTouchListener;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_HOVER_ENTER;
import static android.view.MotionEvent.ACTION_HOVER_EXIT;
import static android.view.MotionEvent.ACTION_HOVER_MOVE;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_OUTSIDE;
import static android.view.MotionEvent.ACTION_POINTER_DOWN;
import static android.view.MotionEvent.ACTION_POINTER_UP;
import static android.view.MotionEvent.ACTION_UP;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class TouchListenerTest {

    private static final float errorEpsilon = 0.001f;
    @Mock
    View view;
    @Mock
    PanoramaRenderer mockedRenderer;
    @Captor
    ArgumentCaptor<Float> floatCaptor1;
    @Captor
    ArgumentCaptor<Float> floatCaptor2;
    private PanoramaTouchListener panoramaTouchListener;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        panoramaTouchListener = new PanoramaTouchListener(mockedRenderer);
    }

    @Test
    public void consumesValidInput() {

        assertTrue(panoramaTouchListener.onTouch(view, genBasicEvent(ACTION_DOWN)));
        assertTrue(panoramaTouchListener.onTouch(view, genBasicEvent(ACTION_UP)));
        assertTrue(panoramaTouchListener.onTouch(view, genBasicEvent(ACTION_MOVE)));
        assertTrue(panoramaTouchListener.onTouch(view, genBasicEvent(ACTION_CANCEL)));
        assertTrue(panoramaTouchListener.onTouch(view, genBasicEvent(ACTION_POINTER_UP)));
        assertTrue(panoramaTouchListener.onTouch(view, genBasicEvent(ACTION_POINTER_DOWN)));
    }

    @Test
    public void doesNotConsumeInvalidInput() {

        assertFalse(panoramaTouchListener.onTouch(view, genBasicEvent(ACTION_HOVER_ENTER)));
        assertFalse(panoramaTouchListener.onTouch(view, genBasicEvent(ACTION_HOVER_EXIT)));
        assertFalse(panoramaTouchListener.onTouch(view, genBasicEvent(ACTION_HOVER_MOVE)));
        assertFalse(panoramaTouchListener.onTouch(view, genBasicEvent(ACTION_OUTSIDE)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullParameterThrowsException() {
        new PanoramaTouchListener(null);
    }

    @Test
    public void handleClick() {

        assertTrue(panoramaTouchListener.onTouch(view,
                genMockedEvent(ACTION_DOWN, 0, 0, 123, 123)));
        assertTrue(panoramaTouchListener.onTouch(view,
                genMockedEvent(ACTION_UP, 0, 0, 123, 124)));
        verify(mockedRenderer).getObjectAt(floatCaptor1.capture(), floatCaptor2.capture());
        assertEquals(123, floatCaptor1.getValue(), errorEpsilon);
        assertEquals(124, floatCaptor2.getValue(), errorEpsilon);
    }

    @Test
    public void discardInvalidClick() {
        panoramaTouchListener.onTouch(view, genMockedEvent(ACTION_DOWN, 0, 0, 0, 0));
        generateValidPos();

        //Clicks
        generateClickWithDrift(-10, -10);
        generateClickWithDrift(-10, 0);
        generateClickWithDrift(0, -10);
        generateClickWithDrift(0, -7);
        generateClickWithDrift(0, 0);
        generateClickWithDrift(0, 1);
        generateClickWithDrift(10, 1);
        generateClickWithDrift(10, 10);

        //Not clicks
        generateClickWithDrift(11, 11);
        generateClickWithDrift(11, 0);
        generateClickWithDrift(0, 11);
        generateClickWithDrift(-11, -11);
        generateClickWithDrift(-11, 0);
        generateClickWithDrift(-11, 0);
        generateClickWithDrift(0, -11);
        generateClickWithDrift(150, -131);

        //Simply UP without DOWN first should be discarded
        panoramaTouchListener.onTouch(view, genMockedEvent(ACTION_UP, 0, 0, 0, 0));

        //Only clicks with less or equal amplitude than scroll threshold should be valid
        verify(mockedRenderer, times(8)).getObjectAt(anyFloat(), anyFloat());
        verify(view, times(17)).performClick();

    }

    @Test
    public void handleInvalidMove() {
        generateValidPos();
        panoramaTouchListener.onTouch(view, genMockedEvent(ACTION_MOVE, 1, 1, 10, 10));
        verify(mockedRenderer, never()).updateCameraRotation(anyFloat(), anyFloat());
    }

    @Test
    public void handleValidMoves() {

        generateValidPos();
        panoramaTouchListener.onTouch(view, genMockedEvent(ACTION_MOVE, 0, 0, 18, 16));

        for (int i = 10, j = 10; i < 100; i += 1, j += 2) {
            float x = 2 * i;
            float y = 2 * j;
            panoramaTouchListener.onTouch(view, genMockedEvent(ACTION_MOVE, 0, 0, x, y));
            verify(mockedRenderer, atLeastOnce()).updateCameraRotation(
                    floatCaptor1.capture(), floatCaptor2.capture());
            assertEquals(x - (2 * (i - 1)), floatCaptor1.getValue());
            assertEquals(y - (2 * (j - 2)), floatCaptor2.getValue());
        }
    }

    @Test
    public void handleMultiplePointers() {
        generateValidPos();

        /*
        If the active pointer is UP then the last pos should be invalidated and next
        event with move shouldn't affect camera
         */

        panoramaTouchListener.onTouch(view, genMockedEvent(ACTION_DOWN, 0, 0, 0, 0));
        panoramaTouchListener.onTouch(view, genMockedEvent(ACTION_POINTER_DOWN, 1, 1, 0, 0));
        generateValidPos();
        panoramaTouchListener.onTouch(view, genMockedEvent(ACTION_POINTER_UP, 0, 0, 0, 0));

        //Move should be discared as active pointer is now 1 and not 0
        panoramaTouchListener.onTouch(view, genMockedEvent(ACTION_MOVE, 0, 0, 100, 100));
        verify(mockedRenderer, never()).updateCameraRotation(anyFloat(), anyFloat());
        //Move doesn't yet affect camera
        panoramaTouchListener.onTouch(view, genMockedEvent(ACTION_MOVE, 0, 1, 100, 100));
        verify(mockedRenderer, never()).updateCameraRotation(anyFloat(), anyFloat());
        //Move taken into account
        panoramaTouchListener.onTouch(view, genMockedEvent(ACTION_MOVE, 0, 1, 200, 200));
        verify(mockedRenderer, times(1)).updateCameraRotation(anyFloat(), anyFloat());
    }

    private MotionEvent genBasicEvent(int action) {
        return MotionEvent.obtain(0, 0, action, 111, 111, 0);
    }

    private MotionEvent genMockedEvent(int action, int pId, int pIndex, float x, float y) {
        MotionEvent mockedEvent = Mockito.mock(MotionEvent.class);
        when(mockedEvent.getActionMasked()).thenReturn(action);
        when(mockedEvent.getActionIndex()).thenReturn(pIndex);
        when(mockedEvent.getPointerId(0)).thenReturn(0);
        when(mockedEvent.getPointerId(1)).thenReturn(1);
        when(mockedEvent.getX(pIndex)).thenReturn(x);
        when(mockedEvent.getX()).thenReturn(x);
        when(mockedEvent.getY(pIndex)).thenReturn(y);
        when(mockedEvent.getY()).thenReturn(y);


        return mockedEvent;
    }

    private void generateValidPos() {
        panoramaTouchListener.onTouch(view, genMockedEvent(ACTION_MOVE, 0, 0, 0, 0));
    }

    private void generateClickWithDrift(float driftX, float driftY) {
        panoramaTouchListener.onTouch(view, genMockedEvent(ACTION_DOWN, 0, 0, 0, 0));
        panoramaTouchListener.onTouch(view, genMockedEvent(ACTION_MOVE, 0, 0, driftX, driftY));
        panoramaTouchListener.onTouch(view, genMockedEvent(ACTION_UP, 0, 0, driftX, driftY));
    }
}
