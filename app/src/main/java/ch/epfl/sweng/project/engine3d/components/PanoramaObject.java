package ch.epfl.sweng.project.engine3d.components;

import android.util.Log;

import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.AlphaMapTexture;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Plane;
import org.rajawali3d.util.ObjectColorPicker;

import ch.epfl.sweng.project.engine3d.PanoramaRenderer;


public abstract class PanoramaObject extends Plane {

    private static final double DISTANCE = 80.0;
    private static final int TEXTURE_COLOR = PanoramaRenderer.TEXTURE_COLOR;


    PanoramaObject() {
        super(10, 10, 2, 2, Vector3.Axis.Z);

        mMaterial = new Material();
    }

    PanoramaObject(double theta, double phi, int width, int height, int distance) {
        super(width, height, width / 5, height / 5);

        setX(distance * Math.sin(phi) * Math.cos(theta));
        setZ(distance * Math.sin(phi) * Math.sin(theta));
        setY(distance * Math.cos(phi));
        mMaterial = new Material();

    }

    PanoramaObject(double theta, double phi, String tag, int iconIndex) {
        super(10, 10, 2, 2, Vector3.Axis.Z);
        mMaterial = new Material();

        setX(DISTANCE * Math.sin(phi) * Math.cos(theta));
        setZ(DISTANCE * Math.sin(phi) * Math.sin(theta));
        setY(DISTANCE * Math.cos(phi));

        setIcon(tag, iconIndex);
    }

    public void setIcon(String tag, int iconIndex) {
        setIcon(tag, iconIndex, TEXTURE_COLOR);
    }

    public void setIcon(String tag, int iconIndex, int colorIndex) {
        AlphaMapTexture alphaMap = new AlphaMapTexture(tag, iconIndex);
        mMaterial.setColor(colorIndex);

        try {
            mMaterial.addTexture(alphaMap);
        } catch (ATexture.TextureException e) {
            Log.e("Texture error", e.getMessage());
        }
    }

    void unregisterComponentFromPicker(ObjectColorPicker p) {
        p.unregisterObject(this);
    }

    void registerComponentAtPicker(ObjectColorPicker p, int colorIndex) {
        p.registerObject(this);
        setPickingColor(colorIndex);
    }

    public void detachFromParentAndDie() {
        if (mParent == null) {
            throw new IllegalStateException("Trying to detach PanoramaTransitionObject from a null " +
                    "parent !");
        }
        mParent.removeChild(this);
        destroy();
    }

    public abstract void reactWith(PanoramaRenderer p);

}
