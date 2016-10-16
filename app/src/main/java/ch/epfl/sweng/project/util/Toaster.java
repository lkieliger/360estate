package ch.epfl.sweng.project.util;


import android.content.Context;
import android.widget.Toast;


/**
 * utility class that eases the Toast displaying process
 */
public final class Toaster {

    private Toaster() {
    }

    /**
     * creates the toast and displays it in the current activity.
     *
     * @param context the context of the current activity
     * @param message the message that is to show.
     */
    public static void shortToast(Context context, CharSequence message) {
        Toast popMsg = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        popMsg.show();
    }

    /**
     * creates the toast and displays it in the current activity.
     *
     * @param context the context of the current activity
     * @param message the message that is to show.
     */
    public static void longToast(Context context, CharSequence message) {
        Toast popMsg = Toast.makeText(context, message, Toast.LENGTH_LONG);
        popMsg.show();
    }
}
