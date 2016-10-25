package ch.epfl.sweng.project.tests3d;

import android.view.Display;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.rajawali3d.math.Quaternion;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.internal.Shadow;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.engine3d.PanoramaActivity;
import ch.epfl.sweng.project.engine3d.PanoramaRenderer;
import ch.epfl.sweng.project.engine3d.RotSensorListener;
import ch.epfl.sweng.project.user.LoginActivity;

import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)

public class PanoramaTests {

    @SuppressWarnings("ObjectEqualsNull")
    @Test
    public void rotSensorListenerTest() {

        LoginActivity loginActivity = Robolectric.buildActivity(LoginActivity.class).
                create().
                get();

        Display display = Shadow.newInstanceOf(Display.class);

        PanoramaRenderer panoramaRenderer = new PanoramaRenderer(loginActivity.getBaseContext(), display);

        RotSensorListener rotSensorListener = new RotSensorListener(display, panoramaRenderer);

        assertTrue(rotSensorListener.getDummyRotation().equals(new Quaternion()));


    }
}
