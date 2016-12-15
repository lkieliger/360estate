package ch.epfl.sweng.project.util;


import org.rajawali3d.math.Quaternion;

import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.engine3d.PanoramaRenderer;

public final class DebugPrinter {

    private DebugPrinter() {
    }

    /**
     * This method should be called by a PanoramaRenderer object. It implements all the logic about the printing
     * of useful parameters in the logcat.
     *
     * @param tag The tag of the panorama renderer, which will be transmitted to the LogHelper.log() method
     * @param pr  A reference to the PanoramaRenderer in order to extract all the useful information
     */
    public static void printRendererDebug(String tag, PanoramaRenderer pr) {


        Quaternion sensorRot = pr.getSensorRot();

        Quaternion q = pr.getUserRotation();
        double camRX = q.getRotationX();
        double camRY = q.getRotationY();
        double camRZ = q.getRotationZ();

        LogHelper.log(tag, String.format(pr.getContext().getString(R.string.debug_camera_rotation),
                camRX, camRY, camRZ));

        double senRX = sensorRot.getRotationX();
        double senRY = sensorRot.getRotationY();
        double senRZ = sensorRot.getRotationZ();

        LogHelper.log(tag, String.format(pr.getContext().getString(R.string.debug_sensor_rotation),
                senRX, senRY, senRZ));
    }

}
