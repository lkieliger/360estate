package ch.epfl.sweng.project.engine3d.components;

import android.graphics.Color;

import org.rajawali3d.math.vector.Vector3;

import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.engine3d.PanoramaRenderer;


public class PanoramaInfoCloser extends PanoramaObject {

    private static final String TAG = "PanoramaInfoCloser";
    private static final int ICON_CLOSE = R.drawable.close_tex;
    private static final int ICON_COLOR = Color.rgb(25, 102, 127);

    private final PanoramaInfoDisplay panoramaInfoDisplay;
    private final PanoramaInfoObject panoramaInfoObject;
    private final double theta;
    private boolean isFocused;


    public PanoramaInfoCloser(double theta, double phi, int width, int height, PanoramaInfoDisplay panoramaInfoDisplay,
                              PanoramaInfoObject panoramaInfoObject) {
        super(theta, phi, width, height, 40);
        this.panoramaInfoDisplay = panoramaInfoDisplay;
        this.panoramaInfoObject = panoramaInfoObject;
        setIcon(TAG, ICON_CLOSE, ICON_COLOR);
        enableLookAt();
        setLookAt(new Vector3(0, 0, 0));
        isFocused = false;
        this.theta = theta;
    }


    @Override
    public void reactWith(PanoramaRenderer p) {
        panoramaInfoObject.unTrigger();
        p.deleteInfo(panoramaInfoDisplay, this);
        if (isFocused) {
            p.zoomOut(theta);
            isFocused = false;
        }
        panoramaInfoDisplay.setFocused(false);
    }

    void setFocused(boolean focused) {
        isFocused = focused;
    }
}
