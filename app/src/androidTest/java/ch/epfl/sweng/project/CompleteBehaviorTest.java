package ch.epfl.sweng.project;


import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.project.user.LoginActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.sweng.project.util.TestUtilityFunctions.wait250ms;
import static org.hamcrest.Matchers.anything;

public class CompleteBehaviorTest {

    private static final String TAG = "LoginActivityTest";
    private static final String TEST_USER_MAIL = "test@astutus.org";
    private static final String TEST_USER_PASSWORD = "12345";

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);


    @Test
    public void testFullApp() {

        onView(withId(R.id.login_email)).perform(typeText(TEST_USER_MAIL), closeSoftKeyboard());
        onView(withId(R.id.login_password)).perform(typeText(TEST_USER_PASSWORD), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        wait250ms(TAG);

        onView(withId(R.id.activity_list)).check(matches(isDisplayed()));

        onData(anything()).inAdapterView(withId(R.id.houseList)).atPosition(2).perform(click());

        wait250ms(TAG);

        onView(childAtPosition(withId(R.id.imgs), 4)).perform(scrollTo()).perform(click());
        wait250ms(TAG);
        onView(withId(R.id.displayed_image)).check(matches(isDisplayed()));

        pressBack();

        wait250ms(TAG);

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
}
