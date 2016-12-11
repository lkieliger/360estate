package ch.epfl.sweng.project.data;

import android.app.Activity;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.data.parse.ParseProxy;
import ch.epfl.sweng.project.data.parse.objects.Item;
import ch.epfl.sweng.project.data.parse.util.TimeoutQuery;
import ch.epfl.sweng.project.features.propertylist.adapter.ItemAdapter;
import ch.epfl.sweng.project.userSupport.activities.LoginActivity;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)

public class ParseTests {

    private final ParseProxy proxy = ParseProxy.PROXY;

    /*
    Maybe rewrite this test using Mocking
    @Test
    public void testParseMgmtCornerCase() throws InterruptedException {

        while (!proxy.internetAvailable()) {
            Thread.sleep(500);
        }

        Activity dummyActivity = Robolectric.buildActivity(LoginActivity.class).create().get();

        ArrayList<Item> itemList = new ArrayList<>();
        PInterface.getItemList(itemList, new ItemAdapter(dummyActivity.getBaseContext(), itemList),
                null, false, "nullUid", null);
    }
    */


    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testParseProxyCancelQueryCalled() throws InterruptedException {

        ParseQuery<ParseObject> query = Mockito.mock(ParseQuery.class);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws InterruptedException {
                Thread.sleep(1000);
                return null;
            }
        }).doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws InterruptedException {
                Thread.sleep(1000);
                return null;
            }
        }).when(query).findInBackground(any(FindCallback.class));

        TimeoutQuery<ParseObject> toQuery = new TimeoutQuery(query, 10);

        toQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                throw new IllegalStateException("Shouldn't have been called");
            }
        });

        Thread.sleep(3000);
        // we cannot check that cancelQuery was called, but only that the first query failed.
        assertTrue(toQuery.firstQueryFailed());
    }

}
