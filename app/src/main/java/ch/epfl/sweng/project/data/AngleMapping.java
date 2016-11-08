package ch.epfl.sweng.project.data;

import org.json.JSONException;
import org.json.JSONObject;

import ch.epfl.sweng.project.engine3d.components.PanoramaComponentType;
import ch.epfl.sweng.project.engine3d.components.PanoramaObject;
import ch.epfl.sweng.project.util.Tuple;

import static ch.epfl.sweng.project.data.JSONTags.phiTag;
import static ch.epfl.sweng.project.data.JSONTags.thetaTag;


/**
 * Represent a neighbor of a {@link ch.epfl.sweng.project.engine3d.components.PanoramaSphere}. It maps a certain
 * angle (theta and phi) to a neighbor id and the url of the neighbor image.
 */
public abstract class AngleMapping {

    private final Tuple<Double, Double> thetaPhi;


    AngleMapping(Tuple<Double, Double> extThetaPhi) {
        thetaPhi = extThetaPhi;
    }

    AngleMapping(Double extTheta, Double extPhi) {
        thetaPhi = new Tuple<>(extTheta, extPhi);
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

        AngleMapping thatAngleMapping = (AngleMapping) obj;
        return (Double.compare(getTheta(), thatAngleMapping.getTheta()) == 0) &&
                (Double.compare(getPhi(), thatAngleMapping.getPhi()) == 0);
    }

    @Override
    public int hashCode() {
        return thetaPhi.hashCode();
    }
}
