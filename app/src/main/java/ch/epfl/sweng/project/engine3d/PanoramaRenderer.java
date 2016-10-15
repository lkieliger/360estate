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

public class PanoramaRenderer extends Renderer{

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
    private double mPhi;
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

        mInitialPos = new Vector3(0,0,0);
        mInitialLookat = new Vector3(0, 0, 1);
        mPhi = 0;
        mTheta = Math.PI / 2.0;
    }

    @Override
    public void initScene(){

        mCamera.setPosition(mInitialPos);
        mCamera.setLookAt(mInitialLookat);

        Material material = new Material();
        Material material2 = new Material();
        material.setColor(0);
        material2.setColor(0);

        Texture earthTexture = new Texture("Earth", R.drawable.pano_1024);
        Texture earthTexture2 = new Texture("Earth", R.drawable.earthtruecolor_nasa_big);
        try{
            material.addTexture(earthTexture);
            material2.addTexture(earthTexture2);

        } catch (ATexture.TextureException error){
            if(BuildConfig.DEBUG){
                Log.d(TAG, "TEXTURE ERROR");
            }
        }

        mChildSphere = new Sphere(8,10,10);
        mChildSphere.setMaterial(material2);
        mChildSphere.setZ(50);
        Sphere mEarthSphere = new Sphere(100, 48, 48);
        mEarthSphere.addChild(mChildSphere);
        mEarthSphere.setPosition(mInitialPos);
        mEarthSphere.setBackSided(true);
        mEarthSphere.setMaterial(material);

        getCurrentScene().addChild(mEarthSphere);
    }

    @Override
    public void onTouchEvent(MotionEvent event){
    }

    @Override
    public void onOffsetsChanged(float x, float y, float z, float w, int i, int j){
    }

    @Override
    public void onRender(final long elapsedTime, final double deltaTime) {
        super.onRender(elapsedTime, deltaTime);

        mChildSphere.rotate(Vector3.Axis.Y, 0.4);
        updateLookAt();
    }

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

    public void updateLookAt() {
        double z = Math.sin(mTheta) * Math.cos(mPhi);
        double y = Math.cos(mTheta);
        double x = Math.sin(mTheta) * Math.sin(mPhi);

        mCamera.setLookAt(new Vector3(x,y,z));
    }

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

    private void clampTheta() {
        if (mTheta < EPSILON) {
            mTheta = EPSILON;
        } else if (mTheta > MAX_THETA) {
            mTheta = MAX_THETA;
        }
    }

}
