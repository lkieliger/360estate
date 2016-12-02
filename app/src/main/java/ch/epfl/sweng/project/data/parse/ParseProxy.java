package ch.epfl.sweng.project.data.parse;


import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import ch.epfl.sweng.project.data.parse.util.TimeoutQuery;


@SuppressWarnings("ClassNamePrefixedWithPackageName")
public enum ParseProxy {

    PROXY;

    private static final long INTERNET_TIMEOUT = 10000L;
    private static final long QUERY_TIMEOUT = 3014L;

    private long internetUnavailableTime = 0;


    public void notifyInternetProblem() {
        internetUnavailableTime = System.currentTimeMillis();
    }

    public boolean internetAvailable() {
        return System.currentTimeMillis() - internetUnavailableTime > INTERNET_TIMEOUT;
    }


    public <T extends ParseObject> void executeQuery(ParseQuery<T> query, FindCallback<T> callback, String tag) {

        // If we haven't had internet since INTERNET_TIMEOUT ms, we make the query locally
        if (!internetAvailable()) {
            query.fromLocalDatastore();
        }

        TimeoutQuery<T> timeoutQuery = new TimeoutQuery<>(query, QUERY_TIMEOUT);
        timeoutQuery.findInBackground(callback);
    }

}
