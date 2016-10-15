package ch.epfl.sweng.project;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.project.user.RegisterActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;


@RunWith(AndroidJUnit4.class)

public class RegisterActivityTest {
    private static final String TAG = "RegisterActivityTest: ";

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public ActivityTestRule<RegisterActivity> mRegisterActivityTestRule = new ActivityTestRule<>(RegisterActivity
            .class);


    private void waitAction() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "InterruptedException" + e.getMessage());
            }
        }
    }

    private void initTest(){
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.goto_registration_button), withText(mActivityTestRule.getActivity().
                        getString(R.string.action_goto_registration))));
        appCompatButton.perform(scrollTo(), click());
    }

    @Test
    public void userAlreadyPresent() {
        initTest();

        ViewInteraction appCompatTextView = onView(
                withId(R.id.registration_email));
        appCompatTextView.perform(scrollTo(), replaceText("test@astutus.org"), closeSoftKeyboard());

        ViewInteraction appCompatEditText = onView(
                withId(R.id.registration_password));
        appCompatEditText.perform(scrollTo(), replaceText("abcdef"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                withId(R.id.registration_password_bis));
        appCompatEditText2.perform(scrollTo(), replaceText("abcdef"), closeSoftKeyboard());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.register_button), withText(mActivityTestRule.getActivity().
                                getString(R.string.action_register)),
                        withParent(allOf(withId(R.id.email_login_form),
                                withParent(withId(R.id.login_form))))));
        appCompatButton2.perform(scrollTo(), click());

        waitAction();

        onView(withText(R.string.error_user_already_exists)).inRoot(withDecorView(not(is(mActivityTestRule.getActivity()
                .getWindow().getDecorView())))).check(matches(isDisplayed()));

    }


    @Test
    public void errorWithdEmptyField(){
        initTest();
        waitAction();
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.goto_registration_button), withText(mActivityTestRule.getActivity().
                        getString(R.string.action_goto_registration))));

        onView(withId(R.id.registration_password)).perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.register_button)).perform(click());

        onView(withText(R.string.error_empty_field)).inRoot(withDecorView(not(is(mActivityTestRule.getActivity()
                .getWindow().getDecorView())))).check(matches(isDisplayed()));

    }

    @Test
    public void errorWithInvalidPassword(){
        initTest();
        waitAction();

        onView(withId(R.id.registration_email)).perform(typeText("HolaSenior@Shanchez.co"), closeSoftKeyboard());
        onView(withId(R.id.registration_phone)).perform(typeText("05404030"), closeSoftKeyboard());
        onView(withId(R.id.registration_password)).perform(typeText("pepe"), closeSoftKeyboard());
        onView(withId(R.id.registration_password_bis)).perform(typeText("pepe"), closeSoftKeyboard());
        onView(withId(R.id.register_button)).perform(click());

        onView(withText(R.string.error_invalid_password)).inRoot(withDecorView(not(is(mActivityTestRule.getActivity()
                .getWindow().getDecorView())))).check(matches(isDisplayed()));

    }


    @Test
    public void errorWithUnmatchingPassword(){
        initTest();
        waitAction();

        onView(withId(R.id.registration_email)).perform(typeText("HolaSenior@Shanchez.co"), closeSoftKeyboard());
        onView(withId(R.id.registration_phone)).perform(typeText("05404030"), closeSoftKeyboard());
        onView(withId(R.id.registration_password)).perform(typeText("pepeTheFrog"), closeSoftKeyboard());
        onView(withId(R.id.registration_password_bis)).perform(typeText("PepeTheFrog"), closeSoftKeyboard());
        onView(withId(R.id.register_button)).perform(click());

        onView(withText(R.string.error_unmatching_passwords)).inRoot(withDecorView(not(is(mActivityTestRule
                .getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

    }

}
