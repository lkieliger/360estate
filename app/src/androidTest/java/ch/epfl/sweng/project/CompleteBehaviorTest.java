package ch.epfl.sweng.project;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import java.security.SecureRandom;

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
import static ch.epfl.sweng.project.util.TestUtilityFunctions.logUserOut;
import static ch.epfl.sweng.project.util.TestUtilityFunctions.wait1s;
import static ch.epfl.sweng.project.util.TestUtilityFunctions.wait250ms;
import static ch.epfl.sweng.project.util.TestUtilityFunctions.wait500ms;
import static ch.epfl.sweng.project.util.TestUtilityFunctions.waitNms;
import static org.hamcrest.Matchers.anything;

public class CompleteBehaviorTest {

    private static final String TAG = "LoginActivityTest";
    /*
 Random string, for the registration.
 Source:
 http://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string
  */
    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static SecureRandom rnd = new SecureRandom();
    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

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

    private static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    @After
    public void finishActivity() {
        mActivityTestRule.getActivity().finish();
        wait1s(TAG);
        logUserOut();
    }

    @Test
    public void testFullApp() {

        logUserOut();

        wait1s(TAG);

        String testUserMail = "test@" + randomString(6) + ".org";
        String testUserPassword = "12345";
        String testUserPhone = "+078888888";

        onView(withId(R.id.goto_registration_button)).perform(closeSoftKeyboard()).perform(click());
        wait500ms(TAG);

        onView(withId(R.id.registration_email)).perform(typeText(testUserMail), closeSoftKeyboard());
        onView(withId(R.id.registration_password)).perform(typeText(testUserPassword), closeSoftKeyboard());
        onView(withId(R.id.registration_password_bis)).perform(typeText(testUserPassword), closeSoftKeyboard());
        onView(withId(R.id.registration_phone)).perform(typeText(testUserPhone), closeSoftKeyboard());
        onView(withId(R.id.register_button)).perform(click());

        wait1s(TAG);
        wait1s(TAG);

        onView(withId(R.id.login_email)).perform(typeText(testUserMail), closeSoftKeyboard());
        onView(withId(R.id.login_password)).perform(typeText(testUserPassword), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        wait1s(TAG);

        onView(withId(R.id.activity_list)).check(matches(isDisplayed()));
        wait1s(TAG);
        onData(anything()).inAdapterView(withId(R.id.houseList)).atPosition(0).perform(click());
        onView(withId(R.id.addToFavorites)).perform(click());
        pressBack();
        onView(withId(R.id.FavoriteButton)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.houseList)).atPosition(0).perform(click());
        wait500ms(TAG);
        onView(withId(R.id.addToFavorites)).perform(click());
        pressBack();
        onView(withId(R.id.FavoriteButton)).perform(click());

        onData(anything()).inAdapterView(withId(R.id.houseList)).atPosition(2).perform(click());
        onView(withId(R.id.activity_description)).check(matches(isDisplayed()));

        // wait 3s for the images to load
        waitNms(TAG, 3000);

        ViewInteraction img0 = onView(childAtPosition(withId(R.id.scroll), 0));
        wait500ms(TAG);

        img0.perform(scrollTo());
        wait250ms(TAG);
        img0.perform(click());
        wait250ms(TAG);

        pressBack();

        wait250ms(TAG);

        onView(withId(R.id.action_launch_panorama)).perform(click());

        logUserOut();

    }

}
