package ch.epfl.sweng.project.data.parse.objects;

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
import ch.epfl.sweng.project.data.panorama.PhotoSphereData;
import ch.epfl.sweng.project.data.panorama.adapters.InformationObject;
import ch.epfl.sweng.project.data.panorama.adapters.SpatialData;
import ch.epfl.sweng.project.data.panorama.adapters.TransitionObject;
import ch.epfl.sweng.project.engine3d.components.PanoramaComponentType;
import ch.epfl.sweng.project.util.Tuple;

import static ch.epfl.sweng.project.data.parse.objects.JSONTags.descriptionTag;
import static ch.epfl.sweng.project.data.parse.objects.JSONTags.idHouseTag;
import static ch.epfl.sweng.project.data.parse.objects.JSONTags.idTag;
import static ch.epfl.sweng.project.data.parse.objects.JSONTags.neighborsListTag;
import static ch.epfl.sweng.project.data.parse.objects.JSONTags.panoSphereDatasTag;
import static ch.epfl.sweng.project.data.parse.objects.JSONTags.panoramaRoomsTag;
import static ch.epfl.sweng.project.data.parse.objects.JSONTags.phiTag;
import static ch.epfl.sweng.project.data.parse.objects.JSONTags.picturesListTag;
import static ch.epfl.sweng.project.data.parse.objects.JSONTags.startingIdTag;
import static ch.epfl.sweng.project.data.parse.objects.JSONTags.startingUrlTag;
import static ch.epfl.sweng.project.data.parse.objects.JSONTags.textInfoTag;
import static ch.epfl.sweng.project.data.parse.objects.JSONTags.thetaTag;
import static ch.epfl.sweng.project.data.parse.objects.JSONTags.titleTag;
import static ch.epfl.sweng.project.data.parse.objects.JSONTags.typeTag;
import static ch.epfl.sweng.project.data.parse.objects.JSONTags.urlTag;

/**
 * ParseObject class that provides the interface for storing objects on the server
 */
@SuppressWarnings("WeakerAccess")
@ParseClassName("Resources")
public final class Resources extends ParseObject {

    private static final String TAG = "Resources class";

    @SuppressWarnings("RedundantNoArgConstructor")
    public Resources() {
        // Default constructor needed for Parse objects
    }

    /**
     * used to retrieve information from the PanoramaRooms list's elements
     *
     * @param photoSphereObject one entry of the PanoramaRooms list
     * @return a photosphere data with the parsed data
     * @throws JSONException
     */
    private static PhotoSphereData parsePhotoSphereData(JSONObject photoSphereObject) throws JSONException {

        List<SpatialData> neighborsList = new ArrayList<>();
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
                        neighborsList.add(new InformationObject(
                                new Tuple<>(
                                        neighborsJSONArray.getJSONObject(i).getDouble(thetaTag),
                                        neighborsJSONArray.getJSONObject(i).getDouble(phiTag)
                                ),
                                neighborsJSONArray.getJSONObject(i).getString(textInfoTag)));
                        break;
                }
            }
        }

        builder.setNeighborsList(neighborsList);

        return builder.build();
    }

    /**
     * @param picturesUrlList the URLs to be displayed in the description activity
     */
    public void setPicturesUrlList(Collection<String> picturesUrlList) {
        JSONArray urlArray = new JSONArray(picturesUrlList);
        put(picturesListTag, urlArray);
    }

    /**
     * updates the content of the Parse object with the parameters values
     * @param photoSphereList the list of PhotoSphere informations (one entry in the list = one room)
     * @param startingId      the Id of the first room
     * @param startingUrl     the Url of the first room
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

    public void setDescription(String desc) {
        put(descriptionTag, desc);
    }

    public String getId() {
        return getString(idHouseTag);
    }

    public void setId(String id) {
        put(idHouseTag, id);
    }

    public String getTitle(){
        return getString(titleTag);
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
     * @return the ID of the first room to be displayed
     * @throws JSONException
     */
    public int getStartingId() throws JSONException {
        JSONObject panoSphereData = getJSONObject(panoSphereDatasTag);
        return panoSphereData.getInt(startingIdTag);
    }

    /**
     * @return the starting Url (first image to be displayed)
     * @throws JSONException
     */
    public String getStartingUrl() throws JSONException {
        JSONObject panoSphereData = getJSONObject(panoSphereDatasTag);
        return panoSphereData.getString(startingUrlTag);
    }
}
