package ch.epfl.sweng.project.engine3d;

import android.view.MotionEvent;
import android.view.View;

import static android.view.MotionEvent.INVALID_POINTER_ID;

class PanoramaTouchListener implements View.OnTouchListener {

    private final PanoramaRenderer mRenderer;

    private float mLastTouchX = 0.0F;
    private float mLastTouchY = 0.0F;

    private int mActivePointerId = 0;

    public PanoramaTouchListener(PanoramaRenderer renderer){
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
                return true;

            case MotionEvent.ACTION_MOVE:
                // Calculate the distance moved
                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;

                mRenderer.updateCameraRotation(dx, dy);

                // Remember this touch position for the next move event
                mLastTouchX = x;
                mLastTouchY = y;


                return true;

            case MotionEvent.ACTION_UP:
                mActivePointerId = INVALID_POINTER_ID;
                v.performClick();
                return true;

            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = INVALID_POINTER_ID;
                return true;


            case MotionEvent.ACTION_POINTER_UP:
                final int pointerId = event.getPointerId(pointerIndex);

                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
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
