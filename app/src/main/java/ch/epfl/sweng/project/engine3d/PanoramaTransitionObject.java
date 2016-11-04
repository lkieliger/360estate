package ch.epfl.sweng.project.engine3d;

import org.rajawali3d.materials.Material;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Plane;
import org.rajawali3d.util.ObjectColorPicker;

/**
 * Represent the object permitting transtion to the next panoSphere.
 */
class PanoramaTransitionObject extends Plane implements PanoramaComponent {


    private final int Id;
    private final String nextUrl;

    /**
     * Creates an object representing a transition possibility in the panorama
     *
     * @param theta   The inclination angle at which to display the object
     * @param phi     The azimuthal angle at which to display the object
     * @param id      The unique identifier of this transition object, used to find it in the transition table
     * @param nextUrl The url of the panorama to show after the transition
     */
    PanoramaTransitionObject(double theta, double phi, int id, String nextUrl) {
        super(10, 10, 2, 2, Vector3.Axis.Z);
        enableLookAt();

        Id = id;
        this.nextUrl = nextUrl;

        Material m = new Material();
        m.setColor(250);
        setMaterial(m);

        setX(50 * Math.sin(phi) * Math.cos(theta));
        setZ(50 * Math.sin(phi) * Math.sin(theta));
        setY(50 * Math.cos(phi));
        mLookAt = new Vector3(0, 0, 0);
    }

    int getId() {
        return Id;
    }

    String getNextUrl() {
        return nextUrl;
    }

    @Override
    public void unregisterComponentFromPicker(ObjectColorPicker p) {
        p.unregisterObject(this);
    }

    @Override
    public void registerComponentAtPicker(ObjectColorPicker p) {
        p.registerObject(this);
        this.setPickingColor(0);
    }

    @Override
    public void detachFromParentAndDie() {
        if (mParent == null) {
            throw new IllegalStateException("Trying to detach PanoramaTransitionObject from a null " +
                    "parent !");
        }
        mParent.removeChild(this);
        this.destroy();
    }
}
