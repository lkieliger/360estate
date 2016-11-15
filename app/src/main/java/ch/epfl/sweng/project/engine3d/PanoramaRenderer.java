package ch.epfl.sweng.project.engine3d;


import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;

import com.squareup.picasso.Picasso;

import org.rajawali3d.Object3D;
import org.rajawali3d.cameras.Camera;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.util.ObjectColorPicker;
import org.rajawali3d.util.OnObjectPickedListener;

import java.io.IOException;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.DataMgmt;
import ch.epfl.sweng.project.data.HouseManager;
import ch.epfl.sweng.project.engine3d.components.PanoramaObject;
import ch.epfl.sweng.project.engine3d.components.PanoramaSphere;
import ch.epfl.sweng.project.engine3d.listeners.RotSensorListener;
import ch.epfl.sweng.project.util.DebugPrinter;
import ch.epfl.sweng.project.util.Tuple;

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
    private Quaternion mUserRot;
    private Quaternion mSensorRot;
    private Vector3 mTargetPos;
    private double mYaw;

    private HouseManager mHouseManager;
    private RenderingLogic mRenderLogic;
    private ObjectColorPicker mPicker;
    private int debugCounter = 0;

    /**
     * Use this rendering when nothing special need to be done. In other words just allowing the camera to look
     * around and print some debug informations.
     */
    private RenderingLogic mIdleRendering = new RenderingLogic() {
        @Override
        public void render() {
            if (debugCounter == 60) {
                debugCounter = 0;
                DebugPrinter.printRendererDebug(TAG, PanoramaRenderer.this);
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
            updateScene();
            mCamera.setPosition(ORIGIN);
            mRenderLogic = mIdleRendering;
        }
    };
    /**
     * Use this rendering logic to gradually move the camera toward the TransitionObject target
     */
    private RenderingLogic mSlidingRendering = new RenderingLogic() {
        @Override
        public void render() {
            double travellingLength = mCamera.getPosition().length();

            if (travellingLength < 65) {
                Vector3 v = new Vector3(mTargetPos.x, 0, mTargetPos.z);
                Vector3 pos = new Vector3(mCamera.getPosition());
                mCamera.setPosition(pos.lerp(v, LERP_FACTOR));
            }
            if (travellingLength >= 65 && NextPanoramaDataBuilder.isReady()) {
                mRenderLogic = mTransitioningRendering;
            }
        }
    };

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

        mRenderLogic = mIdleRendering;
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

        prepareScene(DataMgmt.getBitmapFromUrl(getContext(), mHouseManager.getStartingUrl()), mHouseManager
                .getStartingId());
        updateScene();
    }

    /**
     * Update the current scene by changing the panorama picture and loading the new PanoramaComponents
     */
    public void updateScene() {
        Log.d(TAG, "Call to update scene");

        Tuple<Integer, Bitmap> panoData = NextPanoramaDataBuilder.build();

        mPanoSphere.detachPanoramaComponents(mPicker);
        mPanoSphere.setPhotoTexture(panoData.getY());
        mPanoSphere.attachPanoramaComponents(mHouseManager.getNeighborsFromId(panoData.getX()), mPicker);
    }

    private void prepareScene(Bitmap b, int id) {
        Log.d(TAG, "Call to prepare scene, assigning next bitmap and next id.");

        NextPanoramaDataBuilder.setNextPanoBitmap(b);
        NextPanoramaDataBuilder.setNextPanoId(id);
    }

    public void updatePanorama(String url, int id) {
        Log.d(TAG, "Call to update panorama, creating new task.");
        new FetchPhotoTask().execute(url, String.valueOf(id));
    }

    @Override
    public void onRender(final long elapsedTime, final double deltaTime) {
        super.onRender(elapsedTime, deltaTime);
        mRenderLogic.render();
        updateCamera();
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

    public void getObjectAt(float x, float y) {
        mPicker.getObjectAt(x, y);
    }

    @Override
    public void onObjectPicked(@NonNull Object3D object) {
        Log.d(TAG, "ObjectPicked");
        mTargetPos = object.getWorldPosition();
        ((PanoramaObject) object).reactWith(this);
        mRenderLogic = mSlidingRendering;
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

    private interface RenderingLogic {
        void render();
    }

    /**
     * Builder for the data needed to transition between panoramas.
     */
    public static final class NextPanoramaDataBuilder {
        public static final int INVALID_ID = -1;
        public static Bitmap INVALID_BITMAP = null;
        private static Integer nextPanoId = INVALID_ID;
        private static Bitmap nextPanoBitmap = INVALID_BITMAP;

        public static boolean isReady() {
            return (nextPanoId != -1 && nextPanoBitmap != null);
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
                throw new IllegalStateException("Next panorama id should not be set multiple times");
            }
            nextPanoBitmap = b;
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
        }
    }

    /**
     * Asychronous task used to retrieve a bitmap on the network using Picasso
     * <p>
     * We tried to use directly the Picasso feature for retrieving bitmap asynchronously and storing them in a Target
     * object but this did not work. This class acts in the same way however.
     */
    private class FetchPhotoTask extends AsyncTask<String, Void, Bitmap> {

        private int id;
        private String url;

        @Override
        protected Bitmap doInBackground(String... paramses) {

            url = paramses[0];
            id = Integer.valueOf(paramses[1]);

            Picasso.Builder builder = new Picasso.Builder(getContext());
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, exception.getMessage());
                    }
                }
            });

            Bitmap ret = null;
            try {
                ret = builder.build().with(getContext()).load(url).resize(2048, 4096).get();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }

            //TODO: perform some extensives checks for null pointers
            return ret;
        }

        @Override
        protected void onPostExecute(Bitmap b) {
            prepareScene(b, id);
        }
    }
}
