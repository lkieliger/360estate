package ch.epfl.sweng.project.data;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.data.parse.PInterface;
import ch.epfl.sweng.project.data.parse.ParseProxy;
import ch.epfl.sweng.project.data.parse.objects.Favorites;
import ch.epfl.sweng.project.data.parse.objects.Item;
import ch.epfl.sweng.project.features.SplashActivity;
import ch.epfl.sweng.project.features.propertylist.adapter.ItemAdapter;
import ch.epfl.sweng.project.tests3d.TestUtils;
import ch.epfl.sweng.project.userSupport.activities.RegisterActivity;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
@SuppressWarnings({"TypeMayBeWeakened", "rawtypes", "unchecked"})
public class PInterfaceTests {

    private ParseProxy proxy = mock(ParseProxy.class);
    private PInterface parseInterface = PInterface.INST;

    @Before
    public void setMockedProxy() {
        parseInterface = TestUtils.inject(parseInterface, proxy, "proxy");
    }

    @Test
    public void normalBehavior() throws ParseException, InterruptedException {

        Context context = RuntimeEnvironment.application;

        ParseObject.registerSubclass(Favorites.class);
        ParseObject.registerSubclass(Item.class);
        Favorites customFavorites = mock(Favorites.class);

        Set<String> favorites = new HashSet<>();
        favorites.add("idHouse1");

        doReturn(favorites).when(customFavorites).getFavoritesFromLocal();

        final List<Favorites> favoritesList = new ArrayList<>();
        favoritesList.add(customFavorites);

        final List<Item> itemList = new ArrayList<>(4);
        Item item = new Item();
        itemList.add(item);

        ItemAdapter itemAdapter = mock(ItemAdapter.class);

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
        }).when(proxy).executeFindQuery(any(ParseQuery.class));

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
        }).when(proxy).executeQuery(any(ParseQuery.class), any(FindCallback.class));


        List<Item> queriedList = new ArrayList<>();
        parseInterface.getItemList(queriedList, itemAdapter, null, false, "coolId", context);
        parseInterface.getItemList(queriedList, itemAdapter, null, true, "coolId2", context);

        Thread.sleep(500);
        assertFalse(queriedList.isEmpty());
        assertEquals(item, queriedList.get(0));

    }


}
