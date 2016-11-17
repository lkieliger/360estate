package ch.epfl.sweng.project;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.util.ObjectColorPicker;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.project.data.AngleMapping;
import ch.epfl.sweng.project.data.TransitionObject;
import ch.epfl.sweng.project.engine3d.PanoramaActivity;
import ch.epfl.sweng.project.engine3d.PanoramaRenderer;
import ch.epfl.sweng.project.engine3d.components.PanoramaObject;
import ch.epfl.sweng.project.engine3d.components.PanoramaSphere;
import ch.epfl.sweng.project.engine3d.components.PanoramaTransitionObject;
import ch.epfl.sweng.project.util.ParseInitialiser;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class PanoramaUITests {

    private static final Integer TEST_ID = 4;
    private static final String TEST_URL = "https://360.astutus.org/estate/1/photoMaisonBacu4.jpg";
    private static final String TAG = "UnitTest";
    @Rule
    public ActivityTestRule<PanoramaActivity> mActivityRule;
    private PanoramaRenderer mRenderer;

    @Before
    public void initParse() {
        ParseInitialiser.INSTANCE.initParse(InstrumentationRegistry.getInstrumentation()
                .getTargetContext());
    }

    /**
     * The reason why all tests are merged into one method is to mitigate the load of having multiple panorama
     * activities due to the fact that each unit tests create a new instance of the activity
     */
    @Test
    public void fullPanoramaUITest() {

        mActivityRule = new ActivityTestRule<PanoramaActivity>(PanoramaActivity.class);
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation()
                .getTargetContext(), PanoramaActivity.class);
        intent.putExtra("id", "1");
        mActivityRule.launchActivity(intent);
        mRenderer = mActivityRule.getActivity().getAssociatedRenderer();

        testPanoramaSphere();
        testPanoramaTransitionObjects();
        testRenderingLogics();
    }

    private void testPanoramaSphere() {
        ObjectColorPicker cp = new ObjectColorPicker(mRenderer);
        PanoramaSphere panoSphere = mRenderer.getPanoramaSphere();
        List<AngleMapping> l = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            l.add(new TransitionObject((double) i, (double) i, 0, ""));
        }

        assertEquals(0, panoSphere.getNumChildren());
        panoSphere.attachPanoramaComponents(l, cp);
        assertEquals(10, panoSphere.getNumChildren());

        //REMOVING CHILDREN
        panoSphere.detachPanoramaComponents(cp);
        assertEquals(0, panoSphere.getNumChildren());
    }

    private void testPanoramaTransitionObjects() {
        PanoramaObject panoTransition = new PanoramaTransitionObject(0, 0, TEST_ID, TEST_URL);

        //TEST FOR EXCEPTION THROWN BECAUSE LACKS PARENT (LIKE BATMAN)
        boolean threwException = false;
        try {
            panoTransition.detachFromParentAndDie();
        } catch (IllegalStateException e) {
            threwException = true;
        } finally {
            assertTrue(threwException);
        }

        PanoramaRenderer.NextPanoramaDataBuilder.resetData();
        panoTransition.reactWith(mRenderer);
        assertFalse(PanoramaRenderer.NextPanoramaDataBuilder.isReset());
        mRenderer.cancelPanoramaUpdate();
        assertTrue(PanoramaRenderer.NextPanoramaDataBuilder.isReset());
    }

    private void testRenderingLogics() {

        PanoramaRenderer.RenderingLogic renderingLogic = mRenderer.getCurrentRenderingLogic();
        assertSame(renderingLogic, mRenderer.getCurrentRenderingLogic());
        PanoramaObject dummyTransition = new PanoramaTransitionObject(Math.PI / 2.0, 0, TEST_ID, TEST_URL);
        dummyTransition.setPosition(new Vector3(100, 0, 0));
        mRenderer.onObjectPicked(dummyTransition);
        assertNotSame(renderingLogic, mRenderer.getCurrentRenderingLogic());

        Vector3 pos = mRenderer.getCurrentCamera().getPosition();

        Log.i(TAG, mRenderer.getCurrentCamera().getPosition().toString());

        //Compute 60 frames
        for (int i = 0; i < 60; i++) {
            try {
                Thread.sleep(16);
                mRenderer.onRender(0, 16);
                Log.i("Test", mRenderer.getCurrentCamera().getPosition().toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
