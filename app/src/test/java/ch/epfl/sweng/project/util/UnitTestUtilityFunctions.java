package ch.epfl.sweng.project.util;


import android.content.Context;
import android.util.Log;

import com.parse.Parse;

import org.rajawali3d.math.Quaternion;

import java.lang.reflect.Field;

import ch.epfl.sweng.project.BuildConfig;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public final class UnitTestUtilityFunctions {

    private static final String APP_ID = "360ESTATE";

    private static final double ERROR_EPSILON = 0.1;

    private UnitTestUtilityFunctions() {
    }

    public static void initializeParse(Context context) {
        Parse.initialize(new Parse.Configuration.Builder(context)
                // The network interceptor is used to debug the communication between server/client
                //.addNetworkInterceptor(new ParseLogInterceptor())
                .applicationId(APP_ID)
                .server("http://360.astutus.org:1337/parse/")
                .build()
        );
    }

    public static void initializeParseLocal(Context context) {
        Parse.initialize(new Parse.Configuration.Builder(context)
                .applicationId(APP_ID)
                .enableLocalDataStore()
                .build()
        );
    }

    public static void wait250ms(String debugTag) {
        waitNms(debugTag, 250);
    }

    public static void wait500ms(String debugTag) {
        waitNms(debugTag, 500);
    }

    public static void wait1s(String debugTag) {
        waitNms(debugTag, 1000);
    }

    private static void waitNms(String debugTag, long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            if (BuildConfig.DEBUG) {
                Log.d(debugTag, "InterruptedException" + e.getMessage());
            }
        }
    }

    public static void assertQuaternionEquals(Quaternion q1, Quaternion q2) {
        System.out.println("Expected: " + q1);
        System.out.println("Got     : " + q2);

        assertTrue(q1.equals(q2, ERROR_EPSILON));
    }

    public static void assertQuaternionNotEquals(Quaternion q1, Quaternion q2) {
        System.out.println("Expected: " + q1);
        System.out.println("Got     : " + q2);

        assertFalse(q1.equals(q2, ERROR_EPSILON));
    }

    public static <E, S> E inject(E intoObj, S injection, String fieldName) {
        try {
            Field valueField = intoObj.getClass().getDeclaredField(fieldName);
            valueField.setAccessible(true);
            try {
                valueField.set(intoObj, injection);
            } catch (IllegalAccessException e) {
                Log.e("INJECTOR", e.getMessage());
            }
        } catch (NoSuchFieldException e) {
            Log.e("INJECTOR", e.getMessage());
        }

        return intoObj;
    }

}
