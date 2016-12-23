package ch.epfl.sweng.project.engine3d.components;

import android.animation.ArgbEvaluator;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.vector.Vector3;

import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.engine3d.PanoramaRenderer;
import ch.epfl.sweng.project.engine3d.StringAdapter;

/**
 * Represents information about an element in the panorama picture. This objects stores the informative text as well
 * as the position in spherical coordinates of the location of the piece of information with respect to the panoramic
 * picture
 */
public class PanoramaInfoObject extends PanoramaObject {

    public static final int ROTATION_RATE = 15;
    private static final String TEXTURE_TAG = "PanoramaInfoObject";
    private static final int ICON_INDEX = R.drawable.info_tex;
    private static final int WIDTH = 6;
    private static final int HEIGHT = 6;
    private static final int CONTOUR_SIZE = 10;
    private static final int MARGIN_SIZE = 10;
    private static final int TEXT_SIZE = 24;
    private static final int WIDTH_PIXELS = 512;
    private static final int HEIGHT_LIMIT = 512;
    private static final int WIDTH_INFO_DISPLAY = 35;
    private static final int CLOSE_COLOR = Color.rgb(255, 25, 25);
    private static final double VERTICAL_TEXT_POS = Math.PI / 2.0;
    private final double theta;
    private final StringAdapter stringAdapter;
    private final Bitmap textBitmap;
    private final int heightInfoDisplay;
    private boolean isDisplayed;
    private boolean isOpen;
    private PanoramaInfoDisplay panoramaInfoDisplay;
    private double currentAngle;
    private double targetAngle;
    private int startingColor;
    private int finishColor;
    private float rotationPercent;

    /**
     * Builds a PanoramaInfo object with the given specifications:
     *
     * @param theta    azimuthal angle in spherical coordinates of the piece of information (from 0 which is along
     *                 the x axis to 2
     *                 pi which is again along the x axis)
     * @param phi      inclination in spherical coordinates of the piece of information (0 is up, pi is down)
     * @param textInfo the message that should be stored by this object
     */
    public PanoramaInfoObject(double theta, double phi, String textInfo) {
        super(theta, phi, WIDTH, HEIGHT, 40);
        enableLookAt();
        setLookAt(new Vector3(0, 0, 0));
        setIcon(TEXTURE_TAG, ICON_INDEX);
        this.theta = theta;
        isDisplayed = false;

        stringAdapter = new StringAdapter(textInfo);
        textBitmap = stringAdapter.textToBitmap(TEXT_SIZE, WIDTH_PIXELS, CONTOUR_SIZE, MARGIN_SIZE, HEIGHT_LIMIT);
        heightInfoDisplay = getSizeFromPixels(textBitmap.getHeight());
    }

    @Override
    public void reactWith(PanoramaRenderer p) {
        if (!isDisplayed) {
            panoramaInfoDisplay = new PanoramaInfoDisplay();
            p.getPanoramaSphere().attachPanoramaComponent(panoramaInfoDisplay);
            p.rotatePanoramaInfoObject(this);
            isDisplayed = true;

        } else {
            if (panoramaInfoDisplay.infoTextIsFocused) {
                p.zoomOutAndRotate(theta, this);
                panoramaInfoDisplay.infoTextIsFocused = false;
            } else {
                p.rotatePanoramaInfoObject(this);
            }
            p.getPanoramaSphere().detachPanoramaComponent(panoramaInfoDisplay);
            isDisplayed = false;
        }
    }

    public void setRotationAndColorTarget(){
        if(!isOpen){
            startingColor = TEXTURE_COLOR;
            finishColor = CLOSE_COLOR;
            isOpen = true;
        } else {
            startingColor = CLOSE_COLOR;
            finishColor = TEXTURE_COLOR;
            isOpen = false;
        }
        targetAngle = currentAngle + 180 / 4.0;
        rotationPercent = 0;
    }

    public void rotateAndColor(){
        double angle = 180.0 / (4.0 * ROTATION_RATE);

        //Take the Vector from the object to the origin (0 - ObjectCoordinates )
        rotate(-getX(), -getY(), -getZ(), angle);
        currentAngle += angle;
        ArgbEvaluator argbEvaluator = new ArgbEvaluator();
        rotationPercent += 1.0 / ROTATION_RATE;
        setColor((Integer) argbEvaluator.evaluate(rotationPercent, startingColor, finishColor));
    }

    public boolean rotationIsFinished(){
        return currentAngle >= targetAngle;
    }

    public boolean isDisplayed() {
        return isDisplayed;
    }

    /**
     * A magic formula that maps pixels into the units used in rajawali.
     *
     * @param pixels
     * @return
     */
    private int getSizeFromPixels(int pixels) {
        int i = ((int) (Math.log(pixels) / Math.log(2)) - 7) * 15;
        if (i <= 0) {
            return 10;
        }
        return i;
    }

    private class PanoramaInfoDisplay extends PanoramaObject {

        private static final String TAG = "PanoramaInfoDisplay";
        private static final double VERTICAL_OFFSET = 7;
        private boolean infoTextIsFocused;

        public PanoramaInfoDisplay() {
            super(theta, VERTICAL_TEXT_POS, WIDTH_INFO_DISPLAY, heightInfoDisplay, 40);

            setLookAt(new Vector3(0, 0, 0));
            setY(getY() + VERTICAL_OFFSET);
            Material material = new Material();
            infoTextIsFocused = false;
            material.setColor(0);
            Texture texture = new Texture(TAG, textBitmap);

            try {
                material.addTexture(texture);
            } catch (ATexture.TextureException e) {
                Log.e(TAG, e.getMessage());
            }
            setMaterial(material);
        }

        @Override
        public void reactWith(PanoramaRenderer p) {
            if (infoTextIsFocused) {
                p.zoomOut(theta);
                infoTextIsFocused = false;
            } else {
                p.zoomOnText(theta, getX(), getZ());
                infoTextIsFocused = true;
            }
        }
    }
}
