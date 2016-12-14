package ch.epfl.sweng.project.engine3d;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.rajawali3d.Object3D;
import org.rajawali3d.cameras.Camera;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.util.ObjectColorPicker;
import org.rajawali3d.util.OnObjectPickedListener;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.data.ImageMgmt;
import ch.epfl.sweng.project.data.panorama.HouseManager;
import ch.epfl.sweng.project.data.panorama.adapters.SpatialData;
import ch.epfl.sweng.project.data.panorama.adapters.TransitionObject;
import ch.epfl.sweng.project.engine3d.components.PanoramaComponentType;
import ch.epfl.sweng.project.engine3d.components.PanoramaInfoCloser;
import ch.epfl.sweng.project.engine3d.components.PanoramaInfoDisplay;
import ch.epfl.sweng.project.engine3d.components.PanoramaObject;
import ch.epfl.sweng.project.engine3d.components.PanoramaSphere;
import ch.epfl.sweng.project.engine3d.listeners.RotSensorListener;
import ch.epfl.sweng.project.util.Tuple;

import static android.content.Context.SENSOR_SERVICE;

/**
 * This class defines how the 3d engine should be used to
 * render the scene.
 */
public class PanoramaRenderer extends Renderer implements OnObjectPickedListener {

    public static final double SENSITIVITY = 50.0;
    public static final double CAM_TRAVEL_DISTANCE = 65.0;
    public static final Vector3 ORIGIN = new Vector3(0, 0, 0);
    public static final int TEXTURE_COLOR = 0x0022c8ff;
    public static final double DISTANCE_TO_DISPLAY = 9;

    public static final double LERP_FACTOR = 0.03;
    private final String TAG = "Renderer";
    private final ImageMgmt mImageManager;
    private final Camera mCamera;
    private final double mXdpi;
    private final double mYdpi;
    private final SensorManager mSensorManager;
    private final RotSensorListener mRotListener;
    private final boolean mRotSensorAvailable;
    private final Sensor mRotSensor;
    private PanoramaSphere mPanoSphere;
    private Quaternion mUserRot;
    private Quaternion mSensorRot;
    private Vector3 mTargetPos;
    private Quaternion mTargetQuaternion = new Quaternion();
    private Quaternion mHelperQuaternion = new Quaternion();
    private double mYaw;
    private FetchPhotoTask mImageLoadTask = null;
    private HouseManager mHouseManager;
    private RenderingLogic mRenderLogic;
    private ObjectColorPicker mPicker;
    private int debugCounter = 0;
    /**
     * Use this rendering when nothing special need to be done. In other words just allowing the camera to look
     * around and print some debug information.
     */
    private RenderingLogic mIdleRendering = new RenderingLogic() {
        @Override
        public void render() {
            //Log.i(TAG, "Rendering logic is set to idle");
            if (debugCounter == 60) {
                debugCounter = 0;
                //       DebugPrinter.printRendererDebug(TAG, PanoramaRenderer.this);
            }
            debugCounter++;

        }
    };
    /**
     * Use this rendering logic to change the panorama photo after a scene transition
     */
    private RenderingLogic mTransitioningRendering = new RenderingLogic() {
        @Override
        public void render() {
            //Log.i(TAG, "Rendering logic is set to transitioning");
            updateScene();
            mCamera.setPosition(ORIGIN);
            mRenderLogic = getIdleRendering(
            );
        }
    };
    /**
     * Use this rendering logic to gradually move the camera toward the TransitionObject target
     */
    private RenderingLogic mSlidingRendering = new RenderingLogic() {
        @Override
        public void render() {
            //Log.i(TAG, "Rendering logic is set to sliding");
            double travellingLength = mCamera.getPosition().length();

            if (travellingLength < CAM_TRAVEL_DISTANCE) {
                Vector3 v = new Vector3(mTargetPos.x, mTargetPos.y, mTargetPos.z);
                Vector3 pos = new Vector3(mCamera.getPosition());
                mCamera.setPosition(pos.lerp(v, LERP_FACTOR));
            }

            if (travellingLength >= CAM_TRAVEL_DISTANCE && NextPanoramaDataBuilder.isReady()) {
                mRenderLogic = getTransitioningRendering();
            }
        }
    };

    private RenderingLogic mSlidingToTextRendering = new RenderingLogic() {
        @Override
        public void render() {
            //Log.i(TAG, "Rendering logic is set to sliding");
            double travellingLength = mCamera.getPosition().length();

            if (travellingLength < DISTANCE_TO_DISPLAY) {
                Vector3 v = new Vector3(mTargetPos.x, mTargetPos.y, mTargetPos.z);
                Vector3 pos = new Vector3(mCamera.getPosition());
                mCamera.setPosition(pos.lerp(v, LERP_FACTOR));
            }
            mHelperQuaternion = mHelperQuaternion.slerp(mTargetQuaternion, LERP_FACTOR * 7);
            mCamera.setCameraOrientation(mHelperQuaternion);
        }
    };


    private RenderingLogic mSlidingOutOfTextRendering = new RenderingLogic() {
        @Override
        public void render() {
            //Log.i(TAG, "Rendering logic is set to sliding");
            double travellingLength = mCamera.getPosition().length();
            if (travellingLength > 0.01) {
                Vector3 pos = new Vector3(mCamera.getPosition());
                mCamera.setPosition(pos.lerp(ORIGIN, LERP_FACTOR * 5));
            } else {
                mRenderLogic = getIdleRendering();
                Log.d(TAG, "Movement Terminated");
            }
            Quaternion q = new Quaternion(mSensorRot);
            q = q.multiply(mUserRot);

            mHelperQuaternion = mHelperQuaternion.slerp(q, LERP_FACTOR * 5);
            mCamera.setCameraOrientation(mHelperQuaternion);
        }
    };

    public PanoramaRenderer(Context context, Display display, HouseManager houseManager) {
        super(context);

        NextPanoramaDataBuilder.resetData();

        mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        Sensor rotSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        mHouseManager = houseManager;
        mImageManager = new ImageMgmt();

        if (rotSensor == null) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "No rotSensor available");
            }
            mRotListener = null;
            mRotSensor = null;
            mRotSensorAvailable = false;
        } else {
            mRotListener = new RotSensorListener(display.getRotation(), this);
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
        mCamera.setFarPlane(220);

        mPanoSphere = null;

        mRenderLogic = getIdleRendering();
        mTargetPos = ORIGIN;

        mPicker = new ObjectColorPicker(this);
        mPicker.setOnObjectPickedListener(this);

        setFrameRate(60);
    }

    //-------------------------------------------- ACTION METHODS ---------------------------------------------------//

    /**
     * This method is called to initiate the panorama transition. The camera will start
     * sliding towards the next panorama and a task will be launched to retrieve
     * asynchronously the next bitmap
     *
     * @param url URL of the next Bitmap to load
     * @param id  Id of the next Panorama to load
     */
    public void initiatePanoramaTransition(final String url, final int id) {
        Log.d(TAG, "Call to initiate panorama transition, creating new task and setting next id.");
        mRenderLogic = getSlidingRendering();

        NextPanoramaDataBuilder.setNextPanoId(id);
        mImageLoadTask = new FetchPhotoTask();
        mImageManager.getBitmapFromUrl(mContext, url, mImageLoadTask);
    }

    public void deleteInfo(PanoramaInfoDisplay panoramaInfoDisplay, PanoramaInfoCloser panoramaInfoCloser) {
        Log.d(TAG, "Call to delete text information.");
        mPanoSphere.deleteTextToDisplay(panoramaInfoDisplay, panoramaInfoCloser);
    }

    public void zoomOnText(double angle, double x, double z) {
        mTargetPos = mTargetPos.setAll(x, 25, z);
        if (BuildConfig.DEBUG) Log.d(TAG, "Moving to:" + x + " , " + z);

        mTargetQuaternion = Quaternion.getIdentity().fromEuler(angle * 180 / Math.PI + 90, 0, 0);

        Quaternion q = new Quaternion(mSensorRot);
        mHelperQuaternion = q.multiply(mUserRot);
        mRenderLogic = getSlidingToTextRendering();
    }

    public void zoomOut(double angle) {
        Log.d(TAG, "Moving Out");
        mHelperQuaternion = Quaternion.getIdentity().fromEuler(angle * 180 / Math.PI + 90, 0, 0);
        mRenderLogic = getSlidingOutOfTextRendering();
    }


    /**
     * This method is called by the asynchronous task that fetched the panorama picture
     *
     * @param b The panorama picture as a Bitmap
     */
    public void prepareScene(Bitmap b) {
        if (b == null) {
            Log.e(TAG, "There was a problem with the PhotoFetch task, returned bitmap was null");
            NextPanoramaDataBuilder.resetData();
            mRenderLogic = getIdleRendering();
        } else {
            Log.d(TAG, "Call to prepare scene, assigning next bitmap.");
            NextPanoramaDataBuilder.setNextPanoBitmap(b);
        }
    }


    /**
     * Actually updates the current scene by changing the panorama picture and loading the new PanoramaComponents
     */
    public void updateScene() {

        Tuple<Integer, Bitmap> panoData = NextPanoramaDataBuilder.build();
        if (BuildConfig.DEBUG)
            Log.d(TAG, "Call to update scene, changing panorama photo and attaching new components." +
                    " Pano id is: " + panoData.getX());

        mPanoSphere.detachPanoramaComponents();
        mPanoSphere.setPhotoTexture(panoData.getY());
        List<SpatialData> panoComponents = mHouseManager.getAttachedDataFromId(panoData.getX());
        mPanoSphere.attachPanoramaComponents(panoComponents);

        //Pre-fetch all neighboring panoramas
        List<String> urls = new ArrayList<>();

        for (SpatialData sd : panoComponents) {
            if (sd.getType() == PanoramaComponentType.TRANSITION) {
                urls.add(((TransitionObject) sd).getUrl());
            }
        }

        mImageManager.warmCache(mContext, urls);
    }

    public void cancelPanoramaUpdate() {
        Picasso.with(mContext).cancelRequest(mImageLoadTask);
        NextPanoramaDataBuilder.resetData();
    }


    //---------------------------------------- RENDERING-RELATED METHODS --------------------------------------------//

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
     * @param y yaw
     */
    public void setDeviceYaw(double y) {
        mYaw = y;
    }

    public void resetTargetPos() {
        mTargetPos = new Vector3(0, 0, 0);
    }

    public Quaternion getUserRotation() {
        return new Quaternion(mUserRot);
    }

    public Quaternion getSensorRot() {
        return new Quaternion(mSensorRot);
    }

    public PanoramaSphere getPanoramaSphere() {
        return mPanoSphere;
    }

    public RenderingLogic getCurrentRenderingLogic() {
        return mRenderLogic;
    }

    public void getObjectAt(float x, float y) {
        mPicker.getObjectAt(x, y);
    }

    public RenderingLogic getSlidingOutOfTextRendering() {
        return mSlidingOutOfTextRendering;
    }

    public RenderingLogic getSlidingToTextRendering() {
        return mSlidingToTextRendering;
    }

    public RenderingLogic getSlidingRendering() {
        return mSlidingRendering;
    }

    public RenderingLogic getIdleRendering() {
        return mIdleRendering;
    }

    public RenderingLogic getTransitioningRendering() {
        return mTransitioningRendering;
    }

    @Override
    public void initScene() {

        Log.d(TAG, "Initializing scene");

        Picasso.with(mContext).setLoggingEnabled(true);

        mCamera.setPosition(new Vector3(0, 0, 0));
        mPanoSphere = new PanoramaSphere(mPicker);
        getCurrentScene().addChild(mPanoSphere);

        NextPanoramaDataBuilder.setNextPanoId(mHouseManager.getStartingId());
        prepareScene(mImageManager.getBitmapFromUrl(getContext(), mHouseManager.getStartingUrl()));
        Log.d(TAG, "Updating scene from initscene");
        updateScene();
    }

    @Override
    public void onRender(final long elapsedTime, final double deltaTime) {
        super.onRender(elapsedTime, deltaTime);
        mRenderLogic.render();
        if (mRenderLogic != mSlidingToTextRendering && mRenderLogic != mSlidingOutOfTextRendering) {
            updateCamera();
        }
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
    public void onObjectPicked(@NonNull Object3D object) {
        if (mRenderLogic == mIdleRendering || mRenderLogic == mSlidingToTextRendering) {
            Log.d(TAG, "ObjectPicked");
            mTargetPos = object.getWorldPosition();
            mTargetPos.y += 25;

            ((PanoramaObject) object).reactWith(this);

        }
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


    /**
     * This interface defines a behavior of the PanoramaRenderer. Change in behavior can happen due to user input for
     * displaying a transition for example.
     */

    public interface RenderingLogic {
        void render();
    }

    /**
     * Builder for the data needed to transition between panoramas.
     */
    public static final class NextPanoramaDataBuilder {
        static final int INVALID_ID = -1;
        static final Bitmap INVALID_BITMAP = null;
        private static final String TAG = "NextPanoDataBuilder";
        private static Integer nextPanoId = INVALID_ID;
        private static Bitmap nextPanoBitmap = INVALID_BITMAP;

        public static boolean isReady() {
            return (nextPanoId != INVALID_ID && nextPanoBitmap != INVALID_BITMAP);
        }

        public static boolean isReset() {
            return (nextPanoId == INVALID_ID && nextPanoBitmap == INVALID_BITMAP);
        }

        /**
         * Sets the next panorama id.
         *
         * @param i the next id
         * @throws IllegalStateException if the id is set more than once
         */
        public static void setNextPanoId(Integer i) {
            if (i == null) {
                throw new IllegalArgumentException("Null id");
            }
            if (nextPanoId != INVALID_ID) {
                throw new IllegalStateException("Next panorama id should not be set multiple times");
            }
            nextPanoId = i;
            logStatus();
        }

        /**
         * Sets the next panorama bitmap file
         *
         * @param b The bitmap file
         * @throws IllegalStateException if the bitmap is set more than once
         */
        public static void setNextPanoBitmap(Bitmap b) {
            if (b == null) {
                throw new IllegalArgumentException("Null bitmap");
            }
            if (nextPanoBitmap != INVALID_BITMAP) {
                throw new IllegalStateException("Next panorama bitmap should not be set multiple times");
            }
            nextPanoBitmap = b;
            logStatus();
        }

        /**
         * @return the built PanoramaData
         * @throws IllegalStateException if the PanoramaData is incomplete
         */
        public static Tuple<Integer, Bitmap> build() {
            if (!isReady()) {
                throw new IllegalStateException("Next panorama data is incomplete, cannot build");
            }

            Tuple<Integer, Bitmap> result = new Tuple<>(nextPanoId, nextPanoBitmap);
            resetData();
            return result;
        }

        /**
         * This method should not be used out side this class except in JUnit tests to ensure builder state
         * before each tests
         */
        public static void resetData() {
            nextPanoId = INVALID_ID;
            nextPanoBitmap = INVALID_BITMAP;
            logStatus();
        }

        private static void logStatus() {
            if (BuildConfig.DEBUG) Log.i(TAG, "ID: " + nextPanoId + ", Bitmap@" + nextPanoBitmap);
        }
    }

    /**
     * Target for Picasso's asynchronous image loading
     */
    private class FetchPhotoTask implements Target {

        private static final String TAG = "FetchPhotoTask";

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            prepareScene(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Log.d(TAG, "Picasso async bitmap load failed");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }
}
