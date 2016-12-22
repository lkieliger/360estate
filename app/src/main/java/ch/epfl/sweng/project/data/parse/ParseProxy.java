package ch.epfl.sweng.project.data.parse;


import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bolts.Task;
import ch.epfl.sweng.project.data.parse.util.TimeoutQuery;
import ch.epfl.sweng.project.util.LogHelper;


@SuppressWarnings("ClassNamePrefixedWithPackageName")
public class ParseProxy {

    ParseProxy() {
        // Package local constructor ! only accessbile from data package
    }

    private static final long INTERNET_TIMEOUT = 10000L;
    private static final long QUERY_TIMEOUT = 3014L;
    private static final long QUERY_2ND_TIMEOUT = 1514L;
    private static final String TAG = "ParseProxy";

    private long internetUnavailableTime = 0;

    /**
     * Method used to notify of an internet problem
     */
    public void notifyInternetProblem() {
        internetUnavailableTime = System.currentTimeMillis();
    }

    /**
     * @return true if internet has not been notified unavailable for INTERNET_TIMEOUT ms, false otherwise
     */
    public boolean internetAvailable() {
        return System.currentTimeMillis() - internetUnavailableTime > INTERNET_TIMEOUT;
    }

    /**
     * @param query    the parse query
     * @param callback a FindCallback function that is executed when the query is completed
     * @param <T>      the type of the ParseObject to fetch
     */
    public <T extends ParseObject> void executeQuery(ParseQuery<T> query, FindCallback<T> callback) {

        // If we haven't had internet since INTERNET_TIMEOUT ms, we make the query locally
        if (!internetAvailable()) {
            query.fromLocalDatastore();
        }

        TimeoutQuery<T> timeoutQuery = new TimeoutQuery<>(query, QUERY_TIMEOUT);
        timeoutQuery.findInBackground(callback);
    }

    /**
     *
     * @param query the query to specify which objects to get
     * @param <T>   the type of the objects to get
     * @return      A list of objects that were fetched
     * @throws ParseException
     */
    public <T extends ParseObject> List<T> executeFindQuery(ParseQuery<T> query) throws ParseException {
        return executeFindQuery(query, false);
    }

    private <T extends ParseObject> List<T> executeFindQuery(ParseQuery<T> query, boolean secondTime)
            throws ParseException {

        if (!internetAvailable()) {
            query.fromLocalDatastore();
        }

        Task<List<T>> task = query.findInBackground();

        try {
            task.waitForCompletion(secondTime ? QUERY_2ND_TIMEOUT : QUERY_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            LogHelper.log(TAG, "Error in findQuery waitForCompletion execution: " + e.getMessage());
            return Collections.emptyList();
        }

        if (task.isCompleted()) {
            return task.getResult();
        } else {
            if (task.isFaulted()) {
                Exception error = task.getError();
                if (error instanceof ParseException) {
                    throw (ParseException) error;
                } else {
                    throw new ParseException(error);
                }
            } else {
                if (!secondTime) {
                    LogHelper.log(TAG, "Error in findQuery .. we probably encountered a network timeout");
                    notifyInternetProblem();
                    query.cancel();

                    return executeFindQuery(query, true);
                } else {
                    throw new ParseException(1410, "The findQuery task encountered a non nominal behavior");
                }
            }
        }
    }

}
