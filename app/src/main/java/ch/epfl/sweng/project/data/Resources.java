package ch.epfl.sweng.project.data;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Resources")
public class Resources extends ParseObject {

    private static final String PICTURES_LIST = "picturesList";
    private static final String DESCRIPTION = "description";


    public Resources() {
    }


    public void setDescription(String desc) {
        put(DESCRIPTION, desc);
    }

    public void setPictures(List<String> picturesUrlList) {
        JSONArray urlArray = new JSONArray(picturesUrlList);
        put(PICTURES_LIST, urlArray);
    }


    public void setNeighborsList(List<PhotoSphereData> photoSphereList) {
        JSONArray neighborsArray = new JSONArray();

        for (PhotoSphereData p : photoSphereList) {
            neighborsArray.put(p.getNeighborObject());
        }

        put("testArrayNeighbors", neighborsArray);
    }

    public String getDescription() {
        return getString(DESCRIPTION);
    }

    public List<String> getPicturesList() throws JSONException {
        JSONArray urlArray = getJSONArray(PICTURES_LIST);
        List<String> picturesList = new ArrayList<>();

        for (int i = 0; i < urlArray.length(); i++) {
            picturesList.add(urlArray.getString(i));
        }

        return picturesList;
    }


}
