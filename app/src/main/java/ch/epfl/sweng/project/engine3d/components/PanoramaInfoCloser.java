package ch.epfl.sweng.project.engine3d.components;

import org.rajawali3d.math.vector.Vector3;

import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.engine3d.PanoramaRenderer;


public class PanoramaInfoCloser extends PanoramaObject {

    private static final String TAG = "PanoramaInfoCloser";
    private static final int ICON_CLOSE = R.drawable.close_tex;
    private static final int ICON_COLOR = 0x00ff0000;

    private final PanoramaInfoDisplay panoramaInfoDisplay;


    public PanoramaInfoCloser(double theta, double phi, int width, int height, PanoramaInfoDisplay panoramaInfoDisplay) {
        super(theta, phi, width, height, 40);
        this.panoramaInfoDisplay = panoramaInfoDisplay;
        setIcon(TAG, ICON_CLOSE, ICON_COLOR);
        enableLookAt();
        setLookAt(new Vector3(0, 0, 0));
    }


    @Override
    public void reactWith(PanoramaRenderer p) {
        p.deleteInfo(panoramaInfoDisplay, this);
    }
}
