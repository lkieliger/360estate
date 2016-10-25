package ch.epfl.sweng.project.data;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.parse.ParseObject;
import com.parse.ParseUser;

import org.junit.After;
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
public class ResourcesTests {

    private static final String TAG = "Resources Test";


    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Before
    public void setup() {
        ParseObject.registerSubclass(Resources.class);
    }

    @After
    public void finishActivity() {
        mActivityTestRule.getActivity().finish();
        wait1s(TAG);
    }

    @Test
    public void testNeighbors() {

        Resources testResources = new Resources();

        List<AngleMapping> neighborsList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            neighborsList.add(new AngleMapping(
                    new Tuple<>(0.14d + i / 1000d, 0.10d + i / 1000d),
                    i,
                    i + ".jpg"));
        }

        List<PhotoSphereData> photoSphereDatas = new ArrayList<>();
        photoSphereDatas.add(new PhotoSphereData(14145, "14145.jpg", neighborsList));
        photoSphereDatas.add(new PhotoSphereData(14146, "14146.jpg", neighborsList));
        photoSphereDatas.add(new PhotoSphereData(14145, "14145.jpg", neighborsList));
        testResources.setPhotoSphereDatas(photoSphereDatas);

        List<String> urlList = new ArrayList<>(3);
        urlList.add("https://360.estate.org/estate/houseSmall.jpg");
        urlList.add("https://360.estate.org/estate/houseBig.jpg");
        testResources.setPicturesUrlList(urlList);

        testResources.setDescription("THISisATest");

        List<PhotoSphereData> photoSphereDatas1 = testResources.getPhotoSphereDatas();

        assertEquals(photoSphereDatas.size(), photoSphereDatas1.size());

       for (int i = 0; i < photoSphereDatas1.size(); i++) {
            assertTrue((photoSphereDatas.get(i)).equals(photoSphereDatas1.get(i)));
        }

        assertEquals("THISisATest", testResources.getDescription());

        List<String> urlList1 = testResources.getPicturesList();

        for (int i = 0; i < urlList.size(); i++) {
            assertEquals(urlList.get(i), urlList1.get(i));
        }

        wait250ms(TAG);
    }


}
