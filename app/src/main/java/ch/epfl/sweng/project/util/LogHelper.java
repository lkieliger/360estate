package ch.epfl.sweng.project.util;

import android.util.Log;

import ch.epfl.sweng.project.BuildConfig;

public final class LogHelper {

    private LogHelper() {
    }

    public static void log(String tag, String text) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, text);
        }
    }
}
