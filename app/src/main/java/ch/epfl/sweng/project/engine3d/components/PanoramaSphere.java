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
import ch.epfl.sweng.project.util.LogHelper;



/**
 * A PanoramaSphere is a rajawali 3d object that stores a panorama image
 * in the form of a texture. Additional UI components such as transition arrows and information icons can be attached as
 * children of this object. Upon creation of a panorama sphere a unique texture is created and registered with the
 * material manager. The texture contains a bitmap as well as ID which is used by the texture manager to keep track of
 * all texture used by the application. The bitmap associated with a texture can change but the texture instance
 * remains unique. Therefore subsequent panorama photo changes should call the texture setter as it will handle the
 * bitmap replacement.
 */
public class PanoramaSphere extends Sphere {

    public static final String TEXTURE_TAG = "PhotoTexture";
    private static final int INITIAL_COMPONENTLIST_SIZE = 10;
    private static final String TAG = "PanoramaSphere";
    private static final Vector3 INITIAL_POS = new Vector3(0, 0, 0);
    private final List<PanoramaObject> mComponentList;
    private final ObjectColorPicker mPicker;
    private Texture mPhotoTexture;
    private int mComponentIndex = 0;

    public PanoramaSphere(ObjectColorPicker p) {
        super(100, 48, 48);

        mComponentList = new ArrayList<>(INITIAL_COMPONENTLIST_SIZE);

        mPicker = p;

        setBackSided(true);
        setPosition(INITIAL_POS);
        Material m = new Material();
        m.setColor(0);

        mPhotoTexture = new Texture(TEXTURE_TAG);

        try {
            m.addTexture(mPhotoTexture);
        } catch (ATexture.TextureException e) {
            Log.e(TAG, e.getMessage());
        }

        setMaterial(m);
    }

    /**
     * Dissociate all UI components that were previously defined as children of this panorama
     * sphere. Dissociated objects are destroyed as it does not make sens to have panorama related object instances
     * with the associated spherical picture and data.
     */
    public void detachPanoramaComponents() {
        for (PanoramaObject pc : mComponentList) {
            pc.unregisterComponentFromPicker(mPicker);
            pc.detachFromParentAndDie();
            mComponentIndex--;
        }
        mComponentList.clear();
    }

    /**
     * Detach and destroy a single panorama component
     *
     * @param panoramaObject the objects to detach and destroy
     */
    public void detachPanoramaComponent(PanoramaObject panoramaObject) {
        LogHelper.log(TAG, "Call to detach component");

        int index = mComponentList.indexOf(panoramaObject);
        panoramaObject.setPickingColor(-1);

        for (int i = index; i < mComponentList.size(); i++) {
            mComponentList.get(i).unregisterComponentFromPicker(mPicker);
            mComponentIndex--;
        }

        mComponentList.remove(panoramaObject);
        panoramaObject.detachFromParentAndDie();

        for (int i = index; i < mComponentList.size(); i++) {
            mComponentList.get(i).registerComponentAtPicker(mPicker, mComponentIndex);
            mComponentIndex++;
        }
    }

    /**
     * Replace the currently shown panorama. The
     * method will then call the texture manager and update the bitmap
     * associated with its texture
     *
     * @param b A bitmap file that contain the panorama picture
     */
    public void setPhotoTexture(Bitmap b) {
        Bitmap old = mPhotoTexture.getBitmap();
        if (old != null) {
            old.recycle();
            LogHelper.log(TAG, "Recycling old panorama texture bitmap");
        }
        mPhotoTexture.setBitmap(b);
        TextureManager.getInstance().replaceTexture(mPhotoTexture);
    }

    /**
     * Attach the given components as children of this sphere
     *
     * @param l a list of spatial data to attach to this panorama sphere
     */
    public void attachPanoramaComponents(Iterable<SpatialData> l) {
        LogHelper.log(TAG, "Call to attach panorama");
        for (SpatialData am : l) {
            PanoramaObject panoramaObject = am.toPanoramaObject();
            attachPanoramaComponent(panoramaObject);
        }
    }

    /**
     * Attach a single component as children of this sphere
     *
     * @param panoramaObject the object to attach
     */
    public void attachPanoramaComponent(PanoramaObject panoramaObject) {

        LogHelper.log(TAG, "Call to attach component");
        panoramaObject.registerComponentAtPicker(mPicker, mComponentIndex);

        mComponentIndex++;
        addChild(panoramaObject);
        mComponentList.add(panoramaObject);
    }

    /**
     * Creates and display text in the 3D scene
     *
     * @param message            the text to display
     * @param theta              spherical coordinates of the displayed text
     * @param panoramaInfoObject the object responsible for representing the possibility of showing up text in the 3D
     *                           scene
     */
    public void createTextDisplay(String message, double theta, PanoramaInfoObject panoramaInfoObject) {

        StringAdapter stringAdapter = new StringAdapter(message);
        int contourSize = 10;
        int marginSize = 10;
        int textSize = 18;
        int widthPixels = 512;
        int heightLimit = 512;

        Bitmap bitmap = stringAdapter.textToBitmap(textSize, widthPixels, contourSize, marginSize, heightLimit);

        int heightInfoDisplay = getSizeFromPixels(bitmap.getHeight());
        int widthInfoDisplay = 30;

        PanoramaInfoDisplay panoramaInfoDisplay = new PanoramaInfoDisplay(theta, 1.57, widthInfoDisplay
                , heightInfoDisplay, bitmap, null);

        panoramaInfoDisplay.setY(panoramaInfoDisplay.getY() + 10);

        panoramaInfoDisplay.setPanoramaInfoObject(panoramaInfoObject);
        panoramaInfoObject.setPanoramaInfoDisplay(panoramaInfoDisplay);
        attachPanoramaComponent(panoramaInfoDisplay);
    }

    /**
     * Delete text related to the given info object
     * @param panoramaInfoDisplay the object whose associated text is to remove from the 3D scene
     */
    public void deleteTextToDisplay(PanoramaInfoDisplay panoramaInfoDisplay) {
        detachPanoramaComponent(panoramaInfoDisplay);
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
}
