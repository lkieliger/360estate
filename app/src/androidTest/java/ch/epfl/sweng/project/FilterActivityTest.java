package ch.epfl.sweng.project;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SeekBar;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class FilterActivityTest {

    private final String TAG = "FilterActivityTest: ";

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public ActivityTestRule<ListActivity> listActivityActivityTestRule =
            new ActivityTestRule<>(ListActivity.class);


    @Test
    public void numberOfRoomsTest() {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.d(TAG , "InterruptedException" +e.getMessage());
        }

        onView(withId(R.id.filterButtonPopUp)).perform(click());
        onView(withId(R.id.numberOfRooms)).perform(typeText("3"), closeSoftKeyboard());
        onView(withId(R.id.filterButton)).perform(click());


        final int[] counts = {0};
        onView(withId(R.id.houseList)).check(matches(new ViewTypeSafeMatcher(counts)));

        for (int i = 0; i < counts[0]; i++) {
            onData(anything()).inAdapterView(withId(R.id.houseList)).atPosition(i).
                    check(matches(hasDescendant(withText(containsString("3 rooms")))));
        }
    }

    @Test
    public void cityTest() {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.d(TAG , "InterruptedException" +e.getMessage());
        }

        onView(withId(R.id.filterButtonPopUp)).perform(click());
        onView(withId(R.id.location)).perform(typeText("Renens"), closeSoftKeyboard());
        onView(withId(R.id.filterButton)).perform(click());

        final int[] counts = {0};
        onView(withId(R.id.houseList)).check(matches(new ViewTypeSafeMatcher(counts)));

        for (int i = 0; i < counts[0]; i++) {
            onData(anything()).inAdapterView(withId(R.id.houseList)).atPosition(i).
                    check(matches(hasDescendant(withText(containsString("Renens")))));
        }
    }

    @Test
    public void typeTest() {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.d(TAG , "InterruptedException" +e.getMessage());
        }

        onView(withId(R.id.filterButtonPopUp)).perform(click());
        onView(withId(R.id.spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Building"))).perform(click());
        onView(withId(R.id.filterButton)).perform(click());

        final int[] counts = {0};
        onView(withId(R.id.houseList)).check(matches(new ViewTypeSafeMatcher(counts)));

        for (int i = 0; i < counts[0]; i++) {
            onData(anything()).inAdapterView(withId(R.id.houseList)).atPosition(i).
                    check(matches(hasDescendant(withText(containsString("Building")))));
        }
    }


    @Test
    public void priceTest() {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.d(TAG , "InterruptedException" +e.getMessage());

        }

        onView(withId(R.id.filterButtonPopUp)).perform(click());
        onView(withId(R.id.seekBarPrice)).perform(click());
        onView(withId(R.id.seekBarPrice)).perform(setProgress(7));
        onView(withId(R.id.filterButton)).perform(click());

        final int[] counts = {0};
        onView(withId(R.id.houseList)).check(matches(new ViewTypeSafeMatcher(counts)));

        for (int i = 0; i < counts[0]; i++) {
            onData(anything()).inAdapterView(withId(R.id.houseList)).atPosition(i).
                    check(matches(hasDescendant(withText(containsString("150'000 CHF")))));
        }
    }

    @Test
    public void surfaceTest() {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.d(TAG , "InterruptedException" +e.getMessage());
        }

        onView(withId(R.id.filterButtonPopUp)).perform(click());
        onView(withId(R.id.seekBarSurface)).perform(click());
        onView(withId(R.id.seekBarSurface)).perform((setProgress(100)));

        onView(withId(R.id.filterButton)).perform(click());

        final int[] counts = {0};
        onView(withId(R.id.houseList)).check(matches(new ViewTypeSafeMatcher(counts)));

        for (int i = 0; i < counts[0]; i++) {
            onData(anything()).inAdapterView(withId(R.id.houseList)).atPosition(i).
                    check(matches(hasDescendant(withText(containsString("2'000'000 m")))));
        }
    }



    private static class ViewTypeSafeMatcher extends TypeSafeMatcher<View> {
        private final int[] counts;

        ViewTypeSafeMatcher(int[] counts) {
            this.counts = counts;
        }

        @Override
        public void describeTo(Description description) {
        }

        @Override
        public boolean matchesSafely(View view) {
            ListView listView = (ListView) view;
            counts[0] = listView.getCount();
            return true;
        }
    }

    public static ViewAction setProgress(final int progress) {
        return new MyViewAction(progress);
    }

    private static class MyViewAction implements ViewAction {
        private final int progress;

        MyViewAction(int progress) {
            this.progress = progress;
        }

        @Override
        public void perform(UiController uiController, View view) {
            ((SeekBar) view).setProgress(progress);
        }

        @Override
        public String getDescription() {
            return "Set a progress";
        }

        @Override
        public Matcher<View> getConstraints() {
            return ViewMatchers.isAssignableFrom(SeekBar.class);
        }
    }
}
