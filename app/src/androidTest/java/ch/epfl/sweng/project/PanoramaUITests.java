package ch.epfl.sweng.project;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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

import static ch.epfl.sweng.project.util.TestUtilityFunctions.wait1s;
import static junit.framework.Assert.assertEquals;
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
        mActivityRule.getActivity().getAssociatedRenderer().onPause();
        mActivityRule.getActivity().finish();
        wait1s(TAG);
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
        panoramaTransitionObjectsThrowsException();
    }

    private void testPanoramaSphere() {
        PanoramaSphere panoSphere = mRenderer.getPanoramaSphere();
        List<SpatialData> l = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            l.add(new TransitionObject((double) i, (double) i, 0, ""));
        }

        panoSphere.detachPanoramaComponents();
        assertEquals(0, panoSphere.getNumChildren());
        panoSphere.attachPanoramaComponents(l);
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

}
