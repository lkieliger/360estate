package ch.epfl.sweng.project.engine3d.components;

import org.rajawali3d.math.vector.Vector3;

import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.engine3d.PanoramaRenderer;


public final class PanoramaInfoObject extends PanoramaObject {

    private static final String TEXTURE_TAG = "PanoramaInfoObject";
    private static final int ICON_INDEX = R.drawable.info_tex;
    private final String textInfo;

    public PanoramaInfoObject(double theta, double phi, String textInfo) {
        super(theta, phi, TEXTURE_TAG, ICON_INDEX);
        this.textInfo = textInfo;
        enableLookAt();
        setLookAt(new Vector3(0, 0, 0));
    }


    @Override
    public void reactWith(PanoramaRenderer p) {
        p.displayText(textInfo);
    }
}
