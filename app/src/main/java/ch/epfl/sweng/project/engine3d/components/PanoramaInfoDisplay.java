package ch.epfl.sweng.project.engine3d.components;

import android.graphics.Bitmap;
import android.util.Log;

import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.vector.Vector3;

import ch.epfl.sweng.project.engine3d.PanoramaRenderer;

/**
 * A text plane for displaying a message about an element of the panorama picture
 */
public class PanoramaInfoDisplay extends PanoramaObject {

    private static final String TAG = "PanoramaInfoDisplay";
    private final double theta;
    private boolean isFocused;
    private PanoramaInfoObject panoramaInfoObject;

    /**
     * Creates a {@link PanoramaInfoDisplay} at the specified location, with the specified dimensions as well as with
     * the specidied text bitmap.
     *
     * @param theta              azimuth in spherical coordinates of the piece of information (from 0 which is along the x axis to 2
     *                           pi which is again along the x axis)
     * @param phi                inclination in spherical coordinates of the piece of information (0 is up, pi is down)
     * @param width
     * @param height
     * @param bitmap             the text in the form of a bitmap so it can be displayed as an element in the 3D scene
     * @param panoramaInfoObject the associated object which stores the message to display
     */
    public PanoramaInfoDisplay(double theta, double phi, int width, int height, Bitmap bitmap
            , PanoramaInfoObject panoramaInfoObject) {
        super(theta, phi, width, height, 40);
        this.theta = theta;
        this.panoramaInfoObject = panoramaInfoObject;

        setLookAt(new Vector3(0, 0, 0));
        Material material = new Material();
        isFocused = false;
        material.setColor(0);
        Texture texture = new Texture(TAG, bitmap);

        try {
            material.addTexture(texture);
        } catch (ATexture.TextureException e) {
            Log.e(TAG, e.getMessage());
        }
        setMaterial(material);
    }

    @Override
    public void reactWith(PanoramaRenderer p) {
        if (isFocused) {
            p.zoomOut(theta);
            isFocused = false;
        } else {
            p.zoomOnText(theta, getX(), getZ());
            isFocused = true;
        }
        panoramaInfoObject.setFocused(isFocused);
    }

    void setFocused(boolean focused) {
        isFocused = focused;
    }

    public void setPanoramaInfoObject(PanoramaInfoObject panoramaInfoObject) {
        this.panoramaInfoObject = panoramaInfoObject;
    }
}
