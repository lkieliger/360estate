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
import android.view.View;

import com.parse.Parse;
import com.parse.ParseUser;

import org.hamcrest.Matcher;
import org.hamcrest.core.AllOf;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.AllOf.allOf;

public final class TestUtilityFunctions {

    private TestUtilityFunctions() {
    }

    public static void logUserOut() {
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser != null) {
            ParseUser.logOut();
        }
    }

    public static void wait1s(String debugTag) {
        waitNms(debugTag, TimeUnit.SECONDS.toMillis(1));
    }

    public static void waitNms(String debugTag, long millis) {
        ViewAction waitAction = new WaitNMsAction(millis, debugTag);
        onView(isRoot()).perform(waitAction);
    }

    public static void waitForIdNms(final int viewId, final long millis) {
        onView(isRoot()).perform(new WaitActionForId(viewId, millis));
    }

    public static void waitForIdNms(final int viewId, final long millis, final Matcher<View> matcher) {
        onView(isRoot()).perform(new WaitActionForId(viewId, millis, matcher));
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

    private static class CustomFailureHandler implements FailureHandler {

        private int[] mNumberOfFailedAttempts;

        CustomFailureHandler(int[] nbOfFailedAttempts) {
            mNumberOfFailedAttempts = nbOfFailedAttempts;
        }

        @SuppressLint("LogConditional")
        @Override
        public void handle(Throwable error, Matcher<View> viewMatcher) {
            mNumberOfFailedAttempts[0]++;
            LogHelper.log("RecursiveFailureHandler", "The viewMatcher" + viewMatcher.toString() +
                    " failed. number of failed " + "attempts : " + mNumberOfFailedAttempts[0]);
        }
    }


    /**
     * Perform action of waiting for a specific view id.
     * <p>
     * source : http://stackoverflow.com/a/22563297
     */
    private static class WaitNMsAction implements ViewAction {
        private final long mMillis;
        private final String mDebugTag;

        WaitNMsAction(long millis) {
            this(millis, null);
        }

        WaitNMsAction(long millis, String debugTag) {
            mMillis = millis;
            mDebugTag = debugTag;
        }

        @Override
        public Matcher<View> getConstraints() {
            return isRoot();
        }

        @Override
        public String getDescription() {
            return "wait unconditionally during " + mMillis + " millis.";
        }

        @Override
        public void perform(final UiController uiController, final View view) {
            uiController.loopMainThreadUntilIdle();
            final long startTime = System.currentTimeMillis();
            final long endTime = startTime + mMillis;

            do {
                // loops main thread 140ms each time.
                uiController.loopMainThreadForAtLeast(140);
            }
            while (System.currentTimeMillis() < endTime);

            LogHelper.log(mDebugTag != null ? mDebugTag : "WaitAction", "Just waited for " + mMillis + " ms");
        }
    }

}
