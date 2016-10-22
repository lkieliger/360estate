package ch.epfl.sweng.project.data;

import android.content.Context;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.data.util.TestUtilityFunctions;
import ch.epfl.sweng.project.util.Tuple;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class SampleRoboelectricTest {

    static boolean parseInit = false;

    @Before
    public void init() {
        if (!parseInit) {
            Context context = ShadowApplication.getInstance().getApplicationContext();
            Parse.enableLocalDatastore(context);

            ParseObject.registerSubclass(PhotoSphereData.class);
            TestUtilityFunctions.initializeParseLocal(context);
            ParseUser.enableAutomaticUser();

            parseInit = true;
        }
    }

    @Test
    public void testNeighbors() throws Exception {
        List<AngleMapping> angleMappingList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            angleMappingList.add(new AngleMapping(
                    new Tuple<>(0.14d + i / 1000d, 0.10d + i / 1000d),
                    i,
                    i + ".jpg"));
        }

        PhotoSphereData photoSphereDataObject = new PhotoSphereData(id, url, neighborsList);
        photoSphereDataObject.getNeighborsList(angleMappingList);

        photoSphereDataObject.setId(12345);

        /*
        photoSphereDataObject.save();

        TestUtilityFunctions.wait250ms("DEBUG TAG");

        List<AngleMapping> neighborsList = photoSphereDataObject.getNeighborsJsonArray();

        for (int i = 0; i < angleMappingList.size(); i++) {
            assertEquals(neighborsList.get(i).getPhi(), angleMappingList.get(i).getPhi());
            assertEquals(neighborsList.get(i).getTheta(), angleMappingList.get(i).getTheta());
            assertEquals(neighborsList.get(i).getId(), angleMappingList.get(i).getId());
            assertEquals(neighborsList.get(i).getUrl(), angleMappingList.get(i).getUrl());
        }*/
    }

}
