package ch.epfl.sweng.project.engine3d;

import android.graphics.Bitmap;
import android.util.Log;

import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.materials.textures.TextureManager;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.scene.Scene;


/**
 * Represent the panoramic Spere containing the image.
 */
class PanoramaSphere extends Sphere implements PanoramaComponent {

    public static final String TEXTURE_TAG = "PhotoTexture";
    private static final String TAG = "PanoramaSphere";
    private static final Vector3 INITIAL_POS = new Vector3(0, 0, 0);
    private Texture mPhotoTexture;
    private Material mPhotoMaterial;
    private Bitmap mBitmap;

    PanoramaSphere(float radius, int segmentsW, int segmentsH, Bitmap b) {
        super(radius, segmentsW, segmentsH);

        setBackSided(true);
        setPosition(INITIAL_POS);

        mBitmap = b;

        mPhotoMaterial = new Material();
        mPhotoMaterial.setColor(0);
        mPhotoMaterial.enableLighting(false);

        mPhotoTexture = new Texture(TEXTURE_TAG, b);

        try {
            mPhotoMaterial.addTexture(mPhotoTexture);
        } catch (ATexture.TextureException e) {
            Log.e(TAG, e.getMessage());
        }

        setMaterial(mPhotoMaterial);
    }

    void removeAllChild(){
        mChildren.clear();
    }

    public void setPhotoTexture(Bitmap b) {
        mPhotoTexture.setBitmap(mBitmap);
        TextureManager.getInstance().replaceTexture(mPhotoTexture);
    }


    @Override
    public void associateToPanoramaScene(Scene s) {
        s.addChild(this);
    }
}
