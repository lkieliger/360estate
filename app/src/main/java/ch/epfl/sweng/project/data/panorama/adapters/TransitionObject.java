package ch.epfl.sweng.project.data.panorama.adapters;


import org.json.JSONException;
import org.json.JSONObject;

import ch.epfl.sweng.project.engine3d.components.PanoramaComponentType;
import ch.epfl.sweng.project.engine3d.components.PanoramaObject;
import ch.epfl.sweng.project.engine3d.components.PanoramaTransitionObject;
import ch.epfl.sweng.project.util.Tuple;

import static ch.epfl.sweng.project.data.parse.objects.JSONTags.idTag;
import static ch.epfl.sweng.project.data.parse.objects.JSONTags.typeTag;
import static ch.epfl.sweng.project.data.parse.objects.JSONTags.urlTag;

public final class TransitionObject extends SpatialData {

    private final int mId;
    private final String mUrl;
    private final PanoramaComponentType mType = PanoramaComponentType.TRANSITION;

    /**
     *
     * @param extThetaPhi the theta, phi values
     * @param extId the id of the referenced node
     * @param extUrl the url of the referenced node
     */
    public TransitionObject(Tuple<Double, Double> extThetaPhi, int extId, String extUrl) {
        super(extThetaPhi);
        mId = extId;
        mUrl = extUrl;
    }

    /**
     *
     * @param extTheta the theta value
     * @param extPhi the phi value
     * @param extId the id of the referenced node
     * @param extUrl the url of the referenced node
     */
    public TransitionObject(Double extTheta, Double extPhi, int extId, String extUrl) {
        this(new Tuple<>(extTheta, extPhi), extId, extUrl);
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

    /**
     *
     * @return the transition object in its JSON representation
     * @throws JSONException
     */
    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonTransition = super.toJSONObject();
        jsonTransition.put(idTag, getId());
        jsonTransition.put(urlTag, getUrl());
        jsonTransition.put(typeTag, getType().ordinal());
        return jsonTransition;
    }

    /**
     *
     * @return the Panorama transition object created with this object's attributes
     */
    @Override
    public PanoramaObject toPanoramaObject() {
        Tuple<Double, Double> thetaPhi = getThetaPhi();
        return new PanoramaTransitionObject(thetaPhi.getX(), thetaPhi.getY(), getId(), getUrl());
    }

    /**
     *
     * @param obj the object to compare to
     * @return true iff the two objects are equal
     */
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

    /**
     *
     * @return the hashcode of this object
     */
    @Override
    public int hashCode() {
        int result = mId;
        result = 31 * result + (mUrl != null ? mUrl.hashCode() : 0);
        result = 31 * result + super.hashCode();
        return result;
    }
}
