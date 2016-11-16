package ch.epfl.sweng.project.data;


import org.json.JSONException;
import org.json.JSONObject;

import ch.epfl.sweng.project.engine3d.components.PanoramaComponentType;
import ch.epfl.sweng.project.engine3d.components.PanoramaObject;
import ch.epfl.sweng.project.engine3d.components.PanoramaTransitionObject;
import ch.epfl.sweng.project.util.Tuple;

import static ch.epfl.sweng.project.data.JSONTags.idTag;
import static ch.epfl.sweng.project.data.JSONTags.typeTag;
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
        this(new Tuple<Double, Double>(extTheta, extPhi), extId, extUrl);
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
        jsonTransition.put(typeTag, getType().ordinal());
        return jsonTransition;
    }

    @Override
    public PanoramaObject toPanoramaObject() {
        Tuple<Double, Double> thetaPhi = getThetaPhi();
        return new PanoramaTransitionObject(thetaPhi.getX(), thetaPhi.getY(), getId(), getUrl());
    }

    @Override
    public boolean equals(Object obj) {
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        TransitionObject transitionObject = (TransitionObject) obj;
        return getId() == transitionObject.getId() &&
                getUrl().equals(transitionObject.getUrl()) &&
                super.equals(obj);
    }

    @Override
    public int hashCode() {
        int result = mId;
        result = 31 * result + (mUrl != null ? mUrl.hashCode() : 0);
        result = 31 * result + super.hashCode();
        return result;
    }
}
