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

            panoramaObject.registerComponentAtPicker(p, mComponentIndex);

            mComponentIndex++;
            addChild(panoramaObject);
            mComponentList.add(panoramaObject);
        }
    }

    public void attachPanoramaComponent(PanoramaObject panoramaObject, ObjectColorPicker p) {

        Log.d(TAG, "Call to attach component");
        panoramaObject.registerComponentAtPicker(p, mComponentIndex);

        mComponentIndex++;
        addChild(panoramaObject);
        mComponentList.add(panoramaObject);
    }

    public void setTextToDisplay(String textInfo, double theta, PanoramaInfoObject panoramaInfoObject, int colorIndex,
                                 ObjectColorPicker picker) {

        StringAdapter stringAdapter = new StringAdapter(textInfo);
        int epsilon = 15;
        Bitmap bitmap = stringAdapter.textToBitmap(22, 512, colorIndex, epsilon);


        int heightInfoDisplay = ((int) (Math.log(bitmap.getHeight()) / Math.log(2)) - 7) * 15;
        int widthInfoDisplay = 30;
        int heightInfoClose = 5;
        int widthInfoClose = 5;


        PanoramaInfoDisplay panoramaInfoDisplay = new PanoramaInfoDisplay(theta, 1.5, widthInfoDisplay
                , heightInfoDisplay, bitmap, colorIndex);

        int shiftY = (int) ((heightInfoDisplay + heightInfoClose + 4) / 2.0);

        PanoramaInfoCloser panoramaInfoCloser = new PanoramaInfoCloser(theta, 1.5, widthInfoClose
                , heightInfoClose, panoramaInfoDisplay, panoramaInfoObject);

        panoramaInfoCloser.setY(panoramaInfoCloser.getY() + shiftY);

        attachPanoramaComponent(panoramaInfoDisplay, picker);
        attachPanoramaComponent(panoramaInfoCloser, picker);


    }

    public void deleteTextToDisplay(PanoramaInfoDisplay panoramaInfoDisplay, PanoramaInfoCloser panoramaInfoCloser,
                                    ObjectColorPicker picker) {
        detachPanoramaComponent(picker, panoramaInfoDisplay);
        detachPanoramaComponent(picker, panoramaInfoCloser);
    }
}
