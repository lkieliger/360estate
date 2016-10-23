package ch.epfl.sweng.project.data;

import android.support.compat.BuildConfig;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.project.util.Tuple;


@ParseClassName("OrientationMapping")
public class OrientationMapping extends ParseObject {

    private static final String TAG = "OrientationMapping";

    public OrientationMapping() {
    }

    public void setId(int id) {
        put("id", id);
    }

    private void setMapping(List<Tuple<Tuple<Double, Double>, Tuple<String, Integer>>> list) {
        try {
            JSONArray temp = new JSONArray();
            for (Tuple<Tuple<Double, Double>, Tuple<String, Integer>> elem : list) {
                JSONObject angleMapping = new JSONObject();
                angleMapping.put("theta", elem.getX().getX());
                angleMapping.put("phi", elem.getX().getY());
                angleMapping.put("url", elem.getY().getX());
                angleMapping.put("id", elem.getY().getY());
                temp.put(angleMapping);
            }
            put("angleMapping", temp);
        } catch (JSONException e) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "JSONException:" + e.getMessage());
            }
        }
    }

    public void setMapping(Map<Tuple<Double, Double>, Tuple<String, Integer>> map) {

        List<Tuple<Tuple<Double, Double>, Tuple<String, Integer>>> list = new ArrayList<>();
        for (Tuple<Double, Double> elem : map.keySet()) {
            list.add(new Tuple<>(elem, map.get(elem)));
        }
        setMapping(list);
    }


    public int getId() {
        return getInt("id");
    }

    public Map<Tuple<Double, Double>, Tuple<Integer, String>> getMapping() {
        JSONArray jsonArray = getJSONArray("angleMapping");
        Map<Tuple<Double, Double>, Tuple<Integer, String>> resultMap = new HashMap<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    resultMap.put(new Tuple<>(
                                    jsonArray.getJSONObject(i).getDouble("theta"),
                                    jsonArray.getJSONObject(i).getDouble("phi")),
                            new Tuple<>(jsonArray.getJSONObject(i).getInt("id"),
                                    jsonArray.getJSONObject(i).getString("url")));
                } catch (JSONException e) {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "JSONException:" + e.getMessage());
                    }
                }
            }
        }
        return resultMap;
    }
}
