package ch.epfl.sweng.project.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.test.espresso.FailureHandler;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.util.HumanReadables;
import android.support.test.espresso.util.TreeIterables;
import android.util.Log;
import android.view.View;

import com.parse.Parse;
import com.parse.ParseUser;

import org.hamcrest.Matcher;
import org.hamcrest.core.AllOf;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ch.epfl.sweng.project.BuildConfig;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.AllOf.allOf;

public final class TestUtilityFunctions {

    private static final String APP_ID = "360ESTATE";

    private TestUtilityFunctions() {
    }

    public static void initializeParse(Context context) {
        Parse.initialize(new Parse.Configuration.Builder(context)
                // The network interceptor is used to debug the communication between server/client
                //.addNetworkInterceptor(new ParseLogInterceptor())
                .applicationId(APP_ID)
                .server("https://360.astutus.org/parse/")
                .build()
        );
    }

    public static void logUserOut() {
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser != null) {
            ParseUser.logOut();
        }
    }

    public static void wait1s(String debugTag) {
        waitNms(debugTag, 1000);
    }

    public static void waitNms(String debugTag, long millis) {
        try {
            Thread.sleep(millis);
            if (BuildConfig.DEBUG) {
                Log.d(debugTag, "Thread slept for " + millis + " ms");
            }
        } catch (InterruptedException e) {
            if (BuildConfig.DEBUG) {
                Log.d(debugTag, "InterruptedException" + e.getMessage());
            }
        }
    }

    public static void waitForIdNms(final int viewId, final long millis) {
        onView(isRoot()).perform(new WaitActionForId(viewId, millis));
    }

    public static void waitForIdNms(final int viewId, final long millis, final Matcher<View> matcher) {
        onView(isRoot()).perform(new WaitActionForId(viewId, millis, matcher));
    }

    /**
     * Perform action of waiting for a specific view id.
     * <p>
     * source : http://stackoverflow.com/a/22563297
     */
    private static class WaitActionForId implements ViewAction {
        private final int viewId;
        private final long millis;
        private final Matcher<View> mMatcher;

        WaitActionForId(int viewId, long millis) {
            this(viewId, millis, null);
        }

        WaitActionForId(int viewId, long millis, final Matcher<View> customMatcher) {
            this.viewId = viewId;
            this.millis = millis;
            mMatcher = customMatcher;
        }

        @Override
        public Matcher<View> getConstraints() {
            return isRoot();
        }

        @Override
        public String getDescription() {
            return "wait for a specific view with id <" + viewId + "> during " + millis + " millis.";
        }

        @Override
        public void perform(final UiController uiController, final View view) {
            uiController.loopMainThreadUntilIdle();
            final long startTime = System.currentTimeMillis();
            final long endTime = startTime + millis;
            final Matcher<View> viewMatcher = (mMatcher != null) ?
                    AllOf.allOf(withId(viewId), isDisplayingAtLeast(60), mMatcher) :
                    AllOf.allOf(withId(viewId), isDisplayingAtLeast(60));

            do {
                for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
                    // found view with required ID
                    if (viewMatcher.matches(child)) {
                        return;
                    }
                }

                uiController.loopMainThreadForAtLeast(50);
            }
            while (System.currentTimeMillis() < endTime);

            // timeout happens
            throw new PerformException.Builder()
                    .withActionDescription(this.getDescription())
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(new TimeoutException("The view hasn't been found after " + millis + " ms."))
                    .build();
        }
    }

    public static void viewIdDisplayedAfterNattempts(final int viewId, final int maxNumberOfAttempts) {
        viewIdDisplayedAfterNattempts(viewId, maxNumberOfAttempts, null);
    }

    public static void viewIdDisplayedAfterNattempts(final int viewId, final int maxNumberOfAttempts,
                                                     Matcher<View> matcher) {
        int numberOfAttempts = 0;
        final int[] numberFailedAttempts = {0};
        ViewInteraction viewInteraction = onView(withId(viewId)).
                withFailureHandler(new CustomFailureHandler(numberFailedAttempts));

        do {
            if (matcher != null) {
                viewInteraction.check(matches(allOf(isDisplayed(), matcher)));
            } else {
                viewInteraction.check(matches(isDisplayed()));
            }
            numberOfAttempts++;
            if (numberFailedAttempts[0] < numberOfAttempts)
                return;

            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(2));
            } catch (InterruptedException ignored) {
            }
        } while (numberFailedAttempts[0] < maxNumberOfAttempts - 1);

        if (numberFailedAttempts[0] == maxNumberOfAttempts - 1) {
            // The view was not found, so we run onView one last time without a FailureHandler, so that an exception
            // is thrown if it is still not found, otherwise it will continue
            if (matcher != null) {
                onView(withId(viewId)).check(matches(allOf(isDisplayed(), matcher)));
            } else {
                onView(withId(viewId)).check(matches(isDisplayed()));
            }
        }
    }

    private static class CustomFailureHandler implements FailureHandler {

        private int[] mNumberOfFailedAttempts;

        CustomFailureHandler(int[] nbOfFailedAttempts) {
            mNumberOfFailedAttempts = nbOfFailedAttempts;
        }

        @SuppressLint("LogConditional")
        @Override
        public void handle(Throwable error, Matcher<View> viewMatcher) {
            mNumberOfFailedAttempts[0]++;
            Log.d("RecursiveFailureHandler", "The viewMatcher" + viewMatcher.toString() + " failed. number of failed " +
                    "attempts : " + mNumberOfFailedAttempts[0]);
        }
    }

}
