package ch.epfl.sweng.project.data;

import android.support.compat.BuildConfig;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.epfl.sweng.project.util.Tuple;


@ParseClassName("Neighbors")
public class Neighbors extends ParseObject {

    private static final String TAG = "Neighbors";

    public Neighbors() {
    }

    public void setId(int id) {
        put("id", id);
    }

    private void setList(Iterable<AngleMapping> list) {
        try {
            JSONArray temp = new JSONArray();
            for (AngleMapping elem : list) {
                JSONObject angleMapping = new JSONObject();
                angleMapping.put("theta", elem.getTheta());
                angleMapping.put("phi", elem.getPhi());
                angleMapping.put("id", elem.getId());
                angleMapping.put("url", elem.getUrl());
                temp.put(angleMapping);
            }
            put("neighborsList", temp);
        } catch (JSONException e) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "JSONException:" + e.getMessage());
            }
        }
    }

    public int getId() {
        return getInt("id");
    }

    public List<AngleMapping> getNeighborsList() {
        JSONArray jsonArray = getJSONArray("neighborsList");
        List<AngleMapping> resultList = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    resultList.add(new AngleMapping(
                            new Tuple<>(
                                    jsonArray.getJSONObject(i).getDouble("theta"),
                                    jsonArray.getJSONObject(i).getDouble("phi")
                            ),
                            jsonArray.getJSONObject(i).getInt("id"),
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
}
