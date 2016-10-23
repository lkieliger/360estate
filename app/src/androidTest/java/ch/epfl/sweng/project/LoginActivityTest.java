package ch.epfl.sweng.project;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.project.user.LoginActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.sweng.project.util.TestUtilityFunctions.wait1s;
import static ch.epfl.sweng.project.util.TestUtilityFunctions.wait250ms;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    private static final String TAG = "LoginActivityTest";
    private static final String TEST_USER_MAIL = "test@astutus.org";
    private static final String TEST_USER_PASSWORD = "12345";

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @After
    public void finishActivity() {
        mActivityTestRule.getActivity().finish();
        wait1s(TAG);
    }

    @Test
    public void testUserLogin() {

        onView(withId(R.id.login_email)).perform(typeText("HolaSenior@Shanchez.co"), closeSoftKeyboard());
        onView(withId(R.id.login_password)).perform(typeText("PortesTriEstate"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        wait250ms(TAG);

        onView(withText(R.string.error_login_unsuccessful)).inRoot(withDecorView(not(is(mActivityTestRule.getActivity()
                .getWindow().getDecorView())))).check(matches(isDisplayed()));

        wait1s(TAG);

        onView(withId(R.id.login_email)).perform(clearText()).
                perform(typeText(TEST_USER_MAIL));
        onView(withId(R.id.login_password)).perform(clearText()).
                perform(typeText(TEST_USER_PASSWORD), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        wait250ms(TAG);

        onView(withId(R.id.activity_list)).check(matches(isDisplayed()));
    }

}
