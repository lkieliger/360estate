package ch.epfl.sweng.project.util;

import android.content.Context;
import android.util.Log;

import com.parse.Parse;

import ch.epfl.sweng.project.BuildConfig;

public final class TestUtilityFunctions {

    private static final String APP_ID = "360ESTATE";

    private TestUtilityFunctions() {
    }

    public static void initializeParse(Context context) {
        Parse.initialize(new Parse.Configuration.Builder(context)
                // The network interceptor is used to debug the communication between server/client
                //.addNetworkInterceptor(new ParseLogInterceptor())
                .applicationId(APP_ID)
                .server("https://360.astutus.org/parse/")
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

    public static void waitNms(String debugTag, long millis) {
        try {
            Thread.sleep(millis);
            if (BuildConfig.DEBUG) {
                Log.d(debugTag, "Thread slept for " + millis + " ms");
            }
        } catch (InterruptedException e) {
            if (BuildConfig.DEBUG) {
                Log.d(debugTag, "InterruptedException" + e.getMessage());
            }
        }
    }


}
