package ch.epfl.sweng.project.util;


import android.content.Context;

import com.parse.Parse;

public final class UnitTestUtilityFunctions {

    private static final String APP_ID = "360ESTATE";

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

    private static void waitNms(String debugTag, long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LogHelper.log(debugTag, "InterruptedException" + e.getMessage());
        }
    }

}
