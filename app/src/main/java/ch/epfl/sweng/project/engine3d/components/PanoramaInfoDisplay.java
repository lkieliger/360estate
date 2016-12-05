package ch.epfl.sweng.project.engine3d.components;

import android.graphics.Bitmap;
import android.util.Log;

import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.vector.Vector3;

import ch.epfl.sweng.project.engine3d.PanoramaRenderer;


public final class PanoramaInfoDisplay extends PanoramaObject {

    private static final String TAG = "PanoramaInfoDisplay";
    private final double theta;
    private boolean isFocused;


    public PanoramaInfoDisplay(double theta, double phi, int width, int height, Bitmap bitmap,
                               int colorIndex) {
        super(theta, phi, width, height, 40);
        this.theta = theta;

        setLookAt(new Vector3(0, 0, 0));
        Material material = new Material();
        isFocused = false;
        material.setColor(0);
        Texture texture = new Texture(TAG, bitmap);
        enableLookAt();

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
            p.zoomOut();
            isFocused = false;
        } else {
            p.zoomOnText(theta, getX(), getZ());
            isFocused = true;
        }
    }

}
