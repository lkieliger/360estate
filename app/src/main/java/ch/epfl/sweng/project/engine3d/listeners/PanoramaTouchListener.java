package ch.epfl.sweng.project.engine3d.listeners;

import android.view.MotionEvent;
import android.view.View;

import ch.epfl.sweng.project.engine3d.PanoramaRenderer;
import ch.epfl.sweng.project.util.LogHelper;

import static android.view.MotionEvent.INVALID_POINTER_ID;

public final class PanoramaTouchListener implements View.OnTouchListener {

    private static final String TAG = "PanoramaTouchListener";
    public final int SCROLL_THRESHOLD = 10;
    private final PanoramaRenderer mRenderer;
    private boolean posValueIsValid = false;
    private boolean isOnClick = false;
    private float lastX = 0.0F;
    private float lastY = 0.0F;

    private int mActivePointerId = 0;

    public PanoramaTouchListener(PanoramaRenderer renderer) {
        if (renderer == null) {
            throw new IllegalArgumentException("Renderer reference was null");
        }
        mRenderer = renderer;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        //use masked version to withstand multiple touches from user
        final int action = event.getActionMasked();
        final int pointerIndex = event.getActionIndex();
        final int pointerId = event.getPointerId(pointerIndex);
        final float x = event.getX(pointerIndex);
        final float y = event.getY(pointerIndex);

        LogHelper.log(TAG, "Event values: pointer index: " + pointerIndex + " pointer id: " +
                event.getPointerId(pointerIndex) + " active id: " + mActivePointerId + " x:" + x + " y:" + y);

        switch (action) {

            case MotionEvent.ACTION_DOWN:
                LogHelper.log(TAG, "ACTION DOWN");
                // Remember where we started (for dragging)
                lastX = x;
                lastY = y;
                // Save the ID of this pointer (for dragging)
                mActivePointerId = pointerId;
                isOnClick = true;
                return true;

            case MotionEvent.ACTION_MOVE:
                // Remember this touch position for the next move event
                if (pointerId == mActivePointerId) {
                    LogHelper.log(TAG, "ACTION MOVE");
                    // Calculate the distance moved
                    LogHelper.log(TAG, "Calculating dx with 0");
                    float dx = x - lastX;
                    float dy = y - lastY;

                    lastX = x;
                    lastY = y;

                    //Check if movement has a valid last touch position
                    if (posValueIsValid) {
                        if (isOnClick && (Math.abs(dx) > SCROLL_THRESHOLD || Math.abs(dy) >
                                SCROLL_THRESHOLD)) {
                            LogHelper.log(TAG, "movement detected");
                            isOnClick = false;
                        }
                        mRenderer.updateCameraRotation(dx, dy);
                    }
                    posValueIsValid = true;
                }
                return true;

            case MotionEvent.ACTION_UP:
                LogHelper.log(TAG, "ACTION UP");
                mActivePointerId = INVALID_POINTER_ID;

                v.performClick();
                if (isOnClick) {
                    LogHelper.log(TAG, "CLICK");
                    mRenderer.getObjectAt(event.getX(), event.getY());
                }
                return true;

            case MotionEvent.ACTION_CANCEL:
                LogHelper.log(TAG, "ACTION CANCEL");
                mActivePointerId = INVALID_POINTER_ID;
                return true;

            case MotionEvent.ACTION_POINTER_DOWN:
                LogHelper.log(TAG, "ACTION POINTER DOWN");

                posValueIsValid = false;
                mActivePointerId = 0;

                return true;

            case MotionEvent.ACTION_POINTER_UP:
                LogHelper.log(TAG, "ACTION POINTER UP");

                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new active pointer and adjust accordingly.
                    LogHelper.log(TAG, "Changing pointer index");

                    posValueIsValid = false;
                    mActivePointerId = event.getPointerId(pointerIndex == 0 ? 1 : 0);
                }
                return true;

            default:
                return false;
        }
    }
}
