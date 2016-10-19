package ch.epfl.sweng.project.data;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.project.util.Tuple;


public class HouseManager {

    private final Map<Integer,Map<Tuple<Double,Double>, Tuple<Integer,String>>> map;

    public HouseManager(Map<Integer, Map<Tuple<Double, Double>, Tuple<Integer, String>>> map) {
        this.map = map;
    }

    public Map<Integer, Map<Tuple<Double, Double>, Tuple<Integer, String>>> getMap() {
        return map;
    }


    public static HouseManager reconstruct(){
        final Map<Integer,Map<Tuple<Double,Double>, Tuple<Integer,String>>> resultMap = new HashMap<>();

        ParseQuery<OrientationMapping> query = ParseQuery.getQuery("OrientationMapping");
        query.findInBackground(new FindCallback<OrientationMapping>() {
            public void done(List<OrientationMapping> objects, ParseException e) {
                if (e == null) {
                    Log.d("DataMgmt", "Retrieved " + objects.size() + " photosphere");

                    for (OrientationMapping p: objects
                            ) {
                        Map<Tuple<Double,Double>,Tuple<Integer,String>> t = p.getMapping();
                        resultMap.put(p.getId(),t);
                    }

                } else {
                    Log.d("DataMgmt", "Error: " + e.getMessage());
                }
            }
        });

        return new HouseManager(resultMap);
    }
}
