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
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;



@RunWith(AndroidJUnit4.class)
public class RegisterActivityTest {
    private static final String TAG = "RegisterActivityTest: ";



    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private void waitAction(){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            if(BuildConfig.DEBUG) {
                Log.d(TAG, "InterruptedException" + e.getMessage());
            }
        }
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
