package ch.epfl.sweng.project.data;

import android.content.Context;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.data.panorama.HouseManager;
import ch.epfl.sweng.project.data.panorama.PhotoSphereData;
import ch.epfl.sweng.project.data.panorama.adapters.SpatialData;
import ch.epfl.sweng.project.data.panorama.adapters.TransitionObject;
import ch.epfl.sweng.project.data.parse.PInterface;
import ch.epfl.sweng.project.data.parse.ParseProxy;
import ch.epfl.sweng.project.data.parse.objects.Favorites;
import ch.epfl.sweng.project.data.parse.objects.Item;
import ch.epfl.sweng.project.data.parse.objects.Resources;
import ch.epfl.sweng.project.features.propertylist.adapter.ItemAdapter;
import ch.epfl.sweng.project.util.Tuple;

import static ch.epfl.sweng.project.data.parse.objects.JSONTags.neighborsListTag;
import static ch.epfl.sweng.project.data.parse.objects.JSONTags.panoSphereDatasTag;
import static ch.epfl.sweng.project.data.parse.objects.JSONTags.panoramaRoomsTag;
import static ch.epfl.sweng.project.data.parse.objects.JSONTags.typeTag;
import static ch.epfl.sweng.project.util.UnitTestUtilityFunctions.inject;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
@SuppressWarnings({"TypeMayBeWeakened", "rawtypes", "unchecked", "UseOfSystemOutOrSystemErr"})
public class PInterfaceTests {

    private ParseProxy mockProxy = mock(ParseProxy.class);
    private PInterface parseInterface = PInterface.INST;
    private ParseProxy normalProxy = null;

    @Before
    public void setMockedProxy() {
        normalProxy = parseInterface.getProxy();
        parseInterface = inject(parseInterface, mockProxy, "proxy");
        ParseObject.registerSubclass(Favorites.class);
        ParseObject.registerSubclass(Item.class);
        ParseObject.registerSubclass(Resources.class);
    }

    @After
    public void restoreNormalProxy() {
        parseInterface = inject(parseInterface, normalProxy, "proxy");
    }

    @Test
    public void normalBehavior() throws ParseException, InterruptedException {

        Context context = RuntimeEnvironment.application;

        Set<String> favorites = new HashSet<>();
        favorites.add("idHouse1");

        Favorites customFavorites = mock(Favorites.class);
        doReturn(favorites).when(customFavorites).getFavoritesFromLocal();

        final List<Favorites> favoritesList = new ArrayList<>();
        favoritesList.add(customFavorites);
        favoritesList.add(customFavorites);

        Item item = new Item();
        final List<Item> itemList = new ArrayList<>();
        itemList.add(item);

        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                ParseQuery pQuery = (ParseQuery) args[0];
                switch (pQuery.getClassName()) {
                    case "Favorites":
                        return favoritesList;
                    default:
                        throw new IllegalStateException("Nothing to do here");
                }
            }
        }).when(mockProxy).executeFindQuery(any(ParseQuery.class));

        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                ParseQuery pQuery = (ParseQuery) args[0];
                FindCallback<Item> callback = (FindCallback) args[1];

                switch (pQuery.getClassName()) {
                    case "Item":
                        callback.done(itemList, null);
                        return null;
                    default:
                        throw new IllegalStateException("Nothing to do here");
                }
            }
        }).when(mockProxy).executeQuery(any(ParseQuery.class), any(FindCallback.class));

        ItemAdapter itemAdapter = mock(ItemAdapter.class);
        List<Item> queriedList = new ArrayList<>();
        parseInterface.getItemList(queriedList, itemAdapter, null, false, "coolId", context);
        parseInterface.getItemList(queriedList, itemAdapter, null, true, "coolId2", context);

        Thread.sleep(500);
        assertFalse(queriedList.isEmpty());
        assertEquals(item, queriedList.get(0));

        assertEquals(PInterface.INST, PInterface.valueOf("INST"));
        assertEquals(PInterface.values().length, PInterface.values().length);
    }

    @Test
    public void favoriteToggledEmptyFavorites() throws ParseException, InterruptedException {

        Context context = RuntimeEnvironment.application;

        Favorites customFavorites = mock(Favorites.class);

        Set<String> favorites = new HashSet<>();

        doReturn(favorites).when(customFavorites).getFavoritesFromLocal();

        final List<Favorites> favoritesList = new ArrayList<>();
        favoritesList.add(customFavorites);

        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                ParseQuery pQuery = (ParseQuery) args[0];
                switch (pQuery.getClassName()) {
                    case "Favorites":
                        return favoritesList;
                    default:
                        throw new IllegalStateException("Nothing to do here");
                }
            }
        }).when(mockProxy).executeFindQuery(any(ParseQuery.class));

        ItemAdapter itemAdapter = mock(ItemAdapter.class);
        Item item = new Item();

        List<Item> queriedList = new ArrayList<>();
        queriedList.add(item);
        parseInterface.getItemList(queriedList, itemAdapter, null, true, "coolId2", context);

        assertTrue(queriedList.isEmpty());
    }

    @Test
    public void exceptionsThrown() throws ParseException, InterruptedException {

        Context context = RuntimeEnvironment.application;

        Set<String> favorites = new HashSet<>();
        favorites.add("idHouse1");

        Favorites customFavorites = mock(Favorites.class);
        doReturn(favorites).when(customFavorites).getFavoritesFromLocal();

        final List<Favorites> favoritesList = new ArrayList<>();
        favoritesList.add(customFavorites);

        Item item = new Item();
        final List<Item> itemList = new ArrayList<>();
        itemList.add(item);

        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) throws ParseException {
                Object[] args = invocation.getArguments();
                ParseQuery pQuery = (ParseQuery) args[0];
                switch (pQuery.getClassName()) {
                    case "Favorites":
                        throw new ParseException(new Throwable("intentional favorites query exception"));
                    default:
                        throw new IllegalStateException("Nothing to do here");
                }
            }
        }).when(mockProxy).executeFindQuery(any(ParseQuery.class));

        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                ParseQuery pQuery = (ParseQuery) args[0];
                FindCallback<Item> callback = (FindCallback) args[1];

                switch (pQuery.getClassName()) {
                    case "Item":
                        callback.done(Collections.<Item>emptyList(), new ParseException(new Throwable("intentional error")));
                        return null;
                    default:
                        throw new IllegalStateException("Nothing to do here");
                }
            }
        }).when(mockProxy).executeQuery(any(ParseQuery.class), any(FindCallback.class));


        ItemAdapter itemAdapter = mock(ItemAdapter.class);
        List<Item> queriedList = new ArrayList<>();
        queriedList.add(item);

        //Test that when there is an exception, queriedList is not cleared (this means that we ran into exception)
        parseInterface.getItemList(queriedList, itemAdapter, null, false, "coolId", context);
        Thread.sleep(500);
        assertFalse(queriedList.isEmpty());

        boolean exceptionThrown = false;
        Favorites f = new Favorites();
        //noinspection ProhibitedExceptionCaught
        try {
            f = parseInterface.getFavoriteFromId("coolId");
        } catch (Exception exception) {
            exceptionThrown = true;
            System.out.println("There is a \"normal\" null pointer exception: " + exception.getMessage());
        }
        assertNotNull(f);
        assertTrue(exceptionThrown);
    }

    @Test
    public void getResourcesObjectError() throws JSONException, ParseException {

        final Resources testResources = new Resources();

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

        ((JSONObject) neighborsJSONArray.get(0)).put(typeTag, -1);
        testResources.put(panoSphereDatasTag, panoSphereDatas);

        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                ParseQuery pQuery = (ParseQuery) args[0];
                switch (pQuery.getClassName()) {
                    case "Resources":
                        return Collections.singletonList(testResources);
                    default:
                        throw new IllegalStateException("Nothing to do here");
                }
            }
        }).when(mockProxy).executeFindQuery(any(ParseQuery.class));

        HouseManager houseManager = parseInterface.getHouseManager("someId", RuntimeEnvironment.application);
        assertEquals(-1, houseManager.getStartingId());
    }


}
