package ch.epfl.sweng.project.engine3d;


import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import org.rajawali3d.cameras.Camera;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.renderer.Renderer;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.R;

/**
 * This class defines how the 3d engine should be used to
 * render the scene.
 */
public class PanoramaRenderer extends Renderer {

    public static final double SENSITIVITY = 1.0;
    public static final double MAX_PHI = 2 * Math.PI;
    public static final double EPSILON = 0.1d;
    public static final double MAX_THETA = Math.PI - EPSILON;


    private final String TAG = "Renderer";
    private final Camera mCamera;
    private final Vector3 mInitialPos;
    private final Vector3 mInitialLookat;
    private final double mXdpi;
    private final double mYdpi;
    private Sphere mChildSphere = null;

    //Phi is the azimutal angle
    private double mPhi;
    //Theta is the inclination angle
    private double mTheta;

    public PanoramaRenderer(Context context) {

        super(context);
        mContext = context;

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        mXdpi = displayMetrics.xdpi;
        mYdpi = displayMetrics.ydpi;

        mCamera = getCurrentCamera();
        mCamera.setFieldOfView(80);
        mCamera.enableLookAt();

        setFrameRate(60);

        mInitialPos = new Vector3(0, 0, 0);
        mInitialLookat = new Vector3(0, 0, 1);
        mPhi = 0;
        mTheta = Math.PI / 2.0;
    }

    @Override
    public void initScene() {

        Log.d(TAG, "Initializing scene");

        mCamera.setPosition(mInitialPos);
        mCamera.setLookAt(mInitialLookat);

        Material material = new Material();
        Material material2 = new Material();
        material.setColor(0);
        material2.setColor(0);

        Texture earthTexture = new Texture("Earth", R.drawable.pano_1024);
        Texture earthTexture2 = new Texture("Earth", R.drawable.earthtruecolor_nasa_big);
        earthTexture.shouldRecycle(true);
        earthTexture2.shouldRecycle(true);

        try {
            material.addTexture(earthTexture);
            material2.addTexture(earthTexture2);

        } catch (ATexture.TextureException error) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, error.toString());
            }
        }

        mChildSphere = new Sphere(8, 10, 10);
        mChildSphere.setMaterial(material2);
        mChildSphere.setZ(50);
        Sphere earthSphere = new Sphere(100, 48, 48);
        earthSphere.addChild(mChildSphere);
        earthSphere.setPosition(mInitialPos);
        earthSphere.setBackSided(true);
        earthSphere.setMaterial(material);

        getCurrentScene().addChild(earthSphere);
    }

    /**
     * Method currently not used as the panorama renderer activity already implements an
     * onTouchListener
     *
     * @param event the MotionEvent generated by the user
     */
    @Override
    public void onTouchEvent(MotionEvent event) {
    }

    /**
     * Method not used
     *
     * @param x .
     * @param y .
     * @param z .
     * @param w .
     * @param i .
     * @param j .
     */
    @Override
    public void onOffsetsChanged(float x, float y, float z, float w, int i, int j) {
    }


    @Override
    public void onRender(final long elapsedTime, final double deltaTime) {
        super.onRender(elapsedTime, deltaTime);

        mChildSphere.rotate(Vector3.Axis.Y, 0.4);
        updateLookAt();
    }

    /**
     * Use this method to rotate the camera according to the user input.
     * Its parameters represent over how much pixels the user has dragged its
     * fingers. Positive dx means gesture going to the right, positive dy means
     * gesture going down the screen.
     * The actual angle change is then proportional to the screen dpi so the
     * effect of a "swipe" doesn't change depending on the user screen resolution.
     *
     * @param dx The difference in pixels along the X axis. Positive means right
     * @param dy The difference in pixels along the Y axis. Positive means down
     */
    public void updateCameraRotation(float dx, float dy) {
        mPhi += (dx / mXdpi) * SENSITIVITY;
        mTheta -= (dy / mYdpi) * SENSITIVITY;
        clampPhi();
        clampTheta();

    }

    public double getCameraRotationPhi() {
        return mPhi;
    }

    public double getCameraRotationTheta() {
        return mTheta;
    }

    /**
     * This method computes the lookAt vector based on the phi and theta angles of
     * the camera. It is already automatically called by the onRender method thus you
     * should not call explicitely this method for other purposes than testing.
     * <p>
     * An angle phi equal to 0 is assumed to be along the Z axis, an angle theta equal
     * to 0 is assumed to be along the Y axis.
     * <p>
     * Because the Rajawali axis does not match the axis naming convention in physics
     * one should be careful about the way the lookAt vector is computed. As a reference,
     * see <a href="https://upload.wikimedia.org/wikipedia/commons/thumb/4/4f/3D_Spherical.svg/240px-3D_Spherical.svg
     * .png">this wikipedia illustration of the coordinates system used in physics.</a>.
     * Note that in Rajawali Y is UP, X is RIGHT ans Z is OUTWARDS. (if you match the axis with the above illustration).
     * This is why the components of the vector are not computed exactly the same way as in physics.
     */
    public void updateLookAt() {
        double z = Math.sin(mTheta) * Math.cos(mPhi);
        double y = Math.cos(mTheta);
        double x = Math.sin(mTheta) * Math.sin(mPhi);

        mCamera.setLookAt(new Vector3(x, y, z));
    }

    /**
     * Phi is the azimutal angle, thus it should be contained in the interval [0, 2*PI]
     */
    private void clampPhi() {
        if (mPhi < 0) {
            if (mPhi < -MAX_PHI) {
                //The camera did more that a complete turn in one frame !!
                mPhi = 0;
            } else {
                mPhi = MAX_PHI + mPhi;
            }
        } else if (mPhi > MAX_PHI) {
            mPhi -= MAX_PHI;
        }
    }

    /**
     * Theta is the inclinaison angle, thus it should be contained in the interval [0, PI]
     * Because of rounding errors, it is actually kept at a "security" angle of ESPILON
     * so the camera doesn't behave in a strange way when being aligned along the Y axis (going UP).
     * Indeed, the camera already uses the Y axis to know which way is UP when rotating so
     * an alignment with the Y axis produces a sudden change in orientation.
     */
    private void clampTheta() {
        if (mTheta < EPSILON) {
            mTheta = EPSILON;
        } else if (mTheta > MAX_THETA) {
            mTheta = MAX_THETA;
        }
    }

}
