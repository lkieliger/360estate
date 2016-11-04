package ch.epfl.sweng.project.data;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * House Manager represent the interaction of the different rooms (i.e
 * {@link ch.epfl.sweng.project.engine3d.PanoramaSphere}) of the all house using the logic of a graph.
 * It contain a starting url that will be used to load the first panoramaSphere image. The id, is used to load all
 * the neighbors ( i.e {@link ch.epfl.sweng.project.engine3d.PanoramaTransitionObject}) on the panoramaSphere.
 * It contain also a SparseArray ( an optimized map from integer to objects), that will be used to stock the "graph".
 * The SparseArray will be used to map an Id to an list of {@link AngleMapping} representing the neighbors of the id.
 */
public class HouseManager {

    private final SparseArray<List<AngleMapping>> sparseArray;
    private final String startingUrl;
    private final int startingId;

    public HouseManager(SparseArray<List<AngleMapping>> extSparseArray, int extStartingId, String extStartingUrl ) {
        sparseArray = extSparseArray;
        startingUrl = extStartingUrl;
        startingId = extStartingId;
    }

    public List<AngleMapping> getNeighborsFromId(int id){
        return new ArrayList<>(sparseArray.get(id));
    }

    public int getStartingId() {
        return startingId;
    }

    public String getStartingUrl() {
        return startingUrl;
    }
}
