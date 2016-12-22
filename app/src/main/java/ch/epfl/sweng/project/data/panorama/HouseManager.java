package ch.epfl.sweng.project.data.panorama;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.sweng.project.data.panorama.adapters.SpatialData;

/**
 * House Manager represent the interaction of the different rooms (i.e
 * {@link ch.epfl.sweng.project.engine3d.components.PanoramaSphere}) of the all house using the logic of a graph.
 * It contain a starting url that will be used to load the first panoramaSphere image. The id, is used to load all
 * the neighbors (i.e {@link ch.epfl.sweng.project.engine3d.components.PanoramaTransitionObject}) on the panoramaSphere.
 * It contain also a SparseArray ( an optimized map from integer to objects), that will be used to stock the "graph".
 * The SparseArray will be used to map an Id to an list of {@link SpatialData} representing the neighbors of the id.
 */
public class HouseManager {

    private final SparseArray<List<SpatialData>> sparseArray;
    private final String startingUrl;
    private final int startingId;

    public HouseManager(SparseArray<List<SpatialData>> extSparseArray, int extStartingId, String extStartingUrl) {
        sparseArray = extSparseArray;
        startingUrl = extStartingUrl;
        startingId = extStartingId;
    }

    /**
     * Return the data associated with a specific id passed in argument. It will return every SpatialData that can be
     * reached from the id.
     *
     * @param id the Id to get the data.
     * @return the list of all SpatialData reachable from the id.
     */
    public List<SpatialData> getAttachedDataFromId(int id) {
        if(sparseArray != null && sparseArray.size() != 0) {
            return Collections.unmodifiableList(new ArrayList<>(sparseArray.get(id)));
        }
        return new ArrayList<>();
    }

    public int getStartingId() {
        return startingId;
    }

    public String getStartingUrl() {
        return startingUrl;
    }
}
