package ch.epfl.sweng.project.data;

import android.content.Context;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.data.parse.PInterface;
import ch.epfl.sweng.project.data.parse.ParseProxy;
import ch.epfl.sweng.project.data.parse.objects.Favorites;
import ch.epfl.sweng.project.data.parse.objects.Item;
import ch.epfl.sweng.project.features.propertylist.adapter.ItemAdapter;

import static ch.epfl.sweng.project.util.UnitTestUtilityFunctions.inject;
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

    private ParseProxy mockProxy = mock(ParseProxy.class);
    private PInterface parseInterface = PInterface.INST;
    private ParseProxy normalProxy;

    @Before
    public void setMockedProxy() {
        normalProxy = parseInterface.getProxy();
        parseInterface = inject(parseInterface, mockProxy, "proxy");
    }

    @After
    public void restoreNormalProxy() {
        parseInterface = inject(parseInterface, normalProxy, "proxy");
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


        List<Item> queriedList = new ArrayList<>();
        parseInterface.getItemList(queriedList, itemAdapter, null, false, "coolId", context);
        parseInterface.getItemList(queriedList, itemAdapter, null, true, "coolId2", context);

        Thread.sleep(500);
        assertFalse(queriedList.isEmpty());
        assertEquals(item, queriedList.get(0));

    }


}
