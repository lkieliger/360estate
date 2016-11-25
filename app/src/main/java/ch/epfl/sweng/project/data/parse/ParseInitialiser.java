package ch.epfl.sweng.project.data.parse;

import android.content.Context;

import com.parse.Parse;
import com.parse.ParseObject;

import ch.epfl.sweng.project.data.parse.objects.ClientRequest;
import ch.epfl.sweng.project.data.parse.objects.Favorites;
import ch.epfl.sweng.project.data.parse.objects.Item;
import ch.epfl.sweng.project.data.parse.objects.Resources;


/**
 * Singleton for initializing parse
 */
public enum ParseInitialiser {
    INSTANCE;
    public static final String APP_ID = "360ESTATE";
    private static boolean parseInitialized = false;

    public void initParse(Context c) {
        if (!parseInitialized) {
            //Initialize connection with the parse server
            Parse.initialize(new Parse.Configuration.Builder(c)
                    // The network interceptor is used to debug the communication between server/client
                    //.addNetworkInterceptor(new ParseLogInterceptor())
                    .applicationId(APP_ID)
                    .server("https://360.astutus.org/parse/")
                    .enableLocalDataStore()  // enable the Offline Mode
                    .build()
            );

            ParseObject.registerSubclass(Item.class);
            ParseObject.registerSubclass(Resources.class);
            ParseObject.registerSubclass(Favorites.class);
            ParseObject.registerSubclass(ClientRequest.class);
            parseInitialized = true;
        }
    }
}
