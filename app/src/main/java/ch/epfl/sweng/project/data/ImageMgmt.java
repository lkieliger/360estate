package ch.epfl.sweng.project.data;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.List;

import ch.epfl.sweng.project.BuildConfig;

public final class ImageMgmt {

    private static final String TAG = "ImageMgmt";

    private ImageMgmt() {
    }

    public static void getImgFromUrlIntoView(Context context, String url, ImageView imgV) {
        Picasso.with(context)
                .load(url)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE) //Don't store in memory, prefers disk
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
                mBitmap = Picasso.with(context).load(url).get();
            } catch (IOException e) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }
        return mBitmap;
    }

    /**
     * @param context
     * @param url
     * @param t
     */
    public static void getBitmapFromUrl(final Context context, final String url, final Target t) {

        //The following code runs the Picasso calls form the main thread
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Picasso.with(context)
                        .load(url)
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//Don't store in memory, prefers disk
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(t);
            }
        };

        mainHandler.post(r);
    }

    public static void warmCache(final Context context, final List<String> urls) {

        for (String url : urls) {
            Log.d(TAG, "Asking Picasso to fetch " + url);
            Picasso.with(context)
                    .load(url)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//Don't store in memory, prefers disk
                    .fetch();
        }

    }
}
