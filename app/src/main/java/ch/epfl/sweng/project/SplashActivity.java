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

    private String getDpi(){
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        int density = dm.densityDpi;
        if(density>580) return "xxhdpi";
        if(density <= 580 && density >400) return "xxhdpi";
        if(density <= 400 && density >270) return "xhdpi";
        if(density <= 270 && density >190) return "hdpi";
        return "mdpi";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ParseInitialiser.INSTANCE.initParse(this);

        WebView wv = (WebView) findViewById(R.id.webview);

        wv.loadUrl("file:///android_asset/logo_gif-xxhdpi.gif");

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
