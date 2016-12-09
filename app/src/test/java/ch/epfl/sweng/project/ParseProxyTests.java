package ch.epfl.sweng.project;


import android.content.Context;
import android.net.wifi.WifiManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import ch.epfl.sweng.project.data.parse.ParseProxy;
import ch.epfl.sweng.project.features.propertylist.ListActivity;
import ch.epfl.sweng.project.userSupport.activities.LoginActivity;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)

public class ParseProxyTests {

    private final ParseProxy proxy = ParseProxy.PROXY;

    @Test
    public void testInternetAvailable() {

        boolean isInternet = proxy.internetAvailable();

        assertTrue(isInternet);
    }


}
