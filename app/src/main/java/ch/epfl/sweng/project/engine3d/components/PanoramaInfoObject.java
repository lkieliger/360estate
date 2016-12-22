package ch.epfl.sweng.project.engine3d.components;

import org.rajawali3d.math.vector.Vector3;

import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.engine3d.PanoramaRenderer;

/**
 * Represents information about an element in the panorama picture. This objects stores the informative text as well
 * as the position in spherical coordinates of the location of the piece of information with respect to the panoramic
 * picture
 */
//TODO: fusion this class with InfoDisplay
public class PanoramaInfoObject extends PanoramaObject {

    private static final String TEXTURE_TAG = "PanoramaInfoObject";
    private static final int ICON_INDEX = R.drawable.info_tex;
    private static final int width = 6;
    private static final int height = 6;
    private final String textInfo;
    private final double theta;
    private boolean isDisplay;
    private boolean isFocused;
    private PanoramaInfoDisplay panoramaInfoDisplay = null;

    /**
     * Builds a PanoramaInfo object with the given specifications:
     *
     * @param theta    azimuth in spherical coordinates of the piece of information (from 0 which is along the x axis to 2
     *                 pi which is again along the x axis)
     * @param phi      inclination in spherical coordinates of the piece of information (0 is up, pi is down)
     * @param textInfo the message that should be stored by this object
     */
    public PanoramaInfoObject(double theta, double phi, String textInfo) {
        super(theta, phi, width, height, 40);
        this.textInfo = textInfo;
        enableLookAt();
        setLookAt(new Vector3(0, 0, 0));
        setIcon(TEXTURE_TAG, ICON_INDEX);
        this.theta = theta;
        isDisplay = false;
        isFocused = false;
    }

    /**
     * Associate a text plane with this object
     *
     * @param panoramaInfoDisplay the text plane to associate with this object
     */
    public void setPanoramaInfoDisplay(PanoramaInfoDisplay panoramaInfoDisplay) {
        this.panoramaInfoDisplay = panoramaInfoDisplay;
    }

    public void setFocused(boolean focused) {
        isFocused = focused;
    }

    @Override
    public void reactWith(PanoramaRenderer p) {
        if (!isDisplay) {
            p.displayText(textInfo, theta, this);
            p.rotatePanoramaInfoObject(this);
            isDisplay = true;
        } else {
            if (isFocused) {
                p.deleteInfo(panoramaInfoDisplay);
                p.zoomOutAndRotate(theta, this);
                isFocused = false;
                panoramaInfoDisplay.setFocused(false);
            } else {
                p.deleteInfo(panoramaInfoDisplay);
                p.rotatePanoramaInfoObject(this);
            }
            isDisplay = false;
        }
    }

    public boolean isDisplay() {
        return isDisplay;
    }
}
