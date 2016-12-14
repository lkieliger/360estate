package ch.epfl.sweng.project.engine3d.components;

import android.util.Log;

import org.rajawali3d.math.vector.Vector3;

import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.engine3d.PanoramaRenderer;


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

    public void setPanoramaInfoDisplay(PanoramaInfoDisplay panoramaInfoDisplay) {
        this.panoramaInfoDisplay = panoramaInfoDisplay;
    }

    public void setFocused(boolean focused) {
        isFocused = focused;
    }

    @Override
    public void reactWith(PanoramaRenderer p) {
        Log.d("toto", "titi");
        if (!isDisplay) {
            p.displayText(textInfo, theta, this);
            p.rotateDisplayInfoObject(this);
            isDisplay = true;
        } else {
            if (isFocused) {
                p.deleteInfo(panoramaInfoDisplay);
                p.zoomOutAndRotate(theta, this);
                isFocused = false;
                panoramaInfoDisplay.setFocused(false);
            } else {
                p.deleteInfo(panoramaInfoDisplay);
                p.rotateDisplayInfoObject(this);
            }
            isDisplay = false;
        }
    }

    public boolean isDisplay() {
        return isDisplay;
    }
}
