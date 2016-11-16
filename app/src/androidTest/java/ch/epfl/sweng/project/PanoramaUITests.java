package ch.epfl.sweng.project;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class PanoramaUITests {

    @Rule
    public ActivityTestRule<PanoramaActivity> mActivityRule;

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
        PanoramaRenderer renderer = mActivityRule.getActivity().getAssociatedRenderer();

        /**
         * TEST PANORAMA SPHERE
         */

        //ADDING CHILDREN
        ObjectColorPicker cp = new ObjectColorPicker(renderer);
        PanoramaSphere panoSphere = renderer.getPanoramaSphere();
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


        /**
         * PANORAMA TRANSITION OBJECT
         */
        Integer testId = 4;
        String testUrl = "https://360.astutus.org/estate/1/photoMaisonBacu4.jpg";
        PanoramaObject panoTransition = new PanoramaTransitionObject(0, 0, testId, testUrl);

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
        panoTransition.reactWith(renderer);
        assertFalse(PanoramaRenderer.NextPanoramaDataBuilder.isReset());

    }
}
