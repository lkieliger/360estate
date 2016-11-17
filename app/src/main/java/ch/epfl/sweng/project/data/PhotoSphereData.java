package ch.epfl.sweng.project.data;

import android.support.compat.BuildConfig;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

import static ch.epfl.sweng.project.data.JSONTags.*;

/**
 * class that provides method to simplify the conversion of the data between the Java and the JSON representation
 * It contains the data for every PhotoSphere (the neighbors list and the it's ID)
 */
public class PhotoSphereData {

    private final int mId;
    private final List<AngleMapping> mNeighborsList;

    private static final String TAG = "PhotoSphereData";

    /**
     * PhotoSphereData simple constructor
     * @param extId the Id of the PhotoSphereData
     * @param extNeighborsList the neighbors list of the PhotoSphereData
     */
    public PhotoSphereData(int extId, List<AngleMapping> extNeighborsList) {
        mId = extId;
        mNeighborsList = extNeighborsList;
    }

    /**
     *
     * @return the JSON array encoded as the data representation requires
     */
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

    /**
     *
     * @return the neighbor Object corresponding to the PhotoSphere data
     */
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

    /**
     * An inner builder that simplifies the building of a PhotoSphereData object
     */
    @SuppressWarnings("PackageVisibleInnerClass")
    static class Builder {
        private int mId;
        private List<AngleMapping> mNeighborsList = null;

        Builder(int id) {
            mId = id;
        }

        /**
         *
         * @param neighborsList the neighbors list of the PhotoSphere
         * @return the builder containing this list
         */
        Builder setNeighborsList(List<AngleMapping> neighborsList) {
            mNeighborsList = neighborsList;
            return this;
        }

        /**
         * @return the built PhotoSphereData object
         */
        PhotoSphereData build() {
            return new PhotoSphereData(mId, mNeighborsList);
        }
    }

    /**
     *
     * @param obj the object to compare to
     * @return true iff the two objects are equal
     */
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

    /**
     *
     * @return the hashcode of the current object.
     */
    @Override
    public int hashCode() {
        int result = mId;
        result = 31 * result + (getNeighborsList() != null ? getNeighborsList().hashCode() : 0);
        return result;
    }
}
