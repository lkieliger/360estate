package ch.epfl.sweng.project.data.parse;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.data.panorama.HouseManager;
import ch.epfl.sweng.project.data.panorama.PhotoSphereData;
import ch.epfl.sweng.project.data.panorama.adapters.SpatialData;
import ch.epfl.sweng.project.data.parse.objects.Favorites;
import ch.epfl.sweng.project.data.parse.objects.Item;
import ch.epfl.sweng.project.data.parse.objects.JSONTags;
import ch.epfl.sweng.project.data.parse.objects.Resources;
import ch.epfl.sweng.project.features.propertylist.adapter.ItemAdapter;
import ch.epfl.sweng.project.features.propertylist.filter.FilterValues;

import static ch.epfl.sweng.project.util.Toaster.shortToast;


public final class PInterface {

    private static final String TAG = "ParseInterface";

    private PInterface() {
    }

    public static void getItemList(final Collection<Item> itemList,
                                   final ItemAdapter itemAdapter,
                                   FilterValues filterValues,
                                   Boolean favoriteToggled,
                                   String idUser,
                                   final Context context) {

        List<ParseQuery<Item>> queries = new ArrayList<>();

        if (favoriteToggled) {
            Set<String> listId = getFavoriteFromId(idUser).getFavoritesFromLocal();
            if (!listId.isEmpty()) {
                for (String s : listId) {
                    ParseQuery<Item> queryTemp = ParseQuery.getQuery(Item.class);
                    if (filterValues != null) {
                        queryTemp = filterValues.filterQuery();
                    }
                    queryTemp.whereEqualTo("idHouse", s);
                    queries.add(queryTemp);
                }
            } else {
                fetchItems(null, itemList, itemAdapter, context);
                return;
            }
        } else {
            if (filterValues != null) {
                queries.add(filterValues.filterQuery());
            }
        }

        if (queries.isEmpty()) {
            fetchItems(new ParseQuery<>(Item.class), itemList, itemAdapter, context);
        } else {
            fetchItems(ParseQuery.or(queries), itemList, itemAdapter, context);
        }
    }


    private static void fetchItems(ParseQuery<Item> query,
                                   final Collection<Item> itemList,
                                   final ItemAdapter itemAdapter,
                                   final Context context) {
        if (query != null) {

            FindCallback<Item> callback = new FindCallback<Item>() {
                public void done(List<Item> objects, ParseException e) {
                    if (e == null) {
                        Log.d(TAG, "Retrieved " + objects.size() + " house items");
                        itemList.clear();
                        itemList.addAll(objects);
                        itemAdapter.notifyDataSetChanged();

                        //we only update the localDataStore if internet was available at the time of the query
                        if (ParseProxy.PROXY.internetAvailable()) {
                            ParseObject.pinAllInBackground(objects);
                        }

                    } else {
                        Log.d("fetchItems", "Error: " + e.getMessage());
                    }

                    if (!ParseProxy.PROXY.internetAvailable()) {
                        shortToast(context, context.getResources().getText(R.string.no_internet_access));
                    }
                }
            };

            ParseProxy.PROXY.executeQuery(query, callback, TAG);

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
        int startingId;
        String startingUrl = "";

        try {
            photoSphereDataList = resources.getPhotoSphereDatas();
            startingId = resources.getStartingId();
            startingUrl = resources.getStartingUrl();
        } catch (JSONException e) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Error: " + e.getMessage());
            }
            //If there is a problem during the parsing of the data, we set the ID to -1
            startingId = -1;
        }

        SparseArray<List<SpatialData>> sparseArray = new SparseArray<>();

        for (PhotoSphereData photoSphereData : photoSphereDataList) {
            sparseArray.append(photoSphereData.getId(), photoSphereData.getNeighborsList());
        }


        return new HouseManager(sparseArray, startingId, startingUrl);
    }

    public static void getDataForDescription(String id,
                                             final Collection<String> urls,
                                             StringBuilder description,
                                             final Context context) {

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


    private static List<Resources> getResourcesObject(String id, final Context context) {
        ParseQuery<Resources> query = ParseQuery.getQuery(Resources.class);
        query.whereEqualTo(JSONTags.idHouseTag, id);

        List<Resources> listResource = new ArrayList<>();

        try {
            listResource = ParseProxy.PROXY.executeFindQuery(query);

            if (ParseProxy.PROXY.internetAvailable()) {
                ParseObject.pinAllInBackground(listResource);
            }
        } catch (ParseException e) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Error in Resources query : " + e.getMessage());
            }
        }

        if (!ParseProxy.PROXY.internetAvailable()) {
            shortToast(context, context.getResources().getText(R.string.no_internet_access));
        }

        if (listResource.size() > 1)
            Log.d(TAG, "Warning: The same id has different Resources.");

        return listResource;
    }


    public static void overrideFavorites(String idUser, Collection<String> list) {
        Favorites f = getFavoriteFromId(idUser);
        f.setFavorites((Set<String>) list);

    }

    public static Favorites getFavoriteFromId(String idUser) {
        ParseQuery<Favorites> query = ParseQuery.getQuery(Favorites.class);
        query.whereEqualTo("idUser", idUser);

        List<Favorites> listFavorites = new ArrayList<>();

        try {
            listFavorites = ParseProxy.PROXY.executeFindQuery(query);

            if (ParseProxy.PROXY.internetAvailable()) {
                ParseObject.pinAllInBackground(listFavorites);
            }
        } catch (ParseException e) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Error in favorites query : " + e.getMessage());
            }
        }

        if (listFavorites.size() > 1)
            if (BuildConfig.DEBUG)
                Log.d(TAG, "Warning: The same user id has different Favorites.");

        Favorites f;
        if (listFavorites.isEmpty()) {
            f = saveNewFavorites(idUser);
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
            throw new IllegalArgumentException("PInterface Error: No Item has this id.");

        }
        if (listItems.size() > 1)
            Log.d(TAG, "Warning: The same id has different Items.");

        return listItems.get(0);
    }
}


