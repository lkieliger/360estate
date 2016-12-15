package ch.epfl.sweng.project.data;

import android.app.Activity;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;
import java.util.concurrent.TimeUnit;

import bolts.Task;
import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.data.parse.PInterface;
import ch.epfl.sweng.project.data.parse.util.TimeoutQuery;
import edu.emory.mathcs.backport.java.util.Collections;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)

public class ParseTests {

    private final PInterface parseInterface = PInterface.INST;

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

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test (expected = ParseException.class)
    public void testFindQuery2ndFailure() throws InterruptedException, ParseException {

        ParseQuery<ParseObject> query = Mockito.mock(ParseQuery.class);

        Task<List<ParseObject>> mockTask = Mockito.mock(Task.class);

        doReturn(false).when(mockTask).isCompleted();
        doReturn(false).when(mockTask).isFaulted();

        doReturn(mockTask).when(query).findInBackground();

        parseInterface.getProxy().executeFindQuery(query);
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void findQueryThrowsParseException() {

        ParseQuery<ParseObject> query = Mockito.mock(ParseQuery.class);
        Task<List<ParseObject>> mockTask = Mockito.mock(Task.class);
        doReturn(mockTask).when(query).findInBackground();

        doReturn(false).when(mockTask).isCompleted();
        doReturn(true).when(mockTask).isFaulted();

        Throwable exception = new Throwable("Parse Exception from mock task!");

        //noinspection ThrowableResultOfMethodCallIgnored
        doReturn(new ParseException(exception)).when(mockTask).getError();

        try {
            parseInterface.getProxy().executeFindQuery(query);
        } catch (ParseException e) {
            assertEquals(exception.toString(), e.getMessage());
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void findQueryThrowsAnyException() {

        ParseQuery<ParseObject> query = Mockito.mock(ParseQuery.class);
        Task<List<ParseObject>> mockTask = Mockito.mock(Task.class);
        doReturn(mockTask).when(query).findInBackground();

        doReturn(false).when(mockTask).isCompleted();
        doReturn(true).when(mockTask).isFaulted();

        Exception exception = new IllegalStateException("Parse Exception from mock task!");

        //noinspection ThrowableResultOfMethodCallIgnored
        doReturn(exception).when(mockTask).getError();

        try {
            parseInterface.getProxy().executeFindQuery(query);
        } catch (ParseException e) {
            assertEquals(exception.toString(), e.getMessage());
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void findQueryReturnsEmptyOnError() throws InterruptedException, ParseException {

        ParseQuery<ParseObject> query = Mockito.mock(ParseQuery.class);

        Task<List<ParseObject>> mockTask = Mockito.mock(Task.class);
        doThrow(new InterruptedException("Interrupted Exception from Mock task!"))
                .when(mockTask).waitForCompletion(anyLong(), any(TimeUnit.class));

        doReturn(mockTask).when(query).findInBackground();

        assertEquals(Collections.emptyList(), parseInterface.getProxy().executeFindQuery(query));
    }

}
