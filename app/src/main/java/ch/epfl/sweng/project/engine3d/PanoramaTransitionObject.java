package ch.epfl.sweng.project.engine3d;

import org.rajawali3d.materials.Material;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Plane;

/**
 * Represent the object permitting transtion to the next panoSphere.
 */
class PanoramaTransitionObject extends Plane implements PanoramaComponent {


    private final int Id;
    private final String nextUrl;

    public PanoramaTransitionObject(double theta, double phi, int id, String nextUrl) {
        super(10, 10, 2, 2, Vector3.Axis.X);

        Id = id;
        this.nextUrl = nextUrl;

        Material m = new Material();
        m.setColor(250);
        setMaterial(m);

        setX(50 * Math.sin(phi) * Math.cos(theta));
        setZ(50 * Math.sin(phi) * Math.sin(theta));
        setY(50 * Math.cos(phi));

    }

    int getId() {
        return Id;
    }

    String getNextUrl() {
        return nextUrl;
    }
}
