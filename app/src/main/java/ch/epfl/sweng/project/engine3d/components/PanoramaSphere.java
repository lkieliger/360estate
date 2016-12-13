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

import ch.epfl.sweng.project.data.panorama.adapters.SpatialData;
import ch.epfl.sweng.project.engine3d.StringAdapter;


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
        Log.d(TAG, "Call to detach component");

        int index = mComponentList.indexOf(panoramaObject);
        panoramaObject.setPickingColor(-1);

        for (int i = index; i < mComponentList.size(); i++) {
            mComponentList.get(i).unregisterComponentFromPicker(p);
            mComponentIndex--;
        }

        mComponentList.remove(panoramaObject);
        panoramaObject.detachFromParentAndDie();

        for (int i = index; i < mComponentList.size(); i++) {
            mComponentList.get(i).registerComponentAtPicker(p, mComponentIndex);
            mComponentIndex++;
        }
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

    public void attachPanoramaComponents(Iterable<SpatialData> l, ObjectColorPicker p) {
        Log.d(TAG, "Call to attach panorama");
        for (SpatialData am : l) {
            PanoramaObject panoramaObject = am.toPanoramaObject();
            attachPanoramaComponent(panoramaObject, p);
        }
    }

    public void attachPanoramaComponent(PanoramaObject panoramaObject, ObjectColorPicker p) {

        Log.d(TAG, "Call to attach component");
        panoramaObject.registerComponentAtPicker(p, mComponentIndex);

        mComponentIndex++;
        addChild(panoramaObject);
        mComponentList.add(panoramaObject);
    }

    public void setTextToDisplay(String textInfo, double theta, PanoramaInfoObject panoramaInfoObject,
                                 ObjectColorPicker picker) {

        StringAdapter stringAdapter = new StringAdapter(textInfo);
        int contourSize = 10;
        int marginSize = 10;
        int textSize = 18;
        int widthPixels = 512;

        Bitmap bitmap = stringAdapter.textToBitmap(textSize, widthPixels, contourSize, marginSize);

        int heightInfoDisplay = getSizeFromPixels(bitmap.getHeight());
        int widthInfoDisplay = 30;

        PanoramaInfoDisplay panoramaInfoDisplay = new PanoramaInfoDisplay(theta, 1.5, widthInfoDisplay
                , heightInfoDisplay, bitmap, null);

        panoramaInfoDisplay.setPanoramaInfoObject(panoramaInfoObject);
        panoramaInfoObject.setPanoramaInfoDisplay(panoramaInfoDisplay);
        attachPanoramaComponent(panoramaInfoDisplay, picker);
    }

    public void deleteTextToDisplay(PanoramaInfoDisplay panoramaInfoDisplay, ObjectColorPicker picker) {
        detachPanoramaComponent(picker, panoramaInfoDisplay);
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
            return 15;
        }
        return i;
    }
}
