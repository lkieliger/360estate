package ch.epfl.sweng.project;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import ch.epfl.sweng.project.data.parse.ParseProxy;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)

public class ParseProxyTests {

    private final ParseProxy proxy = ParseProxy.PROXY;

    @Test
    public void testInternetAvailable() {

        assertTrue(proxy.internetAvailable());
        proxy.notifyInternetProblem();

        assertFalse(proxy.internetAvailable());
    }
}
