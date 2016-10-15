package ch.epfl.sweng.project.engine3dTests;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.MotionEvent;
import android.view.View;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.project.engine3d.PanoramaActivity;
import ch.epfl.sweng.project.engine3d.PanoramaRenderer;
import ch.epfl.sweng.project.engine3d.PanoramaTouchListener;
import ch.epfl.sweng.project.engine3dTests.mockobjects.MockView;

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
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class TouchListenerTest {
//TODO: Factorize this class

    @Rule
    public ActivityTestRule<PanoramaActivity> mActivityTestRule = new ActivityTestRule<>(PanoramaActivity.class);

    @Before
    public void initMembers() {
    }

    private MotionEvent genEvent(int action) {
        return MotionEvent.obtain(0, 0, action, 0, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void touchListenerWithNullParameter() {
        PanoramaTouchListener touchListener = new PanoramaTouchListener(null);
    }

    @Test
    public void consumesValidInput() {
        PanoramaRenderer renderer = new PanoramaRenderer(mActivityTestRule.getActivity().getApplicationContext());
        View view = new MockView(mActivityTestRule.getActivity().getApplicationContext());

        PanoramaTouchListener touchListener = new PanoramaTouchListener(renderer);
        assertTrue(touchListener.onTouch(view, genEvent(ACTION_DOWN)));
        assertTrue(touchListener.onTouch(view, genEvent(ACTION_UP)));
        assertTrue(touchListener.onTouch(view, genEvent(ACTION_MOVE)));
        assertTrue(touchListener.onTouch(view, genEvent(ACTION_CANCEL)));
        assertTrue(touchListener.onTouch(view, genEvent(ACTION_POINTER_UP)));
    }

    @Test
    public void dontConsumeInvalidInput() {
        PanoramaRenderer renderer = new PanoramaRenderer(mActivityTestRule.getActivity().getApplicationContext());
        View view = new MockView(mActivityTestRule.getActivity().getApplicationContext());

        PanoramaTouchListener touchListener = new PanoramaTouchListener(renderer);
        assertFalse(touchListener.onTouch(view, genEvent(ACTION_HOVER_ENTER)));
        assertFalse(touchListener.onTouch(view, genEvent(ACTION_HOVER_EXIT)));
        assertFalse(touchListener.onTouch(view, genEvent(ACTION_HOVER_MOVE)));
        assertFalse(touchListener.onTouch(view, genEvent(ACTION_OUTSIDE)));
        assertFalse(touchListener.onTouch(view, genEvent(ACTION_POINTER_DOWN)));
    }


}