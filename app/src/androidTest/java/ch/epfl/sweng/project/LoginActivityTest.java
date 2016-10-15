package ch.epfl.sweng.project;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    private static final String TAG = "LoginActivityTest";
    private static final String TEST_USER_MAIL = "test@astutus.org";
    private static final String TEST_USER_PASSWORD = "12345";

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);



    private void waitAction() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "InterruptedException" + e.getMessage());
            }
        }
    }

    @Test
    public void errorWithInvalidLogin() {

        waitAction();

        onView(withId(R.id.login_email)).perform(typeText("HolaSenior@Shanchez.co"), closeSoftKeyboard());
        onView(withId(R.id.login_password)).perform(typeText("PortesTriEstate"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        waitAction();

        onView(withText(R.string.error_login_unsuccessful)).inRoot(withDecorView(not(is(mActivityTestRule.getActivity()
                .getWindow().getDecorView())))).check(matches(isDisplayed()));

    }

    @Test
    public void errorWithInvalidLoginMail(){

        waitAction();

        onView(withId(R.id.login_email)).perform(typeText("HolaSenior@Shanchez"), closeSoftKeyboard());
        onView(withId(R.id.login_password)).perform(typeText("PortesTriEstate"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        onView(withText(R.string.error_invalid_email)).inRoot(withDecorView(not(is(mActivityTestRule.getActivity()
                .getWindow().getDecorView())))).check(matches(isDisplayed()));

    }

    @Test
    public void errorWithInvalidLoginPassWord(){

        waitAction();

        onView(withId(R.id.login_email)).perform(typeText("HolaSenior@Shanchez.co"), closeSoftKeyboard());
        onView(withId(R.id.login_password)).perform(typeText("si"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        onView(withText(R.string.error_invalid_password)).inRoot(withDecorView(not(is(mActivityTestRule.getActivity()
                .getWindow().getDecorView())))).check(matches(isDisplayed()));

    }

    @Test
    public void testUserLogin(){

        waitAction();

        onView(withId(R.id.login_email)).perform(typeText(TEST_USER_MAIL));
        onView(withId(R.id.login_password)).perform(typeText(TEST_USER_PASSWORD));
        onView(withId(R.id.login_button)).perform(click());

        waitAction();

        onView(withId(R.id.activity_list)).check(matches(isDisplayed()));

    }

    @Test
    public void registerCall(){
        waitAction();

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.goto_registration_button), withText(mActivityTestRule.getActivity().
                        getString(R.string.action_goto_registration))));
        appCompatButton.perform(scrollTo(), click());

        waitAction();

        onView(withId(R.id.register_button)).check(matches(isDisplayed()));
    }
}