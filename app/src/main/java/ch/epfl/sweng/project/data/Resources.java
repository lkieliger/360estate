package ch.epfl.sweng.project.data;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.engine3d.components.PanoramaComponentType;
import ch.epfl.sweng.project.util.Tuple;

import static ch.epfl.sweng.project.data.JSONTags.*;

/**
 * ParseObject class that provides the interface for storing objects on the server
 */
@SuppressWarnings("WeakerAccess")
@ParseClassName("Resources")
public class Resources extends ParseObject {

    private static final String TAG = "Resources class";

    @SuppressWarnings("RedundantNoArgConstructor")
    public Resources() {
        // Default constructor needed for Parse objects
    }

    public void setDescription(String desc) {
        put(descriptionTag, desc);
    }

    public void setId(String id) {
        put(idHouseTag, id);
    }

    /**
     *
     * @param picturesUrlList the URLs to be displayed in the description activity
     */
    public void setPicturesUrlList(Collection<String> picturesUrlList) {
        JSONArray urlArray = new JSONArray(picturesUrlList);
        put(picturesListTag, urlArray);
    }

    /**
     * updates the content of the Parse object with the parameters values
     * @param photoSphereList the list of PhotoSphere informations (one entry in the list = one room)
     * @param startingId the Id of the first room
     * @param startingUrl the Url of the first room
     */
    public void setPhotoSphereDatas(Iterable<PhotoSphereData> photoSphereList, int startingId, String startingUrl) {
        JSONObject photoSphereDatas = new JSONObject();

        JSONArray neighborsList = new JSONArray();
        for (PhotoSphereData p : photoSphereList) {
            neighborsList.put(p.getNeighborObject());
        }
        try {
            photoSphereDatas.put(startingIdTag, String.valueOf(startingId));
            photoSphereDatas.put(startingUrlTag, startingUrl);
            photoSphereDatas.put(panoramaRoomsTag, neighborsList);
        } catch (JSONException e) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, e.getMessage());
            }
        }
        put(panoSphereDatasTag, photoSphereDatas);
    }

    public String getDescription() {
        return getString(descriptionTag);
    }

    public String getId() {
        return getString(idHouseTag);
    }

    /**
     *
     * @return the list of the picures to be displayed on the description activity
     * @throws JSONException
     */
    public List<String> getPicturesList() throws JSONException {
        JSONArray urlArray = getJSONArray(picturesListTag);
        List<String> picturesList = new ArrayList<>();

        if (urlArray == null) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Error parsing the picturesList array from JSON");
            }
            return picturesList;
        }

        for (int i = 0; i < urlArray.length(); i++) {
            picturesList.add(urlArray.getString(i));
        }

        return picturesList;
    }

    /**
     *
     * @return the list of all PhotoSphereData that are contained in the Parse object
     * @throws JSONException
     */
    public List<PhotoSphereData> getPhotoSphereDatas() throws JSONException {
        JSONArray photoSphereDataArray = getJSONObject(panoSphereDatasTag).getJSONArray(panoramaRoomsTag);
        List<PhotoSphereData> photoSphereDatas = new ArrayList<>(photoSphereDataArray.length());

        for (int i = 0; i < photoSphereDataArray.length(); i++) {
            JSONObject photoSphereObject = (JSONObject) photoSphereDataArray.get(i);
            photoSphereDatas.add(parsePhotoSphereData(photoSphereObject));
        }

        return photoSphereDatas;
    }

    /**
     *
     * @return the ID of the first room to be displayed
     * @throws JSONException
     */
    public int getStartingId() throws JSONException {
        JSONObject panoSphereData = getJSONObject(panoSphereDatasTag);
        return panoSphereData.getInt(startingIdTag);
    }

    /**
     *
     * @return the starting Url (first image to be displayed)
     * @throws JSONException
     */
    public String getStartingUrl() throws JSONException {
        JSONObject panoSphereData = getJSONObject(panoSphereDatasTag);
        return panoSphereData.getString(startingUrlTag);
    }


    /**
     * used to retrieve information from the PanoramaRooms list 's elements
     *
     * @param photoSphereObject one entry of the PanoramaRooms list
     * @return a photosphere data with the parsed data
     * @throws JSONException
     */
    private static PhotoSphereData parsePhotoSphereData(JSONObject photoSphereObject) throws JSONException {

        List<AngleMapping> neighborsList = new ArrayList<>();
        JSONArray neighborsJSONArray = photoSphereObject.getJSONArray(neighborsListTag);

        PhotoSphereData.Builder builder = new PhotoSphereData.Builder(photoSphereObject.getInt(idTag));
        PanoramaComponentType[] typeValues = PanoramaComponentType.values();

        if (neighborsJSONArray != null) {
            for (int i = 0; i < neighborsJSONArray.length(); i++) {

                switch (typeValues[neighborsJSONArray.getJSONObject(i).getInt(typeTag)]) {
                    case TRANSITION:
                        neighborsList.add(new TransitionObject(
                                new Tuple<>(
                                        neighborsJSONArray.getJSONObject(i).getDouble(thetaTag),
                                        neighborsJSONArray.getJSONObject(i).getDouble(phiTag)
                                ),
                                neighborsJSONArray.getJSONObject(i).getInt(idTag),
                                neighborsJSONArray.getJSONObject(i).getString(urlTag)));
                        break;

                    case INFORMATION:

                }
            }
        }

        builder.setNeighborsList(neighborsList);

        return builder.build();
    }
}
