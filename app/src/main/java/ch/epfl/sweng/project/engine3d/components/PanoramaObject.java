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

    PanoramaObject(double theta, double phi, String tag, int iconIndex) {
        super(10, 10, 2, 2, Vector3.Axis.Z);
        mMaterial = new Material();

        setX(DISTANCE * Math.sin(phi) * Math.cos(theta));
        setZ(DISTANCE * Math.sin(phi) * Math.sin(theta));
        setY(DISTANCE * Math.cos(phi));

        AlphaMapTexture alphaMap = new AlphaMapTexture(tag, iconIndex);
        mMaterial.setColor(TEXTURE_COLOR);

        try {
            mMaterial.addTexture(alphaMap);
        } catch (ATexture.TextureException e) {
            Log.e("Texture error", e.getMessage());
        }
    }

    void unregisterComponentFromPicker(ObjectColorPicker p) {
        p.unregisterObject(this);
    }

    void registerComponentAtPicker(ObjectColorPicker p) {
        p.registerObject(this);
        setPickingColor(0);
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
