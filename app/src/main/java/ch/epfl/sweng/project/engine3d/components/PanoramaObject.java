package ch.epfl.sweng.project.engine3d.components;

import android.util.Log;

import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.AlphaMapTexture;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Plane;
import org.rajawali3d.util.ObjectColorPicker;

import ch.epfl.sweng.project.engine3d.PanoramaRenderer;

/**
 * A 3D object which can be used in conjunction with a PanoramaRenderer to offer panorama-specific features.
 */
public abstract class PanoramaObject extends Plane {

    private static final double DISTANCE = 80.0;
    static final int TEXTURE_COLOR = 0x0022c8ff;

    PanoramaObject(double theta, double phi, int width, int height, int distance) {
        super(width, height, width / 5, height / 5);

        setX(distance * Math.sin(phi) * Math.cos(theta));
        setZ(distance * Math.sin(phi) * Math.sin(theta));
        setY(distance * Math.cos(phi));
        setMaterial(new Material());

    }

    PanoramaObject(double theta, double phi, String tag, int iconIndex) {
        super(10, 10, 2, 2, Vector3.Axis.Z);
        setMaterial(new Material());

        setX(DISTANCE * Math.sin(phi) * Math.cos(theta));
        setZ(DISTANCE * Math.sin(phi) * Math.sin(theta));
        setY(DISTANCE * Math.cos(phi));

        setIcon(tag, iconIndex);
    }

    /**
     * Modify the icon used to represent this object in the 3D scene
     *
     * @param tag       The texture tag used by the TextureManager to identify the texture of this object
     * @param iconIndex the index used to identify the icon as a ressource
     */
    public void setIcon(String tag, int iconIndex) {
        setIcon(tag, iconIndex, TEXTURE_COLOR);
    }

    /**
     * Modify the icon used to represent this object in the 3D scene
     *
     * @param tag The texture tag used by the TextureManager to identify the texture of this object
     * @param iconIndex the index used to identify the icon as a ressource
     * @param colorIndex the index used to identify the material color of this object
     */
    public void setIcon(String tag, int iconIndex, int colorIndex) {
        AlphaMapTexture alphaMap = new AlphaMapTexture(tag, iconIndex);
        getMaterial().setColor(colorIndex);

        try {
            getMaterial().addTexture(alphaMap);
        } catch (ATexture.TextureException e) {
            Log.e("Texture error", e.getMessage());
        }
    }

    /**
     * Unregisters this objects with the given ObjectColorPicker
     *
     * @param p the ObjectColorPicker that should unregister this object
     */
    final void unregisterComponentFromPicker(ObjectColorPicker p) {
        p.unregisterObject(this);
    }

    /**
     * Register this component with an ObjectPicker so that subsequent clicks on it will be detected by the 3D engine
     *
     * @param p          the ObjectColorPicker with which to register this object
     * @param colorIndex the index that the ColorPicker should use to detect this object
     */
    final void registerComponentAtPicker(ObjectColorPicker p, int colorIndex) {
        p.registerObject(this);
        setPickingColor(colorIndex);
    }

    /**
     * Detach this PanoramaObject from its parent and frees associated memory
     */
    public void detachFromParentAndDie() {
        if (getParent() == null) {
            throw new IllegalStateException("Trying to detach PanoramaTransitionObject from a null " +
                    "parent !");
        }
        getParent().removeChild(this);
        destroy();
    }

    /**
     * Defines the custom behavior of the PanoramaObject. Designed to be overriden by each class to implement
     * different actions upon calls from the renderer such as displaying some text or transitioning to the next panorama
     *
     * @param p the PanoramaRenderer with which to interact
     */
    public abstract void reactWith(PanoramaRenderer p);

}
