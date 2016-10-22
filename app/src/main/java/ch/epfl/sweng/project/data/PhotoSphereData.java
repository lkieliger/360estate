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

    JSONObject getNeighborObject() {
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

    public String getUrl() {
        return mUrl;
    }

    static class Builder {
        private int mId;
        private String mUrl;
        private List<AngleMapping> mNeighborsList;

        Builder(int id) {
            mId = id;
        }

        Builder setUrl(String url) {
            mUrl = url;
            return this;
        }

        Builder setNeighborsList(List<AngleMapping> neighborsList) {
            mNeighborsList = neighborsList;
            return this;
        }

        PhotoSphereData build() {
            return new PhotoSphereData(mId, mUrl, mNeighborsList);
        }
    }

    @Override
    public boolean equals(Object that) {
        if((that == null) || (getClass() != that.getClass())){
            return false;
        }

        PhotoSphereData thatPData = (PhotoSphereData) that;
        if ((mId != thatPData.getId()) || (! mUrl.equals(thatPData.getUrl())))
            return false;

        //TODO: define AngleMapping comparison

        return true;
    }

    @Override
    public int hashCode() {
        int result = mId;
        result = 31 * result + (mUrl != null ? mUrl.hashCode() : 0);
        result = 31 * result + (mNeighborsList != null ? mNeighborsList.hashCode() : 0);
        return result;
    }
}
