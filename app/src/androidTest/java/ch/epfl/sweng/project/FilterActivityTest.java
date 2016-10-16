package ch.epfl.sweng.project;

import android.support.test.espresso.PerformException;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.espresso.util.HumanReadables;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SeekBar;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.actionWithAssertions;
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

    private void waitAction() {
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "InterruptedException" + e.getMessage());
            }
        }
    }


    private static final String TAG = "FilterActivityTest: ";

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public ActivityTestRule<ListActivity> listActivityActivityTestRule =
            new ActivityTestRule<>(ListActivity.class);


    @Test
    public void filterTest() {
        onView(withId(R.id.filterButtonPopUp)).perform(click());
        onView(withId(R.id.numberOfRooms)).perform(typeText("3"), closeSoftKeyboard());
        onView(withId(R.id.location)).perform(typeText("Renens"), closeSoftKeyboard());
        onView(withId(R.id.spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(getString(R.string.building)))).perform(click());
        onView(withId(R.id.seekBarPrice)).perform(scrubSeekBarAction(5));
        onView(withId(R.id.seekBarSurface)).perform(scrubSeekBarAction(50));

        onView(withId(R.id.filterButton)).perform(click());
        waitAction();

        final int[] counts = {0};
        onView(withId(R.id.houseList)).check(matches(new ViewTypeSafeMatcher(counts)));

        for (int i = 0; i < counts[0]; i++) {
            onData(anything()).inAdapterView(withId(R.id.houseList)).atPosition(i).
                    check(matches(hasDescendant(withText(containsString(
                            String.format(
                                    getString(R.string.text_location_surface),
                                    "Renens",
                                    "10'000",
                                    "3",
                                    getString(R.string.rooms)
                            ))))));
            onData(anything()).inAdapterView(withId(R.id.houseList)).atPosition(i).
                    check(matches(hasDescendant(withText(containsString(
                            String.format(
                                    getString(R.string.text_price_type),
                                    "100",
                                    getString(R.string.text_currency),
                                    getString(R.string.building)
                            ))))));

        }
    }


    /*
    @Test
    public void numberOfRoomsTest() {

        onView(withId(R.id.filterButtonPopUp)).perform(click());
        onView(withId(R.id.numberOfRooms)).perform(typeText("3"), closeSoftKeyboard());
        onView(withId(R.id.filterButton)).perform(click());

        waitAction();
        final int[] counts = {0};
        onView(withId(R.id.houseList)).check(matches(new ViewTypeSafeMatcher(counts)));

        for (int i = 0; i < counts[0]; i++) {
            onData(anything()).inAdapterView(withId(R.id.houseList)).atPosition(i).
                    check(matches(hasDescendant(withText(containsString("3 " + getString(R.string.rooms))))));
        }
    }

    @Test
    public void cityTest() {

        onView(withId(R.id.filterButtonPopUp)).perform(click());
        onView(withId(R.id.location)).perform(typeText("Renens"), closeSoftKeyboard());
        onView(withId(R.id.filterButton)).perform(click());
        waitAction();
        final int[] counts = {0};
        onView(withId(R.id.houseList)).check(matches(new ViewTypeSafeMatcher(counts)));

        for (int i = 0; i < counts[0]; i++) {
            onData(anything()).inAdapterView(withId(R.id.houseList)).atPosition(i).
                    check(matches(hasDescendant(withText(containsString("Renens")))));
        }
    }

    @Test
    public void typeTest() {


        onView(withId(R.id.filterButtonPopUp)).perform(click());
        onView(withId(R.id.spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(getString(R.string.building)))).perform(click());
        onView(withId(R.id.filterButton)).perform(click());
        waitAction();
        final int[] counts = {0};
        onView(withId(R.id.houseList)).check(matches(new ViewTypeSafeMatcher(counts)));

        for (int i = 0; i < counts[0]; i++) {
            onData(anything()).inAdapterView(withId(R.id.houseList)).atPosition(i).
                    check(matches(hasDescendant(withText(containsString(getString(R.string.building))))));
        }
    }

    @Test
    public void priceTest() {


        onView(withId(R.id.filterButtonPopUp)).perform(click());
        onView(withId(R.id.seekBarPrice)).perform(scrubSeekBarAction(5));
        onView(withId(R.id.filterButton)).perform(click());
        waitAction();

        final int[] counts = {0};
        onView(withId(R.id.houseList)).check(matches(new ViewTypeSafeMatcher(counts)));

        for (int i = 0; i < counts[0]; i++) {
            onData(anything()).inAdapterView(withId(R.id.houseList)).atPosition(i).
                    check(matches(hasDescendant(withText(containsString("150'000 CHF")))));
        }
    }

    @Test
    public void surfaceTest() {

        onView(withId(R.id.filterButtonPopUp)).perform(click());
        onView(withId(R.id.seekBarSurface)).perform(scrubSeekBarAction(50));
        onView(withId(R.id.filterButton)).perform(click());
        waitAction();
        final int[] counts = {0};
        onView(withId(R.id.houseList)).check(matches(new ViewTypeSafeMatcher(counts)));

        for (int i = 0; i < counts[0]; i++) {
            onData(anything()).inAdapterView(withId(R.id.houseList)).atPosition(i).
                    check(matches(hasDescendant(withText(containsString("2'000'000 m")))));
        }
    }
*/


    private static class ViewTypeSafeMatcher extends TypeSafeMatcher<View> {
        private final int[] counts;

        ViewTypeSafeMatcher(int[] extCounts) {
            counts = extCounts;
        }

        @Override
        public void describeTo(Description description) {
        }

        @Override
        public boolean matchesSafely(View item) {
            ListView listView = (ListView) item;
            counts[0] = listView.getCount();
            return true;
        }
    }

    public static ViewAction scrubSeekBarAction(int progress) {
        return actionWithAssertions(new GeneralSwipeAction(
                Swipe.SLOW,
                new SeekBarThumbCoordinatesProvider(0),
                new SeekBarThumbCoordinatesProvider(progress),
                Press.PINPOINT));
    }

    private static class SeekBarThumbCoordinatesProvider implements CoordinatesProvider {
        int mProgress;

        SeekBarThumbCoordinatesProvider(int progress) {
            mProgress = progress;
        }

        private static float[] getVisibleLeftTop(View view) {
            final int[] xy = new int[2];
            view.getLocationOnScreen(xy);
            return new float[]{ xy[0], xy[1] };
        }

        @Override
        public float[] calculateCoordinates(View view) {
            if (!(view instanceof SeekBar)) {
                throw new PerformException.Builder()
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(new RuntimeException("SeekBar expected")).build();
            }
            SeekBar seekBar = (SeekBar) view;
            int width = seekBar.getWidth() - seekBar.getPaddingLeft() - seekBar.getPaddingRight();
            double progress = mProgress == 0 ? seekBar.getProgress() : mProgress;
            int xPosition = (int) (seekBar.getPaddingLeft() + width * progress / seekBar.getMax());
            float[] xy = getVisibleLeftTop(seekBar);
            return new float[]{ xy[0] + xPosition, xy[1] + 10 };
        }
    }

    private String getString(int id){
        return listActivityActivityTestRule.getActivity().getString(id);
    }
}