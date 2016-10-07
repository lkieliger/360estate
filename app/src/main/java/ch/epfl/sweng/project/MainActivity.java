package ch.epfl.sweng.project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.parse.Parse;
import com.parse.ParseObject;

import ch.epfl.sweng.project.list.Item;

/**
 * Your app's main activity.
 */
public final class MainActivity extends AppCompatActivity {
    public static final String NAME_KEY = "name";
    public static final String APP_ID = "360ESTATE";
    private static boolean parseNotInitialized = true;

    public static int add(final int a, final int b) {
        return a + b;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (parseNotInitialized) {
            //Initialize connection with the parse server
            Parse.initialize(new Parse.Configuration.Builder(this)
                    .applicationId(APP_ID)
                    .server("http://vps-fra.astutus.org:1337/parse/")
                    .build()
            );
            parseNotInitialized = false;
        }

        ParseObject.registerSubclass(Item.class);
        Intent intent = new Intent(this, ListActivity.class);

        startActivity(intent);
    }
}