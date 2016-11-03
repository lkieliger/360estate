package ch.epfl.sweng.project.data;

import android.util.SparseArray;

import java.util.List;

//TODO: Add documentation for this object
public class HouseManager {

    private final SparseArray<List<AngleMapping>> sparseArray;
    private final String startingUrl;
    private final int startingId;

    public HouseManager(SparseArray<List<AngleMapping>> extSparseArray, int extStartingId, String extStartingUrl ) {
        sparseArray = extSparseArray;
        startingUrl = extStartingUrl;
        startingId = extStartingId;
    }

    //TODO: This is super super bad, never return a reference to a whole array
    //See for defensive copying and maybe refine class/method logic
    public SparseArray<List<AngleMapping>> getSparseArray() {
        return sparseArray;
    }

    public int getStartingId() {
        return startingId;
    }

    public String getStartingUrl() {
        return startingUrl;
    }


    //TODO: Determine if this code is necessary
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
