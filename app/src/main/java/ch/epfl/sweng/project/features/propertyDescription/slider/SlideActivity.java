package ch.epfl.sweng.project.features.propertyDescription.slider;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.project.R;

public final class SlideActivity extends FragmentActivity {

    private static int NUM_PAGES;
    int urlIndex = 0;
    ArrayList<String> urls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);

        Bundle bundle = getIntent().getExtras();
        urls = (ArrayList<String>) bundle.get("ArrayURL");
        int first = urls.indexOf(bundle.getString("URL"));
        NUM_PAGES = urls.size();
        ViewPager vPager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter adapter = new SlidePagerAdapter(getSupportFragmentManager());

        vPager.setAdapter(adapter);
        vPager.setCurrentItem(first);
        vPager.setPageTransformer(true, new ZoomOutTransformer());
    }

    private class SlidePagerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> fragments;

        SlidePagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
            for(int i= 0; i < NUM_PAGES; i++){
                SlideFragment slideF = new SlideFragment();
                Bundle b = new Bundle();
                b.putString("url", getNextURL());
                slideF.setArguments(b);
                fragments.add(slideF);
            }
        }

        @Override
        public Fragment getItem(int position) {
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
