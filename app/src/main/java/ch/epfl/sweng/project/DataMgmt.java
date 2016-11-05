package ch.epfl.sweng.project;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import ch.epfl.sweng.project.filter.StateOfPopUpLayout;
import ch.epfl.sweng.project.data.Item;
import ch.epfl.sweng.project.data.ItemAdapter;

public final class DataMgmt {

    private static final String TAG = "DataMgmt";

    private DataMgmt() {
    }

    public static void getImgFromUrlIntoView(Context context, String url, ImageView imgV){
        Picasso.with(context).load(url).into(imgV);
    }


    /**
     * Get a bitmap from url using Picasso.
     *
     * @param mContext
     * @param url the url to load
     */
    public static Bitmap getBitmapfromUrl(Context mContext, String url) {

        Bitmap mBitmap = null;
        Picasso.Builder builder = new Picasso.Builder(mContext);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, exception.getMessage());
                }
            }
        });

        try {
            mBitmap = builder.build().with(mContext).load(url).get();
        } catch (IOException e) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, e.getMessage());
            }
        }
        return mBitmap;
    }

    public static void getData(
            final Collection<Item> itemList, final ItemAdapter itemAdapter, StateOfPopUpLayout stateOfPopUpLayout) {
        ParseQuery<Item> query;
        if(stateOfPopUpLayout == null) {
            query = ParseQuery.getQuery("Item");
        }
        else{
            query = stateOfPopUpLayout.filterQuery();
        }
        query.findInBackground(new FindCallback<Item>() {
            public void done(List<Item> objects, ParseException e) {
                if (e == null) {
                    Log.d("DataMgmt", "Retrieved " + objects.size() + " house items");
                    itemList.clear();
                    itemList.addAll(objects);
                    itemAdapter.notifyDataSetChanged();

                } else {
                    Log.d("DataMgmt", "Error: " + e.getMessage());
                }
            }
        });
    }


}
