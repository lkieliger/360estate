package ch.epfl.sweng.project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.util.ObjectColorPicker;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.project.data.panorama.adapters.SpatialData;
import ch.epfl.sweng.project.data.panorama.adapters.TransitionObject;
import ch.epfl.sweng.project.data.parse.ParseInitialiser;
import ch.epfl.sweng.project.engine3d.PanoramaActivity;
import ch.epfl.sweng.project.engine3d.PanoramaRenderer;
import ch.epfl.sweng.project.engine3d.components.PanoramaObject;
import ch.epfl.sweng.project.engine3d.components.PanoramaSphere;
import ch.epfl.sweng.project.engine3d.components.PanoramaTransitionObject;

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
    private static final int FRAME_TIME_MILLIS = 16;
    @Rule
    public ActivityTestRule<PanoramaActivity> mActivityRule;
    private long elapsedTime = 0;
    private PanoramaRenderer mRenderer;

    @Before
    public void initParse() {
        ParseInitialiser.INSTANCE.initParse(InstrumentationRegistry.getInstrumentation()
                .getTargetContext());
    }

    @After
    public void closeTests() {
        sleepDuring(2000);
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

        sleepDuring(1500);

        testPanoramaSphere();
        panoramaTransitionObjectsThrowsException();
        testRenderingLogics();
        testFetchPhotoTask(TEST_URL);
        testFetchPhotoTask("idontexpectthistobeavalidurl");
    }

    private void testPanoramaSphere() {
        ObjectColorPicker cp = new ObjectColorPicker(mRenderer);
        PanoramaSphere panoSphere = mRenderer.getPanoramaSphere();
        List<SpatialData> l = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            l.add(new TransitionObject((double) i, (double) i, 0, ""));
        }

        panoSphere.detachPanoramaComponents(cp);
        assertEquals(0, panoSphere.getNumChildren());
        panoSphere.attachPanoramaComponents(l, cp);
        assertEquals(10, panoSphere.getNumChildren());
    }

    private void panoramaTransitionObjectsThrowsException() {
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
    }

    private void testRenderingLogics() {
        //Allows the renderer to settle
        sleepDuring(1500);

        //Compute 120 frames in idle
        for (int i = 0; i < 120; i++) {
            computeFrame();
        }

        PanoramaObject dummyTransition = new PanoramaTransitionObject(0, 0, TEST_ID, TEST_URL);
        dummyTransition.setPosition(new Vector3(100, 0, 0));

        //Check if goes out from idle mode
        PanoramaRenderer.RenderingLogic renderingLogic = mRenderer.getCurrentRenderingLogic();
        PanoramaRenderer.RenderingLogic initialRenderingLogic = renderingLogic;

        assertSame(renderingLogic, mRenderer.getCurrentRenderingLogic());
        mRenderer.onObjectPicked(dummyTransition);
        mRenderer.cancelPanoramaUpdate();
        assertNotSame(renderingLogic, mRenderer.getCurrentRenderingLogic());

        //Compute 60 frames in transition
        for (int i = 0; i < 60; i++) {
            computeFrame();
            Log.i(TAG, mRenderer.getCurrentCamera().getPosition().toString());
        }

        //Check if camera moved the expected amout
        assertTrue(mRenderer.getCurrentCamera().getPosition().length() > PanoramaRenderer.CAM_TRAVEL_DISTANCE);
        assertTrue(mRenderer.getCurrentCamera().getPosition().length() < PanoramaRenderer.CAM_TRAVEL_DISTANCE + 10);

        //Prepare for panorama transition
        Bitmap b = BitmapFactory.decodeResource(mActivityRule.getActivity().getResources(), R.drawable.panotest);

        assertFalse(PanoramaRenderer.NextPanoramaDataBuilder.isReady());
        PanoramaRenderer.NextPanoramaDataBuilder.setNextPanoBitmap(b);
        PanoramaRenderer.NextPanoramaDataBuilder.setNextPanoId(TEST_ID);
        assertTrue(PanoramaRenderer.NextPanoramaDataBuilder.isReady());

        renderingLogic = mRenderer.getCurrentRenderingLogic();
        assertSame(renderingLogic, mRenderer.getCurrentRenderingLogic());
        mRenderer.onRender(1016, 16); //SLIDING -> TRANSITIONING
        mRenderer.onRender(1032, 16); //TRANSITIONING: RESET CAM -> IDLE
        assertNotSame(renderingLogic, mRenderer.getCurrentRenderingLogic());

        //Camera should be at origin again
        assertEquals(PanoramaRenderer.ORIGIN, mRenderer.getCurrentCamera().getPosition());
        assertTrue(PanoramaRenderer.NextPanoramaDataBuilder.isReset());

        //Rendering should be at idle again
        mRenderer.onRender(1048, 16);
        assertSame(initialRenderingLogic, mRenderer.getCurrentRenderingLogic());
    }

    private void testFetchPhotoTask(String url) {
        sleepDuring(1000);
        int timeout = 100;
        PanoramaRenderer.NextPanoramaDataBuilder.resetData();
        mRenderer.initiatePanoramaTransition(url, TEST_ID);

        while (!PanoramaRenderer.NextPanoramaDataBuilder.isReset() && timeout > 0) {
            Log.d(TAG, "Waiting 100ms for fetch image task to be completed");
            timeout -= 1;
            sleepDuring(100);
        }
        assertTrue(timeout > 0);
    }

    private void computeFrame() {
        elapsedTime += FRAME_TIME_MILLIS;
        sleepDuring(FRAME_TIME_MILLIS);
        mRenderer.onRender(elapsedTime, FRAME_TIME_MILLIS);
    }

    private void sleepDuring(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
