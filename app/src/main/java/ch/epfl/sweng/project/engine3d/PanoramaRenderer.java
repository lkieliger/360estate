package ch.epfl.sweng.project.engine3d;


import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;

import org.rajawali3d.Object3D;
import org.rajawali3d.cameras.Camera;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.util.ObjectColorPicker;
import org.rajawali3d.util.OnObjectPickedListener;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.DataMgmt;
import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.data.AngleMapping;
import ch.epfl.sweng.project.data.HouseManager;
import ch.epfl.sweng.project.util.DebugPrinter;

import static android.content.Context.SENSOR_SERVICE;

/**
 * This class defines how the 3d engine should be used to
 * render the scene.
 */
public class PanoramaRenderer extends Renderer implements OnObjectPickedListener {

    public static final double SENSITIVITY = 100.0;
    public static final double MAX_PHI = 2 * Math.PI;
    public static final double EPSILON = 0.1d;
    public static final double MAX_THETA = Math.PI - EPSILON;
    private final String TAG = "Renderer";
    private final Display mDisplay;
    private final Camera mCamera;
    private final Vector3 mInitialPos;
    private final double mXdpi;
    private final double mYdpi;
    private final SensorManager mSensorManager;
    private final RotSensorListener mRotListener;
    private final boolean mRotSensorAvailable;
    private final Sensor mRotSensor;
    private PanoramaSphere earthSphere = new PanoramaSphere(100, 48, 48);
    private Quaternion mUserRot;
    private Quaternion mSensorRot;

    private HouseManager mHouseManager;
    private ObjectColorPicker mPicker;

    private int debugCounter = 0;

    public PanoramaRenderer(Context context, Display display, HouseManager houseManager) {

        super(context);

        mPicker = new ObjectColorPicker(this);

        mDisplay = display;

        mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        Sensor rotSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        mHouseManager = houseManager;

        if (rotSensor == null) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "No rotSensor available");
            }
            mRotListener = null;
            mRotSensor = null;
            mRotSensorAvailable = false;
        } else {
            mRotListener = new RotSensorListener(mDisplay, this);
            mRotSensor = rotSensor;
            mRotSensorAvailable = true;
        }


        mContext = context;

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        mXdpi = displayMetrics.xdpi;
        mYdpi = displayMetrics.ydpi;

        mUserRot = new Quaternion();
        mSensorRot = new Quaternion();
        mCamera = getCurrentCamera();
        mCamera.setFieldOfView(80);

        setFrameRate(60);

        mInitialPos = new Vector3(0, 0, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRotSensorAvailable) {
            mSensorManager.registerListener(mRotListener, mRotSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRotSensorAvailable) {
            mSensorManager.unregisterListener(mRotListener);
        }
    }


    @Override
    public void initScene() {

        Log.d(TAG, "Initializing scene");

        mCamera.setPosition(mInitialPos);

        Material material = new Material();
        Material material2 = new Material();
        material.setColor(0);
        material2.setColor(250);

        Bitmap mBitmap = DataMgmt.getBitmapfromUrl(getContext(),mHouseManager.getStartingUrl());

        Texture earthTexture = new Texture("Earth", mBitmap);

        try {
            material.addTexture(earthTexture);
        } catch (ATexture.TextureException error) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, error.toString());
            }
        }

        earthSphere.setPosition(mInitialPos);
        earthSphere.setBackSided(true);
        earthSphere.setMaterial(material);
        mPicker.setOnObjectPickedListener(this);

        addPanoramaTransitionObject(material2,mHouseManager.getStartingId());
        getCurrentScene().addChild(earthSphere);

    }


    /**
     * Update the current scene
     *
     * @param url the url of the image that will be loaded and added on the PanoSphere.
     * @param id the id of the image that will be loaded and added on the PanoSphere.
     */
    private void updateScene(String url,int id){
        Log.d(TAG, "Update scene");

        Material material = new Material();
        Material material2 = new Material();
        material.setColor(0);
        material2.setColor(250);


        Bitmap mBitmap = DataMgmt.getBitmapfromUrl(getContext(),url);

        Texture earthTexture = new Texture("Earth", mBitmap);

        Texture earthTexture2 = new Texture("Earth", R.drawable.earthtruecolor_nasa_big);

        try {
            material.addTexture(earthTexture);

        } catch (ATexture.TextureException error) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, error.toString());
            }
        }

        earthSphere.setMaterial(material);

        addPanoramaTransitionObject(material2,id);

        getCurrentScene().addChild(earthSphere);
    }

    /**
     * Add all the PanoramaTransition object into the Panosphere
     *
     * @param materialObject the material of the transition objects.
     * @param id the id of the current panoSphere.
     */
    private void addPanoramaTransitionObject(Material materialObject, int id){

        earthSphere.removeAllChild();

        for (AngleMapping angleMapping: mHouseManager.getSparseArray().get(id)) {
            PanoramaTransitionObject mChildSphere = new PanoramaTransitionObject(4, 10, 10,angleMapping.getId(),
                    angleMapping.getUrl());
            mChildSphere.setMaterial(materialObject);
            mChildSphere.setX(50*Math.sin(angleMapping.getPhi())*Math.cos(angleMapping.getTheta()));
            mChildSphere.setZ(50*Math.sin(angleMapping.getPhi())*Math.sin(angleMapping.getTheta()));
            mChildSphere.setY(50*Math.cos(angleMapping.getPhi()));
            mPicker.registerObject(mChildSphere);
            earthSphere.addChild(mChildSphere);
        }
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

    @Override
    public void onOffsetsChanged(float x, float y, float z, float w, int i, int j) {
    }


    @Override
    public void onRender(final long elapsedTime, final double deltaTime) {
        super.onRender(elapsedTime, deltaTime);

        updateCamera();

        if (BuildConfig.DEBUG) {

            if (debugCounter == 60) {
                debugCounter = 0;
                DebugPrinter.printRendererDebug(TAG, this);
            }
            debugCounter++;
        }

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
        double x = (dx / mXdpi) * SENSITIVITY;
        double y = (dy / mYdpi) * SENSITIVITY;

        double roll = mSensorRot.getRotationZ();

        double phi = (Math.cos(roll) * x) - (Math.sin(roll) * y);

        Quaternion rotY = new Quaternion().fromAngleAxis(Vector3.Axis.Y, -phi);
        mUserRot.multiplyLeft(rotY);
    }

    /**
     * This method should be called by the sensor listener in order to inform the renderer
     * of the current device rotation
     *
     * @param q The quaternion representing the device's rotation
     */
    public void setSensorRotation(Quaternion q) {
        mSensorRot = new Quaternion(q);
    }


    public Quaternion getUserRotation() {
        return new Quaternion(mUserRot);
    }

    public Quaternion getSensorRot() {
        return new Quaternion(mSensorRot);
    }

    /**
     * Automatically called when rendering, should not be manually called except for testing purposes
     * Updates the camera rotation based on user input and sensor information if available.
     * The way the camera rotation works is the following:
     * <p>
     * The class keeps track of a user quaternion which at the beginning is the identity quaternion.
     * The user quaternion cumulates the inputs generated by the user when the latter touches his screen.
     * Then, periodically the sensor listener updates the mSensorRot field with the latest values.
     * The camera rotation is then given by the sensor rotation additioned with the user input.
     * In quaternion notation this is equivalent to multiplying the sensor quaternion by the user
     * quaterion. Note that a defensive copy is needed because quaternions are mutable objects.
     * </p>
     */
    public void updateCamera() {
        Quaternion q = new Quaternion(mSensorRot);
        mCamera.setCameraOrientation(q.multiply(mUserRot));
    }


    @Override
    public void onObjectPicked(@NonNull Object3D object) {
        PanoramaTransitionObject panoObject = (PanoramaTransitionObject)object;
        updateScene(panoObject.getNextUrl(),panoObject.getId());
    }

    @Override
    public void onNoObjectPicked() {

    }

    public void getObjectAt(float x, float y) {
        Log.d(TAG,"ObjectPicked");
        mPicker.getObjectAt(x, y);
    }

}
