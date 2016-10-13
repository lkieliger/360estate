package ch.epfl.sweng.project.engine3d;


import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import org.rajawali3d.view.ISurface;
import org.rajawali3d.view.SurfaceView;

import ch.epfl.sweng.project.R;

@SuppressWarnings("FieldCanBeLocal")
public class PanoramaActivity extends Activity {

    private static final String TAG = "PanoramaActivity";

    private SurfaceView mSurface = null;
    private PanoramaRenderer mRenderer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSurface = new SurfaceView(this);
        mSurface.setFrameRate(60.0);
        mSurface.setRenderMode(ISurface.RENDERMODE_WHEN_DIRTY);

        // Add mSurface to root view
        addContentView(mSurface, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT));

        mRenderer = new PanoramaRenderer(this);
        mSurface.setSurfaceRenderer(mRenderer);


        //Create listener for handling user inputs
        View.OnTouchListener listener = new PanoramaTouchListener(mRenderer);

        mSurface.setOnTouchListener(listener);
    }
}
