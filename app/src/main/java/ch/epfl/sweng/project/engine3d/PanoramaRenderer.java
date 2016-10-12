package ch.epfl.sweng.project.engine3d;


import android.content.Context;
import android.view.MotionEvent;

import org.rajawali3d.renderer.Renderer;

public class PanoramaRenderer extends Renderer{

    public PanoramaRenderer(Context context){
        super(context);
    }

    @Override
    protected void initScene() {

    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {

    }

    @Override
    public void onTouchEvent(MotionEvent event) {

    }
}
