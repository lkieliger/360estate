package ch.epfl.sweng.project.ScreenSlide;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.project.R;

public class SlideActivity extends FragmentActivity {

    private static final int NUM_PAGES = 5;
    private ViewPager vPager;
    private PagerAdapter adapter;
    private int size;
    int urlIndex = 0;
    ArrayList<String> urls;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);

        Bundle bundle = getIntent().getExtras();
        urls = (ArrayList<String>) bundle.get("ArrayURL");
        int first = urls.indexOf((String) bundle.getString("URL"));
        size = urls.size();
        vPager = (ViewPager) findViewById(R.id.pager);
        adapter = new SlidePagerAdapter(getSupportFragmentManager());

        vPager.setAdapter(adapter);
        vPager.setCurrentItem(first);
        vPager.setPageTransformer(true, new ZoomOutTransformer());
    }
    private class SlidePagerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> fragments;

        public SlidePagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            for(int i= 0; i< size; i++){
                SlideFragment slideF = new SlideFragment();
                Bundle b = new Bundle();
                b.putString("url", getNextURL());
                slideF.setArguments(b);
                fragments.add(slideF);
            }
        }

        @Override
        public Fragment getItem(int position) {
            /*SlideFragment slideF = new SlideFragment();
            Bundle b = new Bundle();
            b.putString("url", getNextURL());
            slideF.setArguments(b);
            return slideF;*/
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        private String getNextURL(){
           // urlIndex = urlIndex % size;
            String res = urls.get(urlIndex);
            urlIndex ++;
            return res;
        }
    }
}
