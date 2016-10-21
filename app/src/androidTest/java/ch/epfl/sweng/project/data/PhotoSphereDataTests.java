package ch.epfl.sweng.project.data;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.parse.ParseObject;
import com.parse.ParseUser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.project.util.TestUtilityFunctions;
import ch.epfl.sweng.project.util.Tuple;

import static ch.epfl.sweng.project.util.TestUtilityFunctions.wait250ms;
import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class PhotoSphereDataTests {

    private static boolean parseInit = false;
    final Context context = InstrumentationRegistry.getTargetContext();
    private static final String TAG = "PhotoSphereDataTests";

    @Before
    public void init() {
        if (!parseInit) {
            ParseObject.registerSubclass(Resources.class);

            TestUtilityFunctions.initializeParse(context);
            ParseUser.enableAutomaticUser();
            parseInit = true;
        }
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

        PhotoSphereData photoSphereData = new PhotoSphereData(14145, "14145.jpg", neighborsList);

        /*
        for (int i = 0; i < neighborsList.size(); i++) {
            assertEquals(neighborsList.get(i).getPhi(), neighborsList.get(i).getPhi());
            assertEquals(neighborsList.get(i).getTheta(), neighborsList.get(i).getTheta());
            assertEquals(neighborsList.get(i).getId(), neighborsList.get(i).getId());
            assertEquals(neighborsList.get(i).getUrl(), neighborsList.get(i).getUrl());
        }*/

        Resources testResources = new Resources();

        ArrayList<PhotoSphereData> photoSphereDatas = new ArrayList<>();
        photoSphereDatas.add(new PhotoSphereData(14145, "14145.jpg", neighborsList));
        photoSphereDatas.add(new PhotoSphereData(14146, "14146.jpg", neighborsList));
        photoSphereDatas.add(new PhotoSphereData(14145, "14145.jpg", neighborsList));
        testResources.setNeighborsList(photoSphereDatas);

        testResources.setDescription("THISisATest");

        testResources.saveInBackground();

        wait250ms(TAG);
    }


}
