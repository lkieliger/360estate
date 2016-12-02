package ch.epfl.sweng.project.data.parse.util;

import android.os.Handler;
import android.os.Looper;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Collections;
import java.util.List;

import ch.epfl.sweng.project.data.parse.ParseProxy;

/**
 * Class used to specify a timeout on ParseQueries
 * <p>
 * source : http://stackoverflow.com/a/27389904
 */
public class TimeoutQuery<T extends ParseObject> {

    private static final long SECOND_QUERY_TIMEOUT = 700L;

    private ParseQuery<T> mQuery;
    private final long mTimeout;
    private final boolean mSecondTry;
    private FindCallback<T> mCallback;
    private final Object mLock = new Object();
    private final Thread mThread;


    public TimeoutQuery(ParseQuery<T> query, long timeout) {
        this(query, timeout, false);
    }

    private TimeoutQuery(ParseQuery<T> query, long timeout, final boolean secondTry) {
        mQuery = query;
        mTimeout = timeout;
        mSecondTry = secondTry;

        mThread = new Thread() {
            @Override
            public void run() {
                if (isInterrupted()) return;
                try {
                    Thread.sleep(mTimeout);
                } catch (InterruptedException ignored) {
                    return;
                }

                ParseProxy.PROXY.notifyInternetProblem();

                synchronized (mLock) {
                    if (mSecondTry) {
                        cancelQuery();
                    } else {
                        secondQueryAttempt();
                    }

                }
            }
        };
    }

    @SuppressWarnings("MethodOnlyUsedFromInnerClass")
    private void cancelQuery() {
        synchronized (mLock) {
            if (null == mQuery)
                return; // it's already canceled

            mQuery.cancel();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mCallback.done(Collections.<T>emptyList(), new ParseException(ParseException.TIMEOUT, "The query " +
                            "took more than " + mTimeout + "ms to complete, it has therefore been canceled"));
                }
            });
        }
    }

    @SuppressWarnings("MethodOnlyUsedFromInnerClass")
    private void secondQueryAttempt() {
        synchronized (mLock) {
            if (null == mQuery)
                return; // nothing to do here

            mQuery.cancel();
            mQuery.fromLocalDatastore();

            // The second time we run the query, we run it from the localDataStore and it should be fast
            TimeoutQuery<T> secondQuery = new TimeoutQuery<>(mQuery, SECOND_QUERY_TIMEOUT, true);
            secondQuery.findInBackground(mCallback);

            // We can terminate the current Thread, as the query is now running in an other TimeoutQuery instance.
            mThread.interrupt();
        }
    }

    public void findInBackground(final FindCallback<T> callback) {
        synchronized (mLock) {
            mCallback = callback;
            mQuery.findInBackground(new FindCallback<T>() {
                @Override
                public void done(List<T> objects, ParseException e) {
                    synchronized (mLock) {
                        mThread.interrupt();
                        mQuery = null;
                        mCallback.done(objects, e);
                    }
                }
            });
            mThread.start();
        }
    }
}