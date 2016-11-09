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
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.util.ObjectColorPicker;
import org.rajawali3d.util.OnObjectPickedListener;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.DataMgmt;
import ch.epfl.sweng.project.data.HouseManager;
import ch.epfl.sweng.project.engine3d.components.PanoramaObject;
import ch.epfl.sweng.project.engine3d.components.PanoramaSphere;
import ch.epfl.sweng.project.engine3d.listeners.RotSensorListener;
import ch.epfl.sweng.project.util.DebugPrinter;

import static android.content.Context.SENSOR_SERVICE;

/**
 * This class defines how the 3d engine should be used to
 * render the scene.
 */
public class PanoramaRenderer extends Renderer implements OnObjectPickedListener {

    public static final double SENSITIVITY = 50.0;
    private static final double LERP_FACTOR = 0.03;
    private static final Vector3 ORIGIN = new Vector3(0, 0, 0);
    private final String TAG = "Renderer";
    private final Camera mCamera;
    private final double mXdpi;
    private final double mYdpi;
    private final SensorManager mSensorManager;
    private final RotSensorListener mRotListener;
    private final boolean mRotSensorAvailable;
    private final Sensor mRotSensor;
    private PanoramaSphere mPanoSphere;
    private PanoramaObject mLastObjectPicked;
    private Quaternion mUserRot;
    private Quaternion mSensorRot;
    private Vector3 mTargetPos;
    private double mYaw;
    private boolean inCameraTransition;
    private boolean startCameraTransition;

    private HouseManager mHouseManager;
    private ObjectColorPicker mPicker;

    private int debugCounter = 0;


    public PanoramaRenderer(Context context, Display display, HouseManager houseManager) {
        super(context);

        mPicker = new ObjectColorPicker(this);
        mPicker.setOnObjectPickedListener(this);

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
            mRotListener = new RotSensorListener(display, this);
            mRotSensor = rotSensor;
            mRotSensorAvailable = true;
        }


        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        mXdpi = displayMetrics.xdpi;
        mYdpi = displayMetrics.ydpi;

        mUserRot = new Quaternion();
        mSensorRot = new Quaternion();
        mYaw = 0;
        mCamera = getCurrentCamera();
        mCamera.setFieldOfView(80);

        mPanoSphere = null;

        inCameraTransition = false;
        startCameraTransition = false;
        mLastObjectPicked = null;
        mTargetPos = ORIGIN;


        setFrameRate(60);
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
            //This frees unnecessary resources when app does not have focus
            mSensorManager.unregisterListener(mRotListener);
        }
    }


    @Override
    public void initScene() {

        Log.d(TAG, "Initializing scene");

        mCamera.setPosition(new Vector3(0, 0, 0));
        mPanoSphere = new PanoramaSphere();
        getCurrentScene().addChild(mPanoSphere);
        updateScene(mHouseManager.getStartingUrl(), mHouseManager.getStartingId());
    }


    /**
     * Update the current scene by changing the panorama picture and loading the new PanoramaComponents
     *
     * @param url the url of the image that will be loaded and added on the PanoSphere.
     * @param id  the id used to retrieve the mappings from angle to transition info
     */
    public void updateScene(String url, int id) {
        Log.d(TAG, "Update scene");

        Bitmap b = DataMgmt.getBitmapFromUrl(getContext(), url);
        mPanoSphere.detachPanoramaComponents(mPicker);
        mPanoSphere.setPhotoTexture(b);
        mPanoSphere.attachPanoramaComponents(mHouseManager.getNeighborsFromId(id), mPicker);
    }

    @Override
    public void onRender(final long elapsedTime, final double deltaTime) {
        super.onRender(elapsedTime, deltaTime);

        if (startCameraTransition) {
            inCameraTransition = true;
            startCameraTransition = false;
        }

        if (inCameraTransition) {

            Vector3 v = new Vector3(mTargetPos.x, 0, mTargetPos.z);
            Vector3 pos = new Vector3(mCamera.getPosition());
            mCamera.setPosition(pos.lerp(v, LERP_FACTOR));

            if (mCamera.getPosition().length() > 65) {
                mCamera.setPosition(ORIGIN);
                mLastObjectPicked.reactWith(this);
                inCameraTransition = false;
            }
        }

        if (debugCounter == 60) {
            debugCounter = 0;
            DebugPrinter.printRendererDebug(TAG, this);
        }
        debugCounter++;
        updateCamera();
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
        double xComp = (dx / mXdpi) * SENSITIVITY;
        double yComp = (dy / mYdpi) * SENSITIVITY;

        Log.d(TAG, "ROLL FROM SENSOR" + mYaw);

        double phi = (Math.cos(mYaw) * xComp) + (Math.sin(mYaw) * yComp);

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

    public double getDeviceYaw() {
        return mYaw;
    }

    /**
     * Updates the device yaw needed to compute the camera rotation for a user swipe
     *
     * @param y
     */
    public void setDeviceYaw(double y) {
        mYaw = y;
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


    public void getObjectAt(float x, float y) {
        mPicker.getObjectAt(x, y);
    }


    @Override
    public void onObjectPicked(@NonNull Object3D object) {
        Log.d(TAG, "ObjectPicked");
        mTargetPos = object.getWorldPosition();
        mLastObjectPicked = (PanoramaObject) object;
        startCameraTransition = true;
    }

    @Override
    public void onNoObjectPicked() {

    }


    @Override
    public void onTouchEvent(MotionEvent event) {
    }

    @Override
    public void onOffsetsChanged(float x, float y, float z, float w, int i, int j) {
    }
}
