package ch.epfl.sweng.project.util;


import android.annotation.SuppressLint;
import android.util.Log;

import org.rajawali3d.cameras.Camera;
import org.rajawali3d.math.Quaternion;

import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.engine3d.PanoramaRenderer;

public final class DebugPrinter {

    private DebugPrinter() {
    }

    @SuppressLint("LogConditional")
    public static void printRendererDebug(String tag, PanoramaRenderer pr) {

        Camera cam = pr.getCurrentCamera();
        Quaternion sensorRot = pr.getSensorRot();

        Quaternion q = cam.getOrientation();
        double camRX = q.getRotationX();
        double camRY = q.getRotationY();
        double camRZ = q.getRotationZ();

        Log.d(tag, String.format(pr.getContext().getString(R.string.debug_camera_rotation), camRX, camRY, camRZ));

        double senRX = sensorRot.getRotationX();
        double senRY = sensorRot.getRotationY();
        double senRZ = sensorRot.getRotationZ();

        Log.d(tag, String.format(pr.getContext().getString(R.string.debug_sensor_rotation), senRX, senRY, senRZ));
    }

}
