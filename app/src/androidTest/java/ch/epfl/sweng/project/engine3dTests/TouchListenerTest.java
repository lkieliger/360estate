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
import static ch.epfl.sweng.project.util.TestUtilityFunctions.wait1s;
import static ch.epfl.sweng.project.util.TestUtilityFunctions.wait250ms;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class TouchListenerTest {

    private static final String TAG = "TouchListener test";
    @Rule
    public ActivityTestRule<PanoramaActivity> mActivityTestRule = new ActivityTestRule<>(PanoramaActivity.class);

    private PanoramaRenderer renderer = null;
    private View view = null;

    @Before
    public void initMembers() {
        renderer = new PanoramaRenderer(
                mActivityTestRule.getActivity().getApplicationContext(),
                mActivityTestRule.getActivity().getWindowManager().getDefaultDisplay());
        wait1s(TAG);
        
        renderer.setSceneCachingEnabled(false);
        wait250ms(TAG);
        view = new View(mActivityTestRule.getActivity().getApplicationContext());
    }

    private MotionEvent genEvent(int action) {
        return MotionEvent.obtain(0, 0, action, 0, 0, 0);
    }

    @Test
    public void touchListenerTests() {

        // consumes valid input

        View.OnTouchListener touchListener = new PanoramaTouchListener(renderer);
        wait250ms(TAG);

        assertTrue(touchListener.onTouch(view, genEvent(ACTION_DOWN)));
        assertTrue(touchListener.onTouch(view, genEvent(ACTION_UP)));
        assertTrue(touchListener.onTouch(view, genEvent(ACTION_MOVE)));
        assertTrue(touchListener.onTouch(view, genEvent(ACTION_CANCEL)));
        assertTrue(touchListener.onTouch(view, genEvent(ACTION_POINTER_UP)));

        // dontConsumeInvalidInput test

        assertFalse(touchListener.onTouch(view, genEvent(ACTION_HOVER_ENTER)));
        assertFalse(touchListener.onTouch(view, genEvent(ACTION_HOVER_EXIT)));
        assertFalse(touchListener.onTouch(view, genEvent(ACTION_HOVER_MOVE)));
        assertFalse(touchListener.onTouch(view, genEvent(ACTION_OUTSIDE)));
        assertFalse(touchListener.onTouch(view, genEvent(ACTION_POINTER_DOWN)));
    }


}
