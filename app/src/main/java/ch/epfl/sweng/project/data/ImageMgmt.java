package ch.epfl.sweng.project.data;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;

import ch.epfl.sweng.project.BuildConfig;

public final class ImageMgmt {

    private static final String TAG = "ImageMgmt";
    //TODO: fusion with panorama renderer values
    private static final int WIDTH = 4096;
    private static final int HEIGHT = 2048;

    private ImageMgmt() {
    }

    public static void getImgFromUrlIntoView(Context context, String url, ImageView imgV) {
        Picasso.with(context)
                .load(url)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE) //Dont store in memory, prefers disk
                .into(imgV);
    }

    /**
     * Get a bitmap from url using Picasso.
     *
     * @param context the current context of the activity.
     * @param url     the url to load
     */
    public static Bitmap getBitmapFromUrl(Context context, String url) {

        Bitmap mBitmap = null;

        if (url != null && !url.isEmpty()) {
            try {
                mBitmap = Picasso.with(context).load(url).resize(WIDTH, HEIGHT).get();
            } catch (IOException e) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }
        return mBitmap;
    }

    public static void getBitmapFromUrl(final Context context, final String url, final Target t) {

        //The following code runs the Picasso calls form the main thread
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Picasso.with(context)
                        .load(url)
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE) //Dont store in memory, prefers disk
                        .resize(WIDTH, HEIGHT).into(t);
            }
        };

        mainHandler.post(r);
    }

}
