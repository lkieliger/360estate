package ch.epfl.sweng.project.data;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.List;

import ch.epfl.sweng.project.util.LogHelper;

public class ImageMgmt {

    private static final String TAG = "ImageMgmt";

    public ImageMgmt() {
    }

    /**
     * Get an image from a URL that wll be directly loaded into the view passed in argument.
     *
     * @param context
     * @param url the url of the image.
     * @param imgV the view to load the image into.
     */
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
    public Bitmap getBitmapFromUrl(Context context, String url) {

        Bitmap mBitmap = null;

        if (url != null && !url.isEmpty()) {
            try {
                mBitmap = Picasso.with(context).load(url).get();
            } catch (IOException e) {
                LogHelper.log(TAG, e.getMessage());
            }
        }
        return mBitmap;
    }

    /**
     * Get an image from a URL that wll be directly loaded into the target passed in argument.
     *
     * @param context
     * @param url the url of the image.
     * @param t the target to load the image
     */
    public void getBitmapFromUrl(final Context context, final String url, final Target t) {

        //The following code runs the Picasso calls form the main thread
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Picasso.with(context)
                        .load(url)
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//Don't store in memory, prefers disk
                        .into(t);
            }
        };

        mainHandler.post(r);
    }

    /**
     * Pre-load the images given with their Url's into the disk.
     *
     * @param context
     * @param urls
     */
    public void warmCache(final Context context, final List<String> urls) {

        for (String url : urls) {
            LogHelper.log(TAG, "Asking Picasso to fetch " + url);
            Picasso.with(context)
                    .load(url)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//Don't store in memory, prefers disk
                    .fetch();
        }

    }
}
