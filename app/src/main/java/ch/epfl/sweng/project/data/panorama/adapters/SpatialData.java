package ch.epfl.sweng.project.data.panorama.adapters;

import org.json.JSONException;
import org.json.JSONObject;

import ch.epfl.sweng.project.engine3d.components.PanoramaComponentType;
import ch.epfl.sweng.project.engine3d.components.PanoramaObject;
import ch.epfl.sweng.project.util.Tuple;

import static ch.epfl.sweng.project.data.parse.objects.JSONTags.phiTag;
import static ch.epfl.sweng.project.data.parse.objects.JSONTags.thetaTag;


/**
 * This class is used to translate Parse data into code usable by the app. A spatial object contains
 * a pair of angle that is used to locate this object using spherical coordinates.
 */
public abstract class SpatialData {

    private final Tuple<Double, Double> thetaPhi;

    SpatialData(Tuple<Double, Double> extThetaPhi) {
        thetaPhi = new Tuple<>(extThetaPhi.getX(), extThetaPhi.getY());
    }

    public abstract PanoramaComponentType getType();

    public Tuple<Double, Double> getThetaPhi() {
        return thetaPhi;
    }

    public Double getTheta() {
        return thetaPhi.getX();
    }

    public Double getPhi() {
        return thetaPhi.getY();
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonAM = new JSONObject();
        jsonAM.put(thetaTag, getTheta());
        jsonAM.put(phiTag, getPhi());
        return jsonAM;
    }

    public abstract PanoramaObject toPanoramaObject();

    @Override
    public boolean equals(Object obj) {
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        SpatialData thatSpatialData = (SpatialData) obj;
        return thetaPhi.equals(thatSpatialData.getThetaPhi());
    }

    @Override
    public int hashCode() {
        return thetaPhi.hashCode();
    }
}
