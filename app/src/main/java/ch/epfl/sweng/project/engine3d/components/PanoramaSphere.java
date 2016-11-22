package ch.epfl.sweng.project.engine3d.components;

import android.graphics.Bitmap;
import android.util.Log;

import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.materials.textures.TextureManager;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.util.ObjectColorPicker;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.project.data.AngleMapping;


/**
 * Represent the panoramic Spere containing the image.
 */
public final class PanoramaSphere extends Sphere {

    public static final String TEXTURE_TAG = "PhotoTexture";
    private static final int INITIAL_COMPONENTLIST_SIZE = 10;
    private static final String TAG = "PanoramaSphere";
    private static final Vector3 INITIAL_POS = new Vector3(0, 0, 0);
    private final List<PanoramaObject> mComponentList;
    private Texture mPhotoTexture;
    private int mComponentIndex = 0;

    /**
     * A PanoramaSphere is a rajawali 3d object that stores a panorama image
     * in the form of a texture. Additional UI components should be attached as
     * children of this object. Upon creation of a panorama sphere a unique texture is
     * created and registered with the material. The texture contains a bitmap as well as
     * an ID which is used by the texture manager to keep track of all texture used by the application. The
     * bitmap associated with a texture can change but the texture instance remains unique. Therefore subsequent
     * panorama photo changes should call the texture setter as it will handle the bitmap replacement
     */
    public PanoramaSphere() {
        super(100, 48, 48);

        mComponentList = new ArrayList<>(INITIAL_COMPONENTLIST_SIZE);

        setBackSided(true);
        setPosition(INITIAL_POS);
        mMaterial = new Material();
        mMaterial.setColor(0);

        mPhotoTexture = new Texture(TEXTURE_TAG);

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
    public void detachPanoramaComponents(ObjectColorPicker p) {
        for (PanoramaObject pc : mComponentList) {
            pc.unregisterComponentFromPicker(p);
            pc.detachFromParentAndDie();
            mComponentIndex--;
        }
        mComponentList.clear();
    }

    public void detachPanoramaComponent(ObjectColorPicker p, PanoramaObject panoramaObject) {
        panoramaObject.unregisterComponentFromPicker(p);
        mComponentList.remove(panoramaObject);
        mComponentIndex--;
        panoramaObject.detachFromParentAndDie();
    }

    /**
     * Call this method to replace the currently shown panorama. The
     * method will then call the texture manager and update the bitmap
     * associated with its texture
     *
     * @param b A bitmap file that contain the panorama photograph
     */
    public void setPhotoTexture(Bitmap b) {
        mPhotoTexture.setBitmap(b);
        TextureManager.getInstance().replaceTexture(mPhotoTexture);
    }

    public void attachPanoramaComponents(Iterable<AngleMapping> l, ObjectColorPicker p) {
        Log.d(TAG, "Call to attach panorama");
        for (AngleMapping am : l) {

            PanoramaObject panoramaObject = am.toPanoramaObject();

            panoramaObject.registerComponentAtPicker(p);
            panoramaObject.setPickingColor(mComponentIndex);

            mComponentIndex++;
            addChild(panoramaObject);
            mComponentList.add(panoramaObject);
        }
    }



    public void attachPanoramaComponent(PanoramaObject panoramaObject, ObjectColorPicker p) {
        Log.d(TAG, "Call to attach panorama");

        panoramaObject.registerComponentAtPicker(p);
        panoramaObject.setPickingColor(mComponentIndex);

        mComponentIndex++;
        addChild(panoramaObject);
        mComponentList.add(panoramaObject);
    }
}
