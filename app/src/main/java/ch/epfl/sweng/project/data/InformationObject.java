package ch.epfl.sweng.project.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import ch.epfl.sweng.project.engine3d.components.PanoramaComponentType;
import ch.epfl.sweng.project.engine3d.components.PanoramaInfoObject;
import ch.epfl.sweng.project.engine3d.components.PanoramaObject;
import ch.epfl.sweng.project.util.Tuple;

import static ch.epfl.sweng.project.data.JSONTags.textInfoTag;
import static ch.epfl.sweng.project.data.JSONTags.typeTag;


public class InformationObject extends AngleMapping {

    private final PanoramaComponentType mType = PanoramaComponentType.INFORMATION;
    private final String textInfo;

    public InformationObject(Tuple<Double, Double> extThetaPhi, String textInfo) {
        super(extThetaPhi);
        this.textInfo = textInfo;
    }

    public String getTextInfo() {
        return textInfo;
    }

    @Override
    public PanoramaComponentType getType() {
        return mType;
    }


    @Override
    public PanoramaObject toPanoramaObject() {
        return new PanoramaInfoObject(getTheta(), getPhi(), textInfo);
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonTransition = super.toJSONObject();
        jsonTransition.put(textInfoTag, getTextInfo());
        jsonTransition.put(typeTag, getType().ordinal());
        return jsonTransition;
    }

    @Override
    public boolean equals(Object obj) {
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        InformationObject informationObject = (InformationObject) obj;
        return super.equals(obj) && Objects.equals(informationObject.getTextInfo(), textInfo);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + textInfo.hashCode();
    }
}

