package ch.epfl.sweng.project.data;

import android.support.compat.BuildConfig;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

import static ch.epfl.sweng.project.data.JSONTags.*;


public class PhotoSphereData {

    private final int mId;
    private final List<AngleMapping> mNeighborsList;

    private static final String TAG = "PhotoSphereData";

    public PhotoSphereData(int extId, List<AngleMapping> extNeighborsList) {
        mId = extId;
        mNeighborsList = extNeighborsList;
    }

    private JSONArray getNeighborsJsonArray() {
        JSONArray neighborsJsonArray = new JSONArray();
        try {
            for (AngleMapping elem : getNeighborsList()) {
                JSONObject angleMapping = elem.toJSONObject();
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


    public List<AngleMapping> getNeighborsList() {
        return mNeighborsList;
    }

    static class Builder {
        private int mId;
        private List<AngleMapping> mNeighborsList = null;

        Builder(int id) {
            mId = id;
        }

        Builder setNeighborsList(List<AngleMapping> neighborsList) {
            mNeighborsList = neighborsList;
            return this;
        }

        PhotoSphereData build() {
            return new PhotoSphereData(mId, mNeighborsList);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        PhotoSphereData thatPData = (PhotoSphereData) obj;

        if (getNeighborsList().size() != thatPData.getNeighborsList().size())
            return false;

        Iterator<AngleMapping> mNeighborListIt = mNeighborsList.iterator();
        Iterator<AngleMapping> thatNeighborListIt = thatPData.getNeighborsList().iterator();

        while (mNeighborListIt.hasNext()) {
            if (!mNeighborListIt.next().equals(thatNeighborListIt.next()))
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = mId;
        result = 31 * result + (getNeighborsList() != null ? getNeighborsList().hashCode() : 0);
        return result;
    }
}
