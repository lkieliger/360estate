package ch.epfl.sweng.project.data;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.parse.ParseObject;
import com.parse.ParseUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.project.user.LoginActivity;
import ch.epfl.sweng.project.util.TestUtilityFunctions;
import ch.epfl.sweng.project.util.Tuple;

import static ch.epfl.sweng.project.util.TestUtilityFunctions.wait1s;
import static ch.epfl.sweng.project.util.TestUtilityFunctions.wait250ms;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class PhotoSphereDataTests {

    private static final String TAG = "PhotoSphereDataTests";


    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Before
    public void setup() {
        ParseObject.registerSubclass(Resources.class);
    }

    @Test
    public void testNeighbors() {
        List<AngleMapping> neighborsList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            neighborsList.add(new AngleMapping(
                    new Tuple<>(0.14d + i / 1000d, 0.10d + i / 1000d),
                    i,
                    i + ".jpg"));
        }

        Resources testResources = new Resources();

        List<PhotoSphereData> photoSphereDatas = new ArrayList<>();
        photoSphereDatas.add(new PhotoSphereData(14145, "14145.jpg", neighborsList));
        photoSphereDatas.add(new PhotoSphereData(14146, "14146.jpg", neighborsList));
        photoSphereDatas.add(new PhotoSphereData(14145, "14145.jpg", neighborsList));
        testResources.setPhotoSphereDatas(photoSphereDatas);

        testResources.setDescription("THISisATest");

        testResources.saveInBackground();

        List<PhotoSphereData> photoSphereDatas1 = testResources.getPhotoSphereDatas();

        assertEquals(photoSphereDatas.size(), photoSphereDatas1.size());

       for (int i = 0; i < photoSphereDatas1.size(); i++) {
            assertTrue((photoSphereDatas.get(i)).equals(photoSphereDatas1.get(i)));
        }

        wait250ms(TAG);
    }


}
