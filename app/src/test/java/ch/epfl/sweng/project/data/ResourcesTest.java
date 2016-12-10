package ch.epfl.sweng.project.data;

import android.util.Log;

import com.parse.ParseObject;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.data.panorama.PhotoSphereData;
import ch.epfl.sweng.project.data.panorama.adapters.SpatialData;
import ch.epfl.sweng.project.data.panorama.adapters.TransitionObject;
import ch.epfl.sweng.project.data.parse.objects.JSONTags;
import ch.epfl.sweng.project.data.parse.objects.Resources;
import ch.epfl.sweng.project.engine3d.components.PanoramaComponentType;
import ch.epfl.sweng.project.engine3d.components.PanoramaObject;
import ch.epfl.sweng.project.util.Tuple;

import static ch.epfl.sweng.project.data.parse.objects.JSONTags.neighborsListTag;
import static ch.epfl.sweng.project.data.parse.objects.JSONTags.panoSphereDatasTag;
import static ch.epfl.sweng.project.data.parse.objects.JSONTags.panoramaRoomsTag;
import static ch.epfl.sweng.project.data.parse.objects.JSONTags.typeTag;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)

public class ResourcesTest {

    private static final String TAG = "Resources tests:";

    @Before
    public void setup() {
        ParseObject.registerSubclass(Resources.class);
    }

    @SuppressWarnings("ObjectEqualsNull")
    @Test
    public void throwsJsonError() {

        Resources resources = new Resources();

        resources.put(JSONTags.picturesListTag, "ThisIsNotAnArry");
        try {
            assertTrue(resources.getPicturesList().isEmpty());
        } catch (JSONException e) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, e.getMessage());

        }
    }

    @Test
    public void resourcesCorrectBehavior() throws JSONException {

        Resources testResources = new Resources();

        List<SpatialData> neighborsList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            neighborsList.add(new TransitionObject(
                    new Tuple<>(0.14d + i / 1000d, 0.10d + i / 1000d),
                    i,
                    i + ".jpg"));
        }

        List<PhotoSphereData> photoSphereDatas = new ArrayList<>();
        photoSphereDatas.add(new PhotoSphereData(14145, neighborsList));
        photoSphereDatas.add(new PhotoSphereData(14146, neighborsList));
        photoSphereDatas.add(new PhotoSphereData(14145, neighborsList));
        testResources.setPhotoSphereDatas(photoSphereDatas, 112358, "blabla");


        List<String> urlList = new ArrayList<>(3);
        urlList.add("https://360.estate.org/estate/houseSmall.jpg");
        urlList.add("https://360.estate.org/estate/houseBig.jpg");
        testResources.setPicturesUrlList(urlList);

        testResources.setDescription("THISisATest");

        List<PhotoSphereData> photoSphereDatas1 = null;
        try {
            photoSphereDatas1 = testResources.getPhotoSphereDatas();
        } catch (JSONException e) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "JSON Exception:" + e.getMessage());
            }
        }
        Assert.assertEquals(photoSphereDatas.size(), photoSphereDatas1.size());

        for (int i = 0; i < photoSphereDatas1.size(); i++) {
            Assert.assertTrue((photoSphereDatas.get(i)).equals(photoSphereDatas1.get(i)));
        }

        Assert.assertEquals("THISisATest", testResources.getDescription());

        List<String> urlList1 = null;
        try {
            urlList1 = testResources.getPicturesList();
        } catch (JSONException e) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, e.getMessage());

        }

        for (int i = 0; i < urlList.size(); i++) {
            Assert.assertEquals(urlList.get(i), urlList1.get(i));
        }
    }

    @Test (expected = JSONException.class)
    public void resourcesThrowsExpectionForTypeTag() throws JSONException {
        Resources testResources = new Resources();

        List<SpatialData> neighborsList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            neighborsList.add(new TransitionObject(
                    new Tuple<>(0.14d + i / 1000d, 0.10d + i / 1000d),
                    i,
                    i + ".jpg"));
        }

        Collection<PhotoSphereData> photoSphereDatas = new ArrayList<>();
        photoSphereDatas.add(new PhotoSphereData(14145, neighborsList));
        testResources.setPhotoSphereDatas(photoSphereDatas, 112358, "blabla");

        JSONObject panoSphereDatas = testResources.getJSONObject(panoSphereDatasTag);
        JSONArray photoSphereDataArray = panoSphereDatas.getJSONArray(panoramaRoomsTag);
        JSONObject photoSphereObject = (JSONObject) photoSphereDataArray.get(0);
        JSONArray neighborsJSONArray = photoSphereObject.getJSONArray(neighborsListTag);

        ((JSONObject)neighborsJSONArray.get(0)).put(typeTag, Integer.MAX_VALUE);
        testResources.put(panoSphereDatasTag, panoSphereDatas);

        testResources.getPhotoSphereDatas();
    }

    @Test (expected = JSONException.class)
    public void resourcesThrowsExceptionForNegType() throws JSONException {
        Resources testResources = new Resources();

        List<SpatialData> neighborsList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            neighborsList.add(new TransitionObject(
                    new Tuple<>(0.14d + i / 1000d, 0.10d + i / 1000d),
                    i,
                    i + ".jpg"));
        }

        Collection<PhotoSphereData> photoSphereDatas = new ArrayList<>();
        photoSphereDatas.add(new PhotoSphereData(14145, neighborsList));
        testResources.setPhotoSphereDatas(photoSphereDatas, 112358, "blabla");

        JSONObject panoSphereDatas = testResources.getJSONObject(panoSphereDatasTag);
        JSONArray photoSphereDataArray = panoSphereDatas.getJSONArray(panoramaRoomsTag);
        JSONObject photoSphereObject = (JSONObject) photoSphereDataArray.get(0);
        JSONArray neighborsJSONArray = photoSphereObject.getJSONArray(neighborsListTag);

        ((JSONObject)neighborsJSONArray.get(0)).put(typeTag, -1);
        testResources.put(panoSphereDatasTag, panoSphereDatas);

        testResources.getPhotoSphereDatas();
    }
}
