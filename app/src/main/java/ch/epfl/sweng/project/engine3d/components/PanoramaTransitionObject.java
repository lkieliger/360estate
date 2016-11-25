package ch.epfl.sweng.project.engine3d.components;

import org.rajawali3d.math.vector.Vector3;

import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.engine3d.PanoramaRenderer;

/**
 * Represent the object permitting transition to the next panoramicSphere. Each of those object will stock the next id
 * and url to load it directly.
 */
public final class PanoramaTransitionObject extends PanoramaObject {

    private static final String TEXTURE_TAG = "PanoTransitionTex";
    private static final int ICON_INDEX = R.drawable.transition_tex;
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
    public PanoramaTransitionObject(double theta, double phi, int id, String nextUrl) {
        super(theta, phi, TEXTURE_TAG, ICON_INDEX);
        enableLookAt();
        Id = id;
        this.nextUrl = nextUrl;
        setLookAt(new Vector3(0, 1000, 0));
    }

    public int getId() {
        return Id;
    }

    public String getNextUrl() {
        return nextUrl;
    }
    /**
     * When called this method will update the PanoramaRender so that it reflects a transition to another panorama
     *
     * @param p A reference to the panorama renderer
     */
    @Override
    public void reactWith(PanoramaRenderer p) {
        p.initiatePanoramaTransition(getNextUrl(), getId());
    }
}
