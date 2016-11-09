package ch.epfl.sweng.project;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.epfl.sweng.project.data.PhotoSphereData;
import ch.epfl.sweng.project.data.Resources;
import ch.epfl.sweng.project.data.AngleMapping;
import ch.epfl.sweng.project.data.HouseManager;
import ch.epfl.sweng.project.data.JSONTags;
import ch.epfl.sweng.project.data.PhotoSphereData;
import ch.epfl.sweng.project.data.Resources;
import ch.epfl.sweng.project.filter.StateOfPopUpLayout;
import ch.epfl.sweng.project.data.Item;
import ch.epfl.sweng.project.data.ItemAdapter;

public final class DataMgmt {

    private static final String TAG = "DataMgmt";

    private DataMgmt() {
    }

    public static void getImgFromUrlIntoView(Context context, String url, ImageView imgV) {
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
        if (stateOfPopUpLayout == null) {
            query = ParseQuery.getQuery("Item");
        } else {
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
                    Log.d("DataMgmt.getData", "Error: " + e.getMessage());
                }
            }
        });
    }

    public static HouseManager getHouseManager(String id) {
        ParseQuery<Resources> query = ParseQuery.getQuery(Resources.class);
        query.whereEqualTo(JSONTags.idHouseTag, id);

        List<Resources> listResource = new ArrayList<>();

        try {
            listResource = query.find();
        } catch (ParseException e) {
            if (BuildConfig.DEBUG) {
                Log.d("DataMgmt", "Error: " + e.getMessage());
            }
        }

        if (listResource.isEmpty()) Log.d("DataMgmt", "Error: No resource has this id.");
        if (listResource.size() > 1) Log.d("DataMgmt", "Warning: The same id has different Resources.");

        Resources resources = listResource.get(0);
        List<PhotoSphereData> photoSphereDataList = new ArrayList<>();
        int startingId = -1;
        String startingUrl = "";

        try {
            photoSphereDataList = resources.getPhotoSphereDatas();
            startingId = resources.getStartingId();
            startingUrl = resources.getStartingIString();
        } catch (JSONException e) {
            if (BuildConfig.DEBUG) {
                Log.d("DataMgmt", "Error: " + e.getMessage());
            }
        }

        SparseArray<List<AngleMapping>> sparseArray = new SparseArray<>();

        for (PhotoSphereData photoSphereData : photoSphereDataList) {
            sparseArray.append(photoSphereData.getId(), photoSphereData.getNeighborsList());
        }


        return new HouseManager(sparseArray, startingId, startingUrl);
    }

    public static Resources getResourcesObject(String id) {
        ParseQuery<Resources> query = ParseQuery.getQuery(Resources.class);
        query.whereEqualTo(JSONTags.idHouseTag, id);

        List<Resources> listResource = new ArrayList<>();

        try {
            listResource = query.find();
        } catch (ParseException e) {
            if (BuildConfig.DEBUG) {
                Log.d("DataMgmt", "Error: " + e.getMessage());
            }
        }

        if (listResource.isEmpty()) {
            throw new IllegalArgumentException("DataMgmt Error: No resource has this id.");

        }
        if (listResource.size() > 1) Log.d("DataMgmt", "Warning: The same id has different Resources.");

        return listResource.get(0);
    }


    public static String getResources(String identifier, final Collection<String> urls){
        ParseQuery<Resources> query = ParseQuery.getQuery(Resources.class);
        query.whereEqualTo(JSONTags.idHouseTag,identifier);

        List<Resources> listResource = new ArrayList<>();

        try {
            listResource = query.find();
        } catch (ParseException e) {
            if(BuildConfig.DEBUG) {
                Log.d("DataMgmt", "Error: " + e.getMessage());
            }
        }

        if(listResource.isEmpty()) Log.d("DataMgmt", "Error: No resource has this id.");
        if(listResource.size()>1) Log.d("DataMgmt", "Warning: The same id has different Resources.");

        Resources resources = listResource.get(0);
        try {
            urls.addAll(resources.getPicturesList());
        } catch (JSONException e) {
            if(BuildConfig.DEBUG) {
                Log.d("DataMgmt", "Error: " + e.getMessage());
            }
        }
        return resources.getDescription();
    }
}


