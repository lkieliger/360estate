package ch.epfl.sweng.project;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.epfl.sweng.project.data.AngleMapping;
import ch.epfl.sweng.project.data.HouseManager;
import ch.epfl.sweng.project.data.ItemAdapter;
import ch.epfl.sweng.project.data.JSONTags;
import ch.epfl.sweng.project.data.PhotoSphereData;
import ch.epfl.sweng.project.data.parse.objects.Favorites;
import ch.epfl.sweng.project.data.parse.objects.Item;
import ch.epfl.sweng.project.data.parse.objects.Resources;
import ch.epfl.sweng.project.filter.StateOfPopUpLayout;

import static ch.epfl.sweng.project.util.InternetAvailable.isInternetAvailable;
import static ch.epfl.sweng.project.util.Toaster.shortToast;


public final class DataMgmt {

    private static final String TAG = "DataMgmt";
    private static final int WIDTH = 2048;
    private static final int HEIGHT = 4096;

    private DataMgmt() {
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


    public static void getItemList(
            final Collection<Item> itemList, final ItemAdapter itemAdapter, StateOfPopUpLayout stateOfPopUpLayout,
            Boolean isFavoriteToggle, String idUser, final Context context) {
        List<ParseQuery<Item>> queries = new ArrayList<>();

        if (isFavoriteToggle) {
            Set<String> listId = DataMgmt.getFavoriteFromId(idUser, context).getFavoritesFromLocal();
            if (!listId.isEmpty()) {
                for (String s : listId) {
                    ParseQuery<Item> queryTemp = ParseQuery.getQuery(Item.class);
                    if (stateOfPopUpLayout != null) {
                        queryTemp = stateOfPopUpLayout.filterQuery();
                    }
                    queryTemp.whereEqualTo("idHouse", s);
                    queries.add(queryTemp);
                }
            } else {
                fetchItems(null, itemList, itemAdapter, context);
                return;
            }
        } else {
            if (stateOfPopUpLayout != null) {
                queries.add(stateOfPopUpLayout.filterQuery());
            }
        }

        if (queries.isEmpty()) {
            fetchItems(new ParseQuery<>(Item.class), itemList, itemAdapter, context);
        } else {
            fetchItems(ParseQuery.or(queries), itemList, itemAdapter, context);
        }
    }


    private static void fetchItems(ParseQuery<Item> query, final Collection<Item> itemList,
                                   final ItemAdapter itemAdapter, final Context context) {
        if (query != null) {

            if (!isInternetAvailable(context)) {
                shortToast(context, context.getResources().getText(R.string.no_internet_access));
                query.fromLocalDatastore();
            }

            query.findInBackground(new FindCallback<Item>() {
                public void done(List<Item> objects, ParseException e) {
                    if (e == null) {
                        Log.d(TAG, "Retrieved " + objects.size() + " house items");
                        itemList.clear();
                        itemList.addAll(objects);
                        itemAdapter.notifyDataSetChanged();


                        if (isInternetAvailable(context)) {

                            ParseObject.pinAllInBackground(objects);
                        }


                    } else {
                        Log.d("DataMgmt.getItemList", "Error: " + e.getMessage());
                    }
                }
            });
        } else {
            itemList.clear();
            itemAdapter.notifyDataSetChanged();
        }
    }


    /**
     * this function should only be called if internet is available.
     */
    public static HouseManager getHouseManager(String id, final Context context) {
        Resources resources = getResourcesObject(id, context).get(0); //
        List<PhotoSphereData> photoSphereDataList = new ArrayList<>();
        int startingId = -1;
        String startingUrl = "";

        try {
            photoSphereDataList = resources.getPhotoSphereDatas();
            startingId = resources.getStartingId();
            startingUrl = resources.getStartingUrl();
        } catch (JSONException e) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Error: " + e.getMessage());
            }
        }

        SparseArray<List<AngleMapping>> sparseArray = new SparseArray<>();

        for (PhotoSphereData photoSphereData : photoSphereDataList) {
            sparseArray.append(photoSphereData.getId(), photoSphereData.getNeighborsList());
        }


        return new HouseManager(sparseArray, startingId, startingUrl);
    }

    public static void getDataForDescription(String id, final Collection<String> urls, StringBuilder description
            , final Context context) {
        List<Resources> resourcesList = getResourcesObject(id, context);


        if (!resourcesList.isEmpty()) {
            Resources resource = resourcesList.get(0);
            try {
                urls.addAll(resource.getPicturesList());
            } catch (JSONException e) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Error: " + e.getMessage());
                }
            }

            description.append(resource.getDescription());
        }

    }


    public static List<Resources> getResourcesObject(String id, final Context context) {
        ParseQuery<Resources> query = ParseQuery.getQuery(Resources.class);
        query.whereEqualTo(JSONTags.idHouseTag, id);

        List<Resources> listResource = new ArrayList<>();

        if (!isInternetAvailable(context)) {
            shortToast(context, context.getResources().getText(R.string.no_internet_access));
            query.fromLocalDatastore();
        }


        try {
            listResource = query.find();

            if (isInternetAvailable(context)) {

                ParseObject.pinAllInBackground(listResource);
            }


        } catch (ParseException e) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Error: " + e.getMessage());
            }
        }

        if (listResource.size() > 1)
            Log.d(TAG, "Warning: The same id has different Resources.");

        return listResource;
    }


    public static void overrideFavorites(String idUser, Collection<String> list, Context context) {
        Favorites f = getFavoriteFromId(idUser, context);
        f.setFavorites((Set<String>) list);

    }

    public static Favorites getFavoriteFromId(String idUser, Context context) {
        ParseQuery<Favorites> query = ParseQuery.getQuery(Favorites.class);
        query.whereEqualTo("idUser", idUser);

        List<Favorites> listFavorites = new ArrayList<>();

        if (!isInternetAvailable(context)) {
            query.fromLocalDatastore();
        }


        try {


            listFavorites = query.find();

            if (isInternetAvailable(context)) {
                ParseObject.pinAllInBackground(listFavorites);
            }
        } catch (ParseException e) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, e.getMessage());
            }
        }

        if (listFavorites.size() > 1)
            Log.d(TAG, "Warning: The same id has different Favorites.");

        Favorites f;
        if (listFavorites.isEmpty()) {
            f = saveNewFavorites(idUser);
            if (isInternetAvailable(context)) {
                f.synchronizeFromServer(); // fetch local set.
            }
        } else {
            f = listFavorites.get(0);

        }

        return f;
    }

    private static Favorites saveNewFavorites(String idUser) {
        Favorites f = new Favorites(new HashSet<String>(), idUser);

        f.saveEventually();


        return f;
    }

    public static Item getItemFromId(String id) {
        ParseQuery<Item> query = new ParseQuery<>(Item.class);
        query.whereEqualTo("idHouse", id);

        List<Item> listItems = new ArrayList<>();
        try {
            listItems = query.find();


        } catch (ParseException e) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, e.getMessage());
            }
        }
        if (listItems.isEmpty()) {
            throw new IllegalArgumentException("DataMgmt Error: No Item has this id.");

        }
        if (listItems.size() > 1)
            Log.d(TAG, "Warning: The same id has different Items.");

        return listItems.get(0);
    }
}


