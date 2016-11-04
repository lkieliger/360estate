package ch.epfl.sweng.project.engine3d;

import org.rajawali3d.math.vector.Vector3;

/**
 * Represent the object permitting transtion to the next panoSphere.
 */
class PanoramaTransitionObject extends PanoramaObject {


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
        super();
        enableLookAt();

        Id = id;
        this.nextUrl = nextUrl;

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

    /**
     * When called this method will update the PanoramaRender so that it reflects a transition to another panorama
     *
     * @param p
     */
    @Override
    public void reactWith(PanoramaRenderer p) {
        p.updateScene(getNextUrl(), getId());
    }


}
