package ch.epfl.sweng.project;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private static final String TEST_USER_MAIL = "test@astutus.org";
    private static final String TEST_USER_PASSWORD = "12345";

    private final String TAG = "MainActivityTest: ";

    private void waitAction(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            if(BuildConfig.DEBUG) {
                Log.d(TAG, "InterruptedException" + e.getMessage());
            }
        }
    }


    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);


    @Test
    public void testUserLogin(){

        waitAction();

        onView(withId(R.id.login_email)).perform(typeText(TEST_USER_MAIL));
        onView(withId(R.id.login_password)).perform(typeText(TEST_USER_PASSWORD));
        onView(withId(R.id.login_button)).perform(click());


        waitAction();

        onView(withId(R.id.activity_list)).check(matches(isDisplayed()));


    }













}
