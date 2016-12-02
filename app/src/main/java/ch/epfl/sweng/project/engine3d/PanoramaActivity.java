package ch.epfl.sweng.project.engine3d;


import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.rajawali3d.view.ISurface;
import org.rajawali3d.view.SurfaceView;

import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.data.DataMgmt;
import ch.epfl.sweng.project.data.panorama.HouseManager;
import ch.epfl.sweng.project.engine3d.listeners.PanoramaTouchListener;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

@SuppressWarnings("FieldCanBeLocal")
public final class PanoramaActivity extends Activity {

    private SurfaceView mSurface = null;
    private PanoramaRenderer mRenderer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HouseManager houseManager = DataMgmt.getHouseManager(getIntent().getStringExtra("id"), getApplicationContext());

        if (houseManager.getStartingId() == -1) {
            Toast toast = Toast.makeText(this, getString(R.string.invalid_parse_object), Toast.LENGTH_SHORT);
            View view = toast.getView();
            view.setBackgroundColor(Color.GRAY);

            TextView text = (TextView) view.findViewById(android.R.id.message);
            text.setBackgroundColor(Color.GRAY);

            toast.show();
            onStop();
        }

        setContentView(R.layout.activity_main);

        mSurface = new SurfaceView(this);
        mSurface.setFrameRate(60.0);
        mSurface.setRenderMode(ISurface.RENDERMODE_WHEN_DIRTY);

        // Add mSurface to root view
        addContentView(mSurface, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT));
        mRenderer = new PanoramaRenderer(this, getWindowManager().getDefaultDisplay(), houseManager);
        mSurface.setSurfaceRenderer(mRenderer);

        //Create listener for handling user inputs
        View.OnTouchListener listener = new PanoramaTouchListener(mRenderer);
        mSurface.setOnTouchListener(listener);
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

    @Override
    public void onStart() {
        super.onStart();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
    }

    @Override
    public void onStop() {
        super.onStop();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        finish();
    }
}
