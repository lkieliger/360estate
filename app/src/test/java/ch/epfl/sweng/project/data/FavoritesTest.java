package ch.epfl.sweng.project.data;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.data.parse.PInterface;
import ch.epfl.sweng.project.data.parse.ParseProxy;
import ch.epfl.sweng.project.data.parse.objects.Favorites;

import static ch.epfl.sweng.project.util.UnitTestUtilityFunctions.inject;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)

public class FavoritesTest {

    private static final String TAG = "Favorites tests:";

    private ParseProxy mockProxy = mock(ParseProxy.class);
    private PInterface parseInterface = PInterface.INST;
    private ParseProxy normalProxy = null;

    @Before
    public void saveNormalProxy() {
        normalProxy = parseInterface.getProxy();
        ParseObject.registerSubclass(Favorites.class);
    }

    @After
    public void restoreNormalProxy() {
        parseInterface = inject(parseInterface, normalProxy, "proxy");
    }

    @Test
    public void getFavoritesNullArray() {
        Favorites favorites = new Favorites();
        Favorites spyedFavorites = spy(favorites);

        doReturn(null).when(spyedFavorites).getJSONArray("favorites");

        Set<String> favoritesFromServer = spyedFavorites.getFavoritesFromServer();
        assertTrue(favoritesFromServer.isEmpty());
    }

    @Test
    public void getFavoritesInvalidArray() throws JSONException {
        Favorites favorites = new Favorites();
        Favorites spyedFavorites = spy(favorites);

        JSONArray invalidArray = mock(JSONArray.class);
        doThrow(new JSONException("intentional exception")).when(invalidArray).getString(anyInt());
        doReturn(14).when(invalidArray).length();

        doReturn(invalidArray).when(spyedFavorites).getJSONArray("favorites");
        Set<String> favoritesFromServer = spyedFavorites.getFavoritesFromServer();
        assertTrue(favoritesFromServer.isEmpty());
    }

    @Test
    public void synchronizeMethodsOfflineMode() throws JSONException, ParseException {

        parseInterface = inject(parseInterface, mockProxy, "proxy");

        mockProxy.notifyInternetProblem();
        Favorites favorites = new Favorites();
        favorites.synchronizeServer();
        favorites.synchronizeFromServer();

        restoreNormalProxy();
    }

    @Test
    public void ovverrideFavoritesError() throws JSONException, ParseException {
        Favorites favorites = mock(Favorites.class);
        //noinspection unchecked
        doThrow(new ParseException(new Throwable(("intentional exception"))))
                .when(favorites).setFavorites(null);
        List<Favorites> favList = new ArrayList<>();
        favList.add(favorites);

        ParseProxy mockedProxy = mock(ParseProxy.class);
        parseInterface = inject(parseInterface, mockedProxy, "proxy");

        doReturn(false).when(mockedProxy).internetAvailable();
        doReturn(favList).when(mockedProxy).executeFindQuery(any(ParseQuery.class));

        parseInterface.overrideFavorites("id", null);
        restoreNormalProxy();
    }


}
