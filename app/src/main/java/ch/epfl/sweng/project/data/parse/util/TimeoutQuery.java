package ch.epfl.sweng.project.data.parse.util;

import android.os.Handler;
import android.os.Looper;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Collections;
import java.util.List;

/**
 * This has been copied from here : http://stackoverflow.com/a/27389904
 */

/*
Class used to specify a timeout on ParseQueries
 */
public class TimeoutQuery<T extends ParseObject> {
    private ParseQuery<T> mQuery;
    private final long mTimeout;
    private FindCallback<T> mCallback;
    private final Object mLock = new Object();
    private final Thread mThread;


    public TimeoutQuery(ParseQuery<T> query, long timeout) {
        mQuery = query;
        mTimeout = timeout;
        mThread = new Thread() {
            @Override
            public void run() {
                if (isInterrupted()) return;
                try {
                    Thread.sleep(mTimeout);
                } catch (InterruptedException ignored) {
                    return;
                }
                cancelQuery();
            }
        };
    }

    @SuppressWarnings("MethodOnlyUsedFromInnerClass")
    private void cancelQuery() {
        synchronized (mLock) {
            if (null == mQuery) return; // it's already canceled
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