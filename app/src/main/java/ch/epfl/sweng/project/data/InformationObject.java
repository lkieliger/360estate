package ch.epfl.sweng.project.data;

import ch.epfl.sweng.project.engine3d.components.PanoramaComponentType;
import ch.epfl.sweng.project.engine3d.components.PanoramaObject;
import ch.epfl.sweng.project.util.Tuple;

/**
 * Created by Quentin on 19/11/2016.
 */

public class InformationObject extends AngleMapping {

    private final PanoramaComponentType mType = PanoramaComponentType.INFORMATION;
    private final String textInfo;

    InformationObject(Tuple<Double, Double> extThetaPhi, String textInfo) {
        super(extThetaPhi);
        this.textInfo = textInfo;
    }

    @Override
    public PanoramaComponentType getType() {
        return mType;
    }

    @Override
    public PanoramaObject toPanoramaObject() {
        return null;
    }
}
