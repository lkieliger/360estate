package ch.epfl.sweng.project.data;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.util.Tuple;

import static ch.epfl.sweng.project.data.JSONTags.*;

@ParseClassName("Resources")
public class Resources extends ParseObject {

    private static final String TAG = "Resources class";

    public Resources() {
        // Default constructor needed for Parse objects
    }

    void setDescription(String desc) {
        put(descriptionTag, desc);
    }

    public void setPicturesUrlList(List<String> picturesUrlList) {
        JSONArray urlArray = new JSONArray(picturesUrlList);
        put(picturesListTag, urlArray);
    }


    void setPhotoSphereDatas(Iterable<PhotoSphereData> photoSphereList) {
        JSONArray photoSphereDatas = new JSONArray();
        for (PhotoSphereData p : photoSphereList) {
            photoSphereDatas.put(p.getNeighborObject());
        }

        put(photoSphereDatasTag, photoSphereDatas);
    }

    public String getDescription() {
        return getString(descriptionTag);
    }

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

    public List<PhotoSphereData> getPhotoSphereDatas() throws JSONException {
        JSONArray photoSphereDataArray = getJSONArray(photoSphereDatasTag);
        List<PhotoSphereData> photoSphereDatas = new ArrayList<>(photoSphereDataArray.length());
        for (int i = 0; i < photoSphereDataArray.length(); i++) {
            JSONObject photoSphereObject = (JSONObject) photoSphereDataArray.get(i);
            photoSphereDatas.add(parsePhotoSphereData(photoSphereObject));
        }

        return photoSphereDatas;
    }


    private static PhotoSphereData parsePhotoSphereData(JSONObject photoSphereObject) throws JSONException {

        List<AngleMapping> neighborsList = new ArrayList<>();
        JSONArray neighborsJSONArray = photoSphereObject.getJSONArray(neighborsListTag);

        PhotoSphereData.Builder builder = new PhotoSphereData.Builder(photoSphereObject.getInt(idTag));

        if (neighborsJSONArray != null) {
            for (int i = 0; i < neighborsJSONArray.length(); i++) {

                neighborsList.add(new AngleMapping(
                        new Tuple<>(
                                neighborsJSONArray.getJSONObject(i).getDouble(thetaTag),
                                neighborsJSONArray.getJSONObject(i).getDouble(phiTag)
                        ),
                        neighborsJSONArray.getJSONObject(i).getInt(idTag),
                        neighborsJSONArray.getJSONObject(i).getString(urlTag)));
            }
        }

        builder.setNeighborsList(neighborsList);
        builder.setUrl(photoSphereObject.getString(urlTag));

        return builder.build();
    }


}
