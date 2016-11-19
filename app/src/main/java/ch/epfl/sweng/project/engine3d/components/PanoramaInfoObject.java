package ch.epfl.sweng.project.engine3d.components;

import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.engine3d.PanoramaRenderer;


public final class PanoramaInfoObject extends PanoramaObject {

    private static final String TEXTURE_TAG = "PanoramaInfoObject";
    private static final int ICON_INDEX = R.drawable.info_tex;
    private final String textInfo;

    PanoramaInfoObject(double theta, double phi, String textInfo) {
        super(theta, phi, TEXTURE_TAG, ICON_INDEX);
        this.textInfo = textInfo;
    }


    @Override
    public void reactWith(PanoramaRenderer p) {
        p.displayText(textInfo);
    }
}
