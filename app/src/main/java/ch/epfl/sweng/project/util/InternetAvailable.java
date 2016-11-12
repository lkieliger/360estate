package ch.epfl.sweng.project.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;



public class InternetAvailable {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static boolean isInternetAvailable(final Context context) {
        boolean isConnected = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null) {
                isConnected = netInfo.isConnected();
            }


        }


        return isConnected;
    }
}
