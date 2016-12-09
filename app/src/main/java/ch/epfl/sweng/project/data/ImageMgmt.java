package ch.epfl.sweng.project.data;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import ch.epfl.sweng.project.BuildConfig;

public final class ImageMgmt {

    private static final String TAG = "ImageMgmt";
    private static final int WIDTH = 2048;
    private static final int HEIGHT = 4096;

    private ImageMgmt() {
    }

    public static void getImgFromUrlIntoView(Context context, String url, ImageView imgV) {
        Picasso.with(context).load(url).into(imgV);
    }

    /**
     * Get a bitmap from url using Picasso.
     *
     * @param context the current context of the activity.
     * @param url     the url to load
     */
    public static Bitmap getBitmapFromUrl(Context context, String url) {

        Bitmap mBitmap = null;
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, exception.getMessage());
                }
            }
        });

        if (url != null && !url.isEmpty()) {
            try {
                mBitmap = builder.build().with(context).load(url).resize(WIDTH, HEIGHT).get();
            } catch (IOException e) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }
        return mBitmap;
    }

}
