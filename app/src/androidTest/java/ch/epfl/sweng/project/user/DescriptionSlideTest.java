package ch.epfl.sweng.project.user;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.project.itemDisplayer.DescriptionActivity;
import ch.epfl.sweng.project.R;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.sweng.project.util.TestUtilityFunctions.wait1s;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;


@RunWith(AndroidJUnit4.class)
public class DescriptionSlideTest {

    private static final String TAG = "DescriptionActivityTest: ";


    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @After
    public void finishActivity() {
        mActivityTestRule.getActivity().finish();
        wait1s(TAG);
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    @Test
    public void descriptionDisplayTest() {

        onView(withId(R.id.login_email)).perform(typeText("test@astutus.org"), closeSoftKeyboard());
        onView(withId(R.id.login_password)).perform(typeText("12345"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        wait1s(TAG);
        wait1s(TAG);

        onView(withId(R.id.activity_list)).check(matches(isDisplayed()));
        wait1s(TAG);

        onData(anything()).inAdapterView(withId(R.id.houseList)).atPosition(2).perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.description_text),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.activity_description),
                                        0),
                                3),
                        isDisplayed()));
        textView.check(matches(isDisplayed()));

        ViewInteraction button = onView(
                allOf(withId(R.id.action_launch_panorama),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.activity_description),
                                        0),
                                4),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        onView(
                allOf(
                        withId(R.id.scroll),
                            childAtPosition(
                                childAtPosition(
                                    withId(R.id.activity_description),0)
                            ,1))).perform(click());

        pressBack();
        pressBack();

        onView(withId(R.id.logOutButton)).perform(click());
    }

}
