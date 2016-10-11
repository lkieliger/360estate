package ch.epfl.sweng.project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.parse.Parse;

import ch.epfl.sweng.project.user.LoginActivity;

/**
 * Your app's main activity.
 */
public final class MainActivity extends AppCompatActivity {
    public static final String APP_ID = "360ESTATE";
    private static boolean parseNotInitialized = true;

    private static Context mContext;

    public static int add(final int a, final int b) {
        return a + b;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        if (parseNotInitialized) {
            //Initialize connection with the parse server
            Parse.initialize(new Parse.Configuration.Builder(this)
                    // The network interceptor is used to debug the communication between server/client
                    //.addNetworkInterceptor(new ParseLogInterceptor())
                    .applicationId(APP_ID)
                    .server("https://360.astutus.org/parse/")
                    .build()
            );
            parseNotInitialized = false;
        }

        Intent intent = new Intent(this, LoginActivity.class);

        startActivity(intent);
    }

    public static Context getContext() {
        return mContext;
    }

}