package ch.epfl.sweng.project.utils;


import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

import ch.epfl.sweng.project.R;



/**
 * @brief tool class that encapsulate the creation and the maintain off toast(popups in activities.).
 */
public class Toaster {

    /**
     * @brief
     * create the toast and show it in the current activity.
     *
     * @param context
     * the context of the current activity
     * @param message
     * the message that is to show.
     */
    public static void createAndShowToast(Context context,String message){

            Toast popMsg = Toast.makeText(
                    context,
                    context.getResources().getText(R.string.error_login_unsuccessful),
                    Toast.LENGTH_SHORT);
            popMsg.show();

        }
    }

}
