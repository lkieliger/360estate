package ch.epfl.sweng.project.engine3d.components;

import android.graphics.Bitmap;
import android.util.Log;

import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.vector.Vector3;

import ch.epfl.sweng.project.engine3d.PanoramaRenderer;
import ch.epfl.sweng.project.engine3d.StringAdapter;

/**
 * Created by Quentin on 19/11/2016.
 */

public class PanoramaInfoDisplay extends PanoramaObject {

    private static final String TAG = "PanoramaInfoDisplay";

    public PanoramaInfoDisplay(double theta, double phi, int width, int height, String text, int colorIndex) {
        super(theta, phi, width, height);
        setLookAt(new Vector3(0, 0, 0));
        Material material = new Material();

        StringAdapter stringAdapter = new StringAdapter(text);
        Bitmap bitmap = stringAdapter.textAsBitmap(text, colorIndex);

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
    }
}
