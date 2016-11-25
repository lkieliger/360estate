package ch.epfl.sweng.project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;

import com.parse.ParseUser;

import ch.epfl.sweng.project.user.LoginActivity;
import ch.epfl.sweng.project.user.RegisterActivity;
import ch.epfl.sweng.project.user.display.ListActivity;
import ch.epfl.sweng.project.util.ParseInitialiser;

public class SplashActivity extends AppCompatActivity {
    public static final String APP_ID = "360ESTATE";
    public static final String TAG = "SplashScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ParseInitialiser.INSTANCE.initParse(this);

        WebView wv = (WebView) findViewById(R.id.webview);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        float density = dm.xdpi;

        String dpi;
        if(density > 480) dpi = "xxxhpi";
        else if(density > 320) dpi = "xxhpi";
        else if(density > 240) dpi = "xhpi";
        else if(density > 160) dpi = "hpi";
        else dpi = "mdpi";

        wv.loadUrl("file:///android_asset/logo_gif-"+dpi+".gif");

        // Check if the user is already logged in in the localDatastore, and jump to the ListActivity accordingly
        if (userAlreadyLoggedIn()) {
            Intent intent = new Intent(this, ListActivity.class);
            startActivity(intent);
        }
    }

    /**
     * This method is called when the user wants to register a new account
     * instead of directly logging in the app
     *
     * @param view The view from which the event was generated
     */
    public void proceedToRegistration(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * This method is called when the user presses the button to log in
     *
     * @param view The view from which the event was generated
     */
    public void proceedToLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * @return true if user's information is already cached.
     */
    private boolean userAlreadyLoggedIn() {
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser != null) {
            if (currentUser.isAuthenticated()) {
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "The user is already logged in");

                return true;
            } else {
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "The user is not authenticated");
            }
        }

        return false;
    }
}
