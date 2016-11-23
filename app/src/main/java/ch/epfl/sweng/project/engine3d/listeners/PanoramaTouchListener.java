package ch.epfl.sweng.project.engine3d.listeners;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import ch.epfl.sweng.project.engine3d.PanoramaRenderer;

import static android.view.MotionEvent.INVALID_POINTER_ID;

public class PanoramaTouchListener implements View.OnTouchListener {

    private static final String TAG = "PanoramaTouchListener";
    private final PanoramaRenderer mRenderer;
    private final int SCROLL_THRESHOLD = 10;
    private boolean isOnClick = false;
    private float mLastTouchX = 0.0F;
    private float mLastTouchY = 0.0F;

    private int mActivePointerId = 0;

    public PanoramaTouchListener(PanoramaRenderer renderer){
        if(renderer == null ){
            throw new IllegalArgumentException("Renderer reference was null");
        }
        mRenderer = renderer;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        //use masked version to withstand multiple touches from user
        final int action = event.getActionMasked();
        final int pointerIndex = event.getActionIndex();
        final float x = event.getX(pointerIndex);
        final float y = event.getY(pointerIndex);

        switch(action) {

            case MotionEvent.ACTION_DOWN:

                // Remember where we started (for dragging)
                mLastTouchX = x;
                mLastTouchY = y;
                // Save the ID of this pointer (for dragging)
                mActivePointerId = event.getPointerId(pointerIndex);
                isOnClick = true;
                return true;

            case MotionEvent.ACTION_MOVE:
                // Calculate the distance moved
                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;

                mRenderer.updateCameraRotation(dx, dy);

                // Remember this touch position for the next move event
                mLastTouchX = x;
                mLastTouchY = y;

                if (isOnClick && (Math.abs(dx) > SCROLL_THRESHOLD || Math.abs(dy) >
                        SCROLL_THRESHOLD)) {
                    Log.i(TAG, "movement detected");
                    isOnClick = false;
                }

                return true;

            case MotionEvent.ACTION_UP:
                mActivePointerId = INVALID_POINTER_ID;
                v.performClick();
                if(isOnClick) {
                    Log.d(TAG, "CLIQUE");
                    mRenderer.getObjectAt(event.getX(), event.getY());
                }
                return true;

            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = INVALID_POINTER_ID;
                return true;


            case MotionEvent.ACTION_POINTER_UP:
                final int pointerId = event.getPointerId(pointerIndex);
                Log.i(TAG, "Detected a pointer up");
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    Log.i(TAG, "Changing pointer index");
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = event.getX();
                    mLastTouchY = event.getY();
                    mActivePointerId = event.getPointerId(newPointerIndex);
                }
                return true;

            default:
                return false;
        }
    }
}
