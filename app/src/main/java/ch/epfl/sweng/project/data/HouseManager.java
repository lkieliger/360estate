package ch.epfl.sweng.project.data;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.project.util.Tuple;


public class HouseManager {

    private final Map<Integer, List<AngleMapping>> map;

    public HouseManager(Map<Integer, List<AngleMapping>> extMap) {
        this.map = extMap;
    }

    public Map<Integer, List<AngleMapping>> getMap() {
        return map;
    }

    public static HouseManager reconstruct() {
        final Map<Integer, List<AngleMapping>> resultMap = new HashMap<>();

        ParseQuery<Neighbors> query = ParseQuery.getQuery("Neighbors");
        query.findInBackground(new FindCallback<Neighbors>() {
            public void done(List<Neighbors> objects, ParseException e) {
                if (e == null) {
                    Log.d("DataMgmt", "Retrieved " + objects.size() + " neighbors");

                    for (Neighbors n : objects) {
                        resultMap.put(n.getId(), n.getNeighborsList());
                    }
                } else {
                    Log.d("DataMgmt", "Error: " + e.getMessage());
                }
            }
        });

        return new HouseManager(resultMap);
    }
}
