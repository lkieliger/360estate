package ch.epfl.sweng.project.engine3d.components;

import android.util.Log;

import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.AlphaMapTexture;
import org.rajawali3d.math.vector.Vector3;

import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.engine3d.PanoramaRenderer;

/**
 * Represent the object permitting transition to the next panoramicSphere. Each of those object will stock the next id
 * and url to load it directly.
 */
public final class PanoramaTransitionObject extends PanoramaObject {

    private static final String TEXTURE_TAG = "PanoTransitionTex";
    private static final double DISTANCE = 80.0;
    private static final int TEXTURE_COLOR = 0x22c8ff;
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
        super();
        enableLookAt();
        AlphaMapTexture alphamap = new AlphaMapTexture(TEXTURE_TAG, R.drawable
                .transition_tex);
        mMaterial.setColor(TEXTURE_COLOR);

        try {
            mMaterial.addTexture(alphamap);
        } catch (ATexture.TextureException e) {
            Log.e("Texture error", e.getMessage());
        }

        Id = id;
        this.nextUrl = nextUrl;

        setX(DISTANCE * Math.sin(phi) * Math.cos(theta));
        setZ(DISTANCE * Math.sin(phi) * Math.sin(theta));
        setY(DISTANCE * Math.cos(phi));

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
