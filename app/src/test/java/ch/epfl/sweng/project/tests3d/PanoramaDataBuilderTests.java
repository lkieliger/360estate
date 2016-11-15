package ch.epfl.sweng.project.tests3d;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.engine3d.PanoramaRenderer.NextPanoramaDataBuilder;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class PanoramaDataBuilderTests {

    private Bitmap b;

    @Before
    public void cleanDataBuilder() {
        NextPanoramaDataBuilder.resetData();
        b = BitmapFactory.decodeResource(
                RuntimeEnvironment.application.getResources(),
                R.drawable.transition_tex
        );
    }

    @Test
    public void isReadyIsCorrect() {

        assertFalse(NextPanoramaDataBuilder.isReady());

        NextPanoramaDataBuilder.setNextPanoId(1);
        assertFalse(NextPanoramaDataBuilder.isReady());

        NextPanoramaDataBuilder.setNextPanoBitmap(b);
        assertTrue(NextPanoramaDataBuilder.isReady());

        NextPanoramaDataBuilder.build();
        assertFalse(NextPanoramaDataBuilder.isReady());
    }

    @Test(expected = IllegalStateException.class)
    public void buildAfterResetThrowsException() {
        NextPanoramaDataBuilder.build();
    }

    @Test(expected = IllegalStateException.class)
    public void buildAfterIdSetOnlyThrowsException() {
        NextPanoramaDataBuilder.setNextPanoId(1);
        NextPanoramaDataBuilder.build();
    }

    @Test(expected = IllegalStateException.class)
    public void buildAfterBitmapSetOnlyThrowsException() {
        NextPanoramaDataBuilder.setNextPanoBitmap(b);
        NextPanoramaDataBuilder.build();
    }

    @Test(expected = IllegalStateException.class)
    public void illegalPanoSetThrowsException() {
        NextPanoramaDataBuilder.setNextPanoBitmap(b);
        NextPanoramaDataBuilder.setNextPanoBitmap(b);
    }

    @Test(expected = IllegalStateException.class)
    public void illegalIdSetThrowsExcpetion() {

        NextPanoramaDataBuilder.setNextPanoId(1);
        NextPanoramaDataBuilder.setNextPanoId(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullIdThrowsException() {
        NextPanoramaDataBuilder.setNextPanoId(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullBitmapThrowsException() {
        NextPanoramaDataBuilder.setNextPanoBitmap(null);
    }
}
