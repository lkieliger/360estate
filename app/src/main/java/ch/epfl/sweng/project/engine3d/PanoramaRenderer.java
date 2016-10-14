package ch.epfl.sweng.project.engine3d;


import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

import org.rajawali3d.cameras.Camera;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.renderer.Renderer;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.R;

public class PanoramaRenderer extends Renderer{

    private static final double MAX_THETA = Math.PI;
    private static final double MAX_PHI = 2*Math.PI;

    private final double mScreenWidth;

    @SuppressWarnings("FieldCanBeLocal")
    private final String TAG = "Renderer";
    private final Camera mCamera;
    private Sphere mChildSphere = null;
    private double mPhi;
    private double mTheta;
    private final Vector3 mInitialPos;
    private final Vector3 mInitialLookat;

    public PanoramaRenderer(Context context) {
        super(context);
        mContext = context;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mScreenWidth = size.x;

        mCamera = getCurrentCamera();
        mCamera.setFieldOfView(80);
        mCamera.enableLookAt();

        setFrameRate(60);

        mInitialPos = new Vector3(0,0,0);
        mInitialLookat = new Vector3(1,0,0);
        mPhi = 0;
        mTheta = 90;
    }

    @Override
    public void initScene(){

        mCamera.setPosition(mInitialPos);
        mCamera.setLookAt(mInitialLookat);

        Material material = new Material();
        Material material2 = new Material();
        material.setColor(0);
        material2.setColor(0);

        Texture earthTexture = new Texture("Earth", R.drawable.pano_4096);
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
        mChildSphere.setX(50);
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
        double x = Math.sin(mTheta) * Math.cos(mPhi);
        double y = Math.cos(mTheta);
        double z = Math.sin(mTheta) * Math.sin(mPhi);

        mCamera.setLookAt(new Vector3(x,y,z));
    }

    public void updateCameraRotation(float dx, float dy) {
        mPhi -= (dx/ mScreenWidth)*3;
        mTheta -= (dy/ mScreenWidth)*3;
    }
}
