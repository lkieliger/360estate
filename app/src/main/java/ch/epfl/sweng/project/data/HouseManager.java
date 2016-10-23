package ch.epfl.sweng.project.data;

import android.util.Log;
import android.util.SparseArray;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HouseManager {

    private final SparseArray<List<AngleMapping>> sparseArray;
    private final String startingUrl;
    private final int startingId;

    public HouseManager(SparseArray<List<AngleMapping>> extSparseArray, int extStartingId, String extStartingUrl ) {
        sparseArray = extSparseArray;
        startingUrl = extStartingUrl;
        startingId = extStartingId;
    }

    public SparseArray<List<AngleMapping>> getSparseArray() {
        return sparseArray;
    }

    public int getStartingId() {
        return startingId;
    }

    public String getStartingUrl() {
        return startingUrl;
    }
/*
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
    */
}
