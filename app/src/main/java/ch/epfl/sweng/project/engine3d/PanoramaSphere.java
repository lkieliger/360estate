package ch.epfl.sweng.project.engine3d;

import android.graphics.Bitmap;
import android.util.Log;

import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.materials.textures.TextureManager;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Sphere;


/**
 * Represent the panoramic Spere containing the image.
 */
final class PanoramaSphere extends Sphere {

    public static final String TEXTURE_TAG = "PhotoTexture";
    private static final String TAG = "PanoramaSphere";
    private static final Vector3 INITIAL_POS = new Vector3(0, 0, 0);
    private Texture mPhotoTexture;

    /**
     * A PanoramaSphere is a rajawali 3d object that stores a panorama image
     * in the form of a texture. Additional UI components should be attached as
     * children of this object. Upon creation of a panorama sphere a unique texture is
     * created and registered with the material. The texture contains a bitmap as well as
     * an ID which is used by the texture manager to keep track of all texture used by the application. The
     * bitmap associated with a texture can change but the texture instance remains unique. Therefore subsequent
     * panorama photo changes should call the texture setter as it will handle the bitmap replacement
     *
     * @param b A bitmap file that contains the panorama photograph
     */
    PanoramaSphere(Bitmap b) {
        super(100, 48, 48);

        setBackSided(true);
        setPosition(INITIAL_POS);
        mMaterial = new Material();
        mMaterial.setColor(0);
        mMaterial.enableLighting(false);

        mPhotoTexture = new Texture(TEXTURE_TAG, b);

        try {
            mMaterial.addTexture(mPhotoTexture);
        } catch (ATexture.TextureException e) {
            Log.e(TAG, e.getMessage());
        }

        setMaterial(mMaterial);
    }

    /**
     * Call this method to dissociate all UI components that were previously defined as children of the panorama sphere
     */
    void removeAllChild() {
        mChildren.clear();
    }

    /**
     * Call this method to replace the currently shown panorama. The
     * method will then call the texture manager and update the bitmap
     * associated with its texture
     *
     * @param b A bitmap file that contain the panorama photograph
     */
    void setPhotoTexture(Bitmap b) {
        mPhotoTexture.setBitmap(b);
        TextureManager.getInstance().replaceTexture(mPhotoTexture);
    }

    void attachPanoramaComponent(PanoramaComponent component) {

    }


}
