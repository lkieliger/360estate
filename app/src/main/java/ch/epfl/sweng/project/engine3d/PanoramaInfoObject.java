package ch.epfl.sweng.project.engine3d;

import android.util.Log;


public final class PanoramaInfoObject extends PanoramaObject {

    private final static String TAG = "PanoramaInfoObject";

    @Override
    public void reactWith(PanoramaRenderer p) {
        Log.d(TAG, "I am a panorama info object and I just displayed some text," +
                "how cool is that ?");

    }
}
