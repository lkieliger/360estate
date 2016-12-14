package ch.epfl.sweng.project.engine3d.callbacks;

import android.util.Log;

import com.squareup.picasso.Callback;

public class FetchNeighborsCallback implements Callback {

    private static final String TAG = "FetchNeighborsCallback";

    private final String url;

    public FetchNeighborsCallback(String url) {
        this.url = url;
    }

    @Override
    public void onSuccess() {
        Log.d(TAG, "Fetching " + url + " was a success");
    }

    @Override
    public void onError() {
        Log.d(TAG, "Fetching " + url + " was a failure");
    }
}