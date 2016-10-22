package ch.epfl.sweng.project.data.util;


import android.content.Context;
import android.util.Log;

import com.parse.Parse;

import ch.epfl.sweng.project.BuildConfig;

public final class TestUtilityFunctions {

    private static final String APP_ID = "360ESTATE";

    private TestUtilityFunctions(){
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

    public static void waitNms(String debugTag, long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            if (BuildConfig.DEBUG) {
                Log.d(debugTag, "InterruptedException" + e.getMessage());
            }
        }
    }

}
