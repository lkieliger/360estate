package ch.epfl.sweng.project.data;


import org.json.JSONException;
import org.json.JSONObject;

import ch.epfl.sweng.project.engine3d.components.PanoramaComponentType;
import ch.epfl.sweng.project.util.Tuple;

import static ch.epfl.sweng.project.data.JSONTags.idTag;
import static ch.epfl.sweng.project.data.JSONTags.urlTag;

public class TransitionObject extends AngleMapping {

    private final int mId;
    private final String mUrl;
    private final PanoramaComponentType mType = PanoramaComponentType.TRANSITION;


    public TransitionObject(Tuple<Double, Double> extThetaPhi, int extId, String extUrl) {
        super(extThetaPhi);
        mId = extId;
        mUrl = extUrl;
    }

    public TransitionObject(Double extTheta, Double extPhi, int extId, String extUrl) {
        super(extTheta, extPhi);
        mId = extId;
        mUrl = extUrl;
    }

    public int getId() {
        return mId;
    }

    public String getUrl() {
        return mUrl;
    }

    @Override
    public PanoramaComponentType getType() {
        return mType;
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonTransition = super.toJSONObject();
        jsonTransition.put(idTag, getId());
        jsonTransition.put(urlTag, getUrl());
        return jsonTransition;
    }

    @Override
    public boolean equals(Object obj) {
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        TransitionObject transitionObject = (TransitionObject) obj;
        return getId() == transitionObject.getId() &&
                getUrl().equals(transitionObject.getUrl()) &&
                (Double.compare(getTheta(), transitionObject.getTheta()) == 0) &&
                (Double.compare(getPhi(), transitionObject.getPhi()) == 0);
    }

    @Override
    public int hashCode() {
        int result = mId;
        result = 31 * result + (mUrl != null ? mUrl.hashCode() : 0);
        result = 31 * result + getThetaPhi().hashCode();
        return result;
    }
}
