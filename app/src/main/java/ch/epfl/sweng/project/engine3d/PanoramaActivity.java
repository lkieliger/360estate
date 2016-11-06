package ch.epfl.sweng.project.engine3d;


import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.rajawali3d.view.ISurface;
import org.rajawali3d.view.SurfaceView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.epfl.sweng.project.DataMgmt;
import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.data.AngleMapping;
import ch.epfl.sweng.project.data.HouseManager;
import ch.epfl.sweng.project.data.PhotoSphereData;
import ch.epfl.sweng.project.data.Resources;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

@SuppressWarnings("FieldCanBeLocal")
public class PanoramaActivity extends Activity {

    private SurfaceView mSurface = null;
    private PanoramaRenderer mRenderer = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSurface = new SurfaceView(this);
        mSurface.setFrameRate(60.0);
        mSurface.setRenderMode(ISurface.RENDERMODE_WHEN_DIRTY);

        // Add mSurface to root view
        addContentView(mSurface, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT));


        ParseObject.registerSubclass(Resources.class);
        HouseManager houseManager = DataMgmt.getHouseManager(getIntent().getStringExtra("id"));

        mRenderer = new PanoramaRenderer(this, getWindowManager().getDefaultDisplay(), houseManager);
        mSurface.setSurfaceRenderer(mRenderer);

        //Create listener for handling user inputs
        View.OnTouchListener listener = new PanoramaTouchListener(mRenderer);
        mSurface.setOnTouchListener(listener);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRenderer.onResume();
        int viewFlags = SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | SYSTEM_UI_FLAG_FULLSCREEN;
        int surfaceViewFlags = SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | SYSTEM_UI_FLAG_FULLSCREEN |
                SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(surfaceViewFlags);
        mSurface.setSystemUiVisibility(viewFlags);
    }


    @Override
    protected void onPause() {
        super.onPause();
        mRenderer.onPause();
    }

    public PanoramaRenderer getAssociatedRenderer() {
        return mRenderer;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Panorama Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }
}
