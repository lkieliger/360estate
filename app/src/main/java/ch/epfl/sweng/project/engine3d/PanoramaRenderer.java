package ch.epfl.sweng.project.engine3d;


import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;

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

import ch.epfl.sweng.project.data.ImageMgmt;
import ch.epfl.sweng.project.data.panorama.HouseManager;
import ch.epfl.sweng.project.data.panorama.adapters.SpatialData;
import ch.epfl.sweng.project.data.panorama.adapters.TransitionObject;
import ch.epfl.sweng.project.engine3d.components.PanoramaComponentType;
import ch.epfl.sweng.project.engine3d.components.PanoramaInfoDisplay;
import ch.epfl.sweng.project.engine3d.components.PanoramaInfoObject;
import ch.epfl.sweng.project.engine3d.components.PanoramaObject;
import ch.epfl.sweng.project.engine3d.components.PanoramaSphere;
import ch.epfl.sweng.project.engine3d.listeners.RotSensorListener;
import ch.epfl.sweng.project.util.DebugPrinter;
import ch.epfl.sweng.project.util.LogHelper;
import ch.epfl.sweng.project.util.Tuple;

import static android.content.Context.SENSOR_SERVICE;

/**
 * This class defines how the 3d engine should be used to render the scene.
 */
public class PanoramaRenderer extends Renderer implements OnObjectPickedListener {

    public static final double SENSITIVITY = 50.0;
    public static final double CAM_TRAVEL_DISTANCE = 65.0;
    public static final Vector3 ORIGIN = new Vector3(0, 0, 0);
    public static final int TEXTURE_COLOR = 0x0022c8ff;
    public static final double DISTANCE_TO_DISPLAY = 9;
    public static final double LERP_FACTOR = 0.03;
    public static final double FOV_PORTRAIT = 90;
    public static final double FOV_LANDSCAPE = 65;
    private static final int COLOR_CLOSE = Color.rgb(255, 25, 25);

    private final String TAG = "Renderer";
    private final ImageMgmt mImageManager;
    private final Camera mCamera;
    private final double mXdpi;
    private final double mYdpi;
    private final SensorManager mSensorManager;
    private final RotSensorListener mRotListener;
    private final boolean mRotSensorAvailable;
    private final Sensor mRotSensor;
    private final Quaternion mUserRot;
    private final HouseManager mHouseManager;
    private final ObjectColorPicker mPicker;
    private PanoramaSphere mPanoSphere;
    private Quaternion mSensorRot;
    private Vector3 mTargetPos;
    private Quaternion mTargetQuaternion = new Quaternion();
    private Quaternion mHelperQuaternion = new Quaternion();
    /**
     * Use this rendering logic to gradually move the camera toward the PanoramaInfoDisplay target.
     */
    private final RenderingLogic mSlidingToTextRendering = new RenderingLogic() {
        @Override
        public void render() {
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
    private double mYaw;
    private FetchPhotoTask mImageLoadTask = null;
    private RenderingLogic mRenderLogic;
    /**
     * Use this rendering logic to change the panorama photo after a scene transition
     */
    private final RenderingLogic mTransitioningRendering = new RenderingLogic() {
        @Override
        public void render() {
            updateScene();
            mCamera.setPosition(ORIGIN);
            mRenderLogic = getIdleRendering(
            );
        }
    };
    /**
     * Use this rendering logic to gradually move the camera toward the TransitionObject target
     */
    private final RenderingLogic mSlidingRendering = new RenderingLogic() {
        @Override
        public void render() {
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
    /**
     * Use this rendering logic to gradually move the camera to the ORIGIN (the center of the scene)
     */
    private final RenderingLogic mSlidingOutOfTextRendering = new RenderingLogic() {
        @Override
        public void render() {
            slideOutOfText();
        }
    };
    private int debugCounter = 0;
    /**
     * Use this rendering when nothing special need to be done. In other words just allowing the camera to look
     * around and print some debug information.
     */
    private final RenderingLogic mIdleRendering = new RenderingLogic() {
        @Override
        public void render() {
            if (debugCounter == 60) {
                debugCounter = 0;
                DebugPrinter.printRendererDebug(TAG, PanoramaRenderer.this);
            }
            debugCounter++;
        }
    };
    // Variables used for the rotation.
    private PanoramaInfoObject objectToRotate = null;
    private double targetAngle = 0.0;
    private double currentAngle = 0.0;
    private float rotationPercent = 0;
    private int startingColor = TEXTURE_COLOR;
    private int finishColor = COLOR_CLOSE;

    /**
     * Use this rendering logic to gradually rotate the PanoramaInfoObject target
     */
    private final RenderingLogic mRotateObjectRendering = new RenderingLogic() {
        @Override
        public void render() {
            Log.i(TAG, "Rotate Object in progress");
            rotateTarget();
        }
    };

    /**
     * Use this rendering logic to gradually rotate the PanoramaInfoObject target and gradually move
     * the camera towards the center. It combines mRotateObjectRendering and mSlidingOutOfTextRendering.
     */
    private final RenderingLogic mRotateObjectAndZoomOutRendering = new RenderingLogic() {
        @Override
        public void render() {
            Log.i(TAG, "Rotate Object And Zoom Out");
            rotateTarget();
            if (mRenderLogic == mIdleRendering) {
                mRenderLogic = mSlidingOutOfTextRendering;
            }

            slideOutOfText();
            if (mRenderLogic == mIdleRendering) {
                mRenderLogic = mRotateObjectRendering;
                }
            }

    };

    public PanoramaRenderer(Context context, int displayRotation, HouseManager houseManager) {
        super(context);

        NextPanoramaDataBuilder.resetData();

        mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        Sensor rotSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        mHouseManager = houseManager;
        mImageManager = new ImageMgmt();

        mCamera = getCurrentCamera();

        switch (displayRotation) {
            case Surface.ROTATION_0:
            case Surface.ROTATION_180:
                mCamera.setFieldOfView(FOV_PORTRAIT);
                break;
            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
                mCamera.setFieldOfView(FOV_LANDSCAPE);
                break;
            default:
                mCamera.setFieldOfView(FOV_PORTRAIT);
                displayRotation = Surface.ROTATION_0;
                break;
        }

        mCamera.setFarPlane(220);

        if (rotSensor == null) {
            LogHelper.log(TAG, "No rotSensor available");

            mRotListener = null;
            mRotSensor = null;
            mRotSensorAvailable = false;
        } else {
            mRotListener = new RotSensorListener(displayRotation, this);
            mRotSensor = rotSensor;
            mRotSensorAvailable = true;
        }

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        mXdpi = displayMetrics.xdpi;
        mYdpi = displayMetrics.ydpi;

        mUserRot = new Quaternion();
        mSensorRot = new Quaternion();
        mYaw = 0;

        mPanoSphere = null;

        mRenderLogic = getIdleRendering();
        mTargetPos = ORIGIN;

        mPicker = new ObjectColorPicker(this);
        mPicker.setOnObjectPickedListener(this);

        setFrameRate(60);
    }

    /**
     * Helper method to gradually rotate the target object by 45° and gradually change the color of it.
     */
    private void rotateTarget() {
        double rateOfRotation = 15.0;
        double angle = 180 / (4.0 * rateOfRotation);

        //Take the Vector from the object to the origin (0 - ObjectCoordinates )
        objectToRotate.rotate(-objectToRotate.getX(), -objectToRotate.getY(), -objectToRotate.getZ(), angle);
        currentAngle += angle;
        ArgbEvaluator argbEvaluator = new ArgbEvaluator();
        rotationPercent += 1 / rateOfRotation;
        objectToRotate.setColor((Integer) argbEvaluator.evaluate(rotationPercent, startingColor, finishColor));

        if (currentAngle >= targetAngle) {
            rotationPercent = 0;
            mRenderLogic = mIdleRendering;
            LogHelper.log(TAG, "Rotation finished");
        }

    }

    /**
     * Helper method to gradually move the camera to the center.
     */
    private void slideOutOfText() {
        Log.i(TAG, "Zoom out in progress");
        double travellingLength = mCamera.getPosition().length();
        if (travellingLength > 0.01) {
            Vector3 pos = new Vector3(mCamera.getPosition());
            mCamera.setPosition(pos.lerp(ORIGIN, LERP_FACTOR * 5));
        } else {
            mRenderLogic = getIdleRendering();
            LogHelper.log(TAG, "Movement Terminated");
        }
        Quaternion q = new Quaternion(mSensorRot);
        q = q.multiply(mUserRot);

        mHelperQuaternion = mHelperQuaternion.slerp(q, LERP_FACTOR * 5);
        mCamera.setCameraOrientation(mHelperQuaternion);
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
        LogHelper.log(TAG, "Call to initiate panorama transition, creating new task and setting next id.");
        mRenderLogic = getSlidingRendering();

        NextPanoramaDataBuilder.setNextPanoId(id);
        mImageLoadTask = new FetchPhotoTask();
        mImageManager.getBitmapFromUrl(mContext, url, mImageLoadTask);
    }

    /**
     * Display the text passed in argument, into a PanoramaInfoDisplay that will be displayed at the
     * angle passed in argument. The object created will be linked to the PanoramaInfoObject.
     *
     * @param textInfo           The text to be displayed.
     * @param theta              The angle of the PanoramaInfoDisplay.
     * @param panoramaInfoObject The panoramaInfoObject that activated the display of the text.
     */
    public void displayText(String textInfo, double theta, PanoramaInfoObject panoramaInfoObject) {
        LogHelper.log(TAG, "Call to display text information.");
        mPanoSphere.createTextDisplay(textInfo, theta, panoramaInfoObject);
    }

    /**
     * Delete the PanoramaInfoDisplay passed in argument from the scene.
     *
     * @param panoramaInfoDisplay The panoramaInfoDisplay to be deleted.
     */
    public void deleteInfo(PanoramaInfoDisplay panoramaInfoDisplay) {
        LogHelper.log(TAG, "Call to delete text information.");
        mPanoSphere.deleteTextToDisplay(panoramaInfoDisplay);
    }

    /**
     * Rotate the object passed in argument by 45° and change the color of the PanoramaInfoObject
     * passed in argument from Blue to Red or Red to Blue.
     *
     * @param panoramaInfoObject
     */
    public void rotatePanoramaInfoObject(PanoramaInfoObject panoramaInfoObject) {
        objectToRotate = panoramaInfoObject;
        targetAngle = currentAngle + 180 / 4.0;
        if (panoramaInfoObject.isDisplay()) {
            startingColor = COLOR_CLOSE;
            finishColor = TEXTURE_COLOR;
        } else {
            startingColor = TEXTURE_COLOR;
            finishColor = COLOR_CLOSE;
        }
        mRenderLogic = mRotateObjectRendering;
    }

    /**
     * Zoom out (move the camera to the center) and rotate the object and change the color of the
     * PanoramaInfoObject passed in argument.
     *
     * @param theta the current angle to which the camera is looking at.
     * @param panoramaInfoObject the PanoramaInfoObject to rotate
     */
    public void zoomOutAndRotate(double theta, PanoramaInfoObject panoramaInfoObject) {
        rotatePanoramaInfoObject(panoramaInfoObject);
        zoomOut(theta);
        mRenderLogic = mRotateObjectAndZoomOutRendering;
    }

    /**
     * Zoom on (move the camera) to a certain position and with a certain orientation.
     *
     * @param angle The target orientation of the camera
     * @param x The target X position.
     * @param z The target Z position.
     */
    public void zoomOnText(double angle, double x, double z) {
        mTargetPos = mTargetPos.setAll(x, 25, z);
        LogHelper.log(TAG, "Moving to:" + x + " , " + z);

        mTargetQuaternion = Quaternion.getIdentity().fromEuler(angle * 180 / Math.PI + 90, 0, 0);

        Quaternion q = new Quaternion(mSensorRot);
        mHelperQuaternion = q.multiply(mUserRot);
        mRenderLogic = getSlidingToTextRendering();
    }

    /**
     * Zoom out the camera (change the position of the camera to the center) and gradually change
     * the orientation of the camera to which the user is currently watching.
     *
     * @param angle the current angle to which the camera is pointing at.
     */
    public void zoomOut(double angle) {
        LogHelper.log(TAG, "Moving Out");
        mHelperQuaternion = Quaternion.getIdentity().fromEuler(angle * 180 / Math.PI + 90, 0, 0);
        mRenderLogic = getSlidingOutOfTextRendering();
    }


    /**
     * Callback method that should be called by the asynchronous task fetching the panorama picture.
     * It will save the bitmap inside the NextPanoramaBuilder class
     *
     * @param b The panorama picture
     */
    public void prepareScene(Bitmap b) {
        if (b == null) {
            handleFailure();
        } else {
            LogHelper.log(TAG, "Call to prepare scene, assigning next bitmap.");
            NextPanoramaDataBuilder.setNextPanoBitmap(b);
        }
    }


    /**
     * Actually updates the current scene by changing the panorama picture and loading the new PanoramaComponents.
     * This method should be called when we have gathered all the needed data the transition between two panoramas
     * and that we want to update the scene or in other terms what the user actually sees.
     */
    public void updateScene() {

        Tuple<Integer, Bitmap> panoData = NextPanoramaDataBuilder.build();
        LogHelper.log(TAG, "Call to update scene, changing panorama photo and attaching new components." +
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

    /**
     * A call to this method will ask the Picasso library to cancel the task of fetching the next photo sphere
     * texture, be it on disk or over the network
     */
    public void cancelPanoramaUpdate() {
        Picasso.with(mContext).cancelRequest(mImageLoadTask);
        NextPanoramaDataBuilder.resetData();
    }

    /**
     * In case something bad happened during a transition or some other action, a call to this method will reset user
     * position as well as any stored data about the next requested panorama
     */
    public void handleFailure() {
        mCamera.setPosition(ORIGIN);
        mRenderLogic = getIdleRendering();
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
     * quaternion. Note that a defensive copy is needed because quaternions are mutable objects.
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
     * Updates the device yaw information. Yaw represents how much the device is tilted to the left or the right when
     * you position it in front of you, in a vertical orientation.
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

    public RenderingLogic getRotateObjectAndZoomOutRendering() {
        return mRotateObjectAndZoomOutRendering;
    }

    public RenderingLogic getRotateObjectRendering() {
        return mRotateObjectRendering;
    }

    @Override
    public void initScene() {

        LogHelper.log(TAG, "Initializing scene");

        Picasso.with(mContext).setLoggingEnabled(true);

        mCamera.setPosition(new Vector3(0, 0, 0));
        mPanoSphere = new PanoramaSphere(mPicker);
        getCurrentScene().addChild(mPanoSphere);

        NextPanoramaDataBuilder.setNextPanoId(mHouseManager.getStartingId());
        prepareScene(mImageManager.getBitmapFromUrl(getContext(), mHouseManager.getStartingUrl()));
        LogHelper.log(TAG, "Updating scene from initscene");
        updateScene();
    }

    @Override
    public void onRender(final long elapsedTime, final double deltaTime) {
        super.onRender(elapsedTime, deltaTime);
        mRenderLogic.render();
        if (mRenderLogic != mSlidingToTextRendering && mRenderLogic != mSlidingOutOfTextRendering && mRenderLogic !=
                mRotateObjectAndZoomOutRendering) {
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
            LogHelper.log(TAG, "ObjectPicked");
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
    public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep,
                                 int xPixelOffset, int yPixelOffset) {
    }


    /**
     * This interface defines a behavior of the PanoramaRenderer. Change in behavior can happen due to user input for
     * displaying a transition for example. When computing a new frame, the 3D engine will call the onRender method
     * of the PanoramaRenderer class. This method in turn uses a rendering logic to modify the state of the 3D scene
     */

    public interface RenderingLogic {
        void render();
    }

    /**
     * Builder for the data needed to transition between panoramas.
     * The data consists of the ID of the next panorama (as tracked by the house manager) to load as well as the bitmap
     * that represent the panorama texture to be applied to the panorama sphere
     */
    public static final class NextPanoramaDataBuilder {
        static final int INVALID_ID = -1;
        static final Bitmap INVALID_BITMAP = null;
        private static final String TAG = "NextPanoDataBuilder";
        private static Integer nextPanoId = INVALID_ID;
        private static Bitmap nextPanoBitmap = INVALID_BITMAP;

        private NextPanoramaDataBuilder() {
        }

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
         * Build data corresponding to the next panorama to load. Two consecutive calls to this method is invalid as
         * the first one will have erased the data stored by the class.
         *
         * @return the built PanoramaData under the shape of a tuple
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
            LogHelper.log(TAG, "ID: " + nextPanoId + ", Bitmap@" + nextPanoBitmap);
        }
    }

    /**
     * Target for Picasso's asynchronous image loading. When asking the Picasso library to fetch images online or on
     * the disk, this class is given as a "Target". This means that this class is used upon completion of the Picasso
     * task.
     */
    private class FetchPhotoTask implements Target {

        private static final String TAG = "FetchPhotoTask";

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            prepareScene(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            LogHelper.log(TAG, "Picasso async bitmap load failed");
            handleFailure();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }
}
