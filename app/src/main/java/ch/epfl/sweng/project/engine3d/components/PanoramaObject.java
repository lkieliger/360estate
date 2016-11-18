package ch.epfl.sweng.project.engine3d.components;

import org.rajawali3d.materials.Material;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Plane;
import org.rajawali3d.util.ObjectColorPicker;

import ch.epfl.sweng.project.engine3d.PanoramaRenderer;


public abstract class PanoramaObject extends Plane {

    PanoramaObject() {
        super(10, 10, 2, 2, Vector3.Axis.Z);

        mMaterial = new Material();
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
