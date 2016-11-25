package ch.epfl.sweng.project.features.propertyDescription.slider;
import android.support.v4.view.ViewPager;
import android.view.View;

public final class ZoomOutTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;
    @Override
    public void transformPage(View view, float position) {
        int width = view.getWidth();
        int height = view.getHeight();

        if(position < -1){
            view.setAlpha(0);
        }else if(position <= 1){
            float scale = Math.max(MIN_SCALE, 1- Math.abs(position));
            float verticalMargin = height*(1-scale)/2;
            float horizontalMargin = width*(1-scale)/2;
            if(position < 0){
                view.setTranslationX(horizontalMargin-verticalMargin / 2);
            }else{
                view.setTranslationY(-horizontalMargin + verticalMargin /2 );
            }
            view.setScaleX(scale);
            view.setScaleY(scale);

            view.setAlpha(MIN_ALPHA+ (scale - MIN_SCALE)/(1 - MIN_SCALE)*(1 - MIN_ALPHA));
        }else{
            view.setAlpha(0);
        }
    }
}
