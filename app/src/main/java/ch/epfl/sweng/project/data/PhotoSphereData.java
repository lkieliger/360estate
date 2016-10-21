package ch.epfl.sweng.project.data;

import android.support.compat.BuildConfig;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static ch.epfl.sweng.project.data.JSONTags.*;


class PhotoSphereData {

    private final int mId;
    private final String mUrl;
    private final List<AngleMapping> mNeighborsList;

    private static final String TAG = "PhotoSphereData";

    PhotoSphereData(int extId, String extUrl, List<AngleMapping> extNeighborsList) {
        mId = extId;
        mUrl = extUrl;
        mNeighborsList = extNeighborsList;
    }

    private JSONArray getNeighborsJsonArray() {
        JSONArray neighborsJsonArray = new JSONArray();
        try {
            for (AngleMapping elem : mNeighborsList) {
                JSONObject angleMapping = new JSONObject();
                angleMapping.put(thetaTag, elem.getTheta());
                angleMapping.put(phiTag, elem.getPhi());
                angleMapping.put(idTag, elem.getId());
                angleMapping.put(urlTag, elem.getUrl());
                neighborsJsonArray.put(angleMapping);
            }
        } catch (JSONException e) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "JSONException:" + e.getMessage());
            }
        }
        return neighborsJsonArray;
    }

    public JSONObject getNeighborObject() {
        JSONObject neighborObject = new JSONObject();

        try {
            neighborObject.put(idTag, mId);
            neighborObject.put(urlTag, mUrl);
            neighborObject.put(neighborsListTag, getNeighborsJsonArray());
        } catch (JSONException e) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "JSONException:" + e.getMessage());
            }
        }
        return neighborObject;
    }

    public int getId() {
        return mId;
    }
/*
    public List<AngleMapping> parseNeighborsJsonArray() {
        JSONArray jsonArray = getJSONArray("mNeighborsList");
        List<AngleMapping> resultList = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    resultList.add(new AngleMapping(
                            new Tuple<>(
                                    jsonArray.getJSONObject(i).getDouble("theta"),
                                    jsonArray.getJSONObject(i).getDouble("phi")
                            ),
                            jsonArray.getJSONObject(i).getInt("mId"),
                            jsonArray.getJSONObject(i).getString("url")));
                } catch (JSONException e) {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "JSONException:" + e.getMessage());
                    }
                }
            }
        }
        return resultList;
    }

    */
}
