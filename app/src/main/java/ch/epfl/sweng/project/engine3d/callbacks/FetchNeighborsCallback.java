package ch.epfl.sweng.project.engine3d.callbacks;

import com.squareup.picasso.Callback;

import ch.epfl.sweng.project.util.LogHelper;

public class FetchNeighborsCallback implements Callback {

    private static final String TAG = "FetchNeighborsCallback";

    private final String url;

    public FetchNeighborsCallback(String url) {
        this.url = url;
    }

    @Override
    public void onSuccess() {
        LogHelper.log(TAG, "Fetching " + url + " was a success");
    }

    @Override
    public void onError() {
        LogHelper.log(TAG, "Fetching " + url + " was a failure");
    }
}