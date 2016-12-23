package ch.epfl.sweng.project;


import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.EspressoKey;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.KeyEventAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ListView;

import com.parse.ParseUser;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import ch.epfl.sweng.project.data.parse.ParseInitialiser;
import ch.epfl.sweng.project.features.SplashActivity;
import ch.epfl.sweng.project.util.LogHelper;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.actionWithAssertions;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.sweng.project.util.TestUtilityFunctions.logUserOut;
import static ch.epfl.sweng.project.util.TestUtilityFunctions.viewIdDisplayedAfterNattempts;
import static ch.epfl.sweng.project.util.TestUtilityFunctions.wait1s;
import static ch.epfl.sweng.project.util.TestUtilityFunctions.waitForIdNms;
import static ch.epfl.sweng.project.util.TestUtilityFunctions.waitNms;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class CompleteBehaviorTest {

    private static final String TAG = "CompleteBehaviorTest";
    /*
    Random string, for the registration.
    Source:
    http://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string
  */
    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static SecureRandom rnd = new SecureRandom();
    @Rule
    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(SplashActivity.class);

    @SuppressWarnings("AnonymousInnerClassWithTooManyMethods")
    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View item) {
                ViewParent parent = item.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && item.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    private static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    public static ViewAction clickShiftX() {
        return new GeneralClickAction(
                Tap.SINGLE,
                new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {

                        final int[] screenPos = new int[2];
                        view.getLocationOnScreen(screenPos);

                        float[] coordinates = GeneralLocation.CENTER.calculateCoordinates(view);
                        coordinates[1] -= view.getHeight() / 4;

                        return coordinates;
                    }
                },
                Press.PINPOINT
        );
    }

    @Before
    public void checkParseUserLoggedIn() {
        if (ParseUser.getCurrentUser() != null) {
            logUserOut();
            throw new IllegalStateException("The Parse User was already logged in ! Run the test again");
        }
    }

    @After
    public void finishActivity() {
        logUserOut();
        mActivityTestRule.getActivity().finish();
    }

    @Test
    public void testFullApp() {

        String testUserMail = randomString(8) + "@astutus.org";
        String testUserPassword = "12345";
        String testUserPhone = "+078888888";
        String testUserName = "TestName";
        String testUserLastName = "TestLastName";

        waitForIdNms(R.id.goto_registration_button, TimeUnit.SECONDS.toMillis(5));
        onView(withId(R.id.goto_registration_button)).perform(click());

        LogHelper.log(TAG, "Testing already registered user behavior");
        testAlreadyRegisteredUser();

        waitForIdNms(R.id.goto_registration_button, TimeUnit.SECONDS.toMillis(5));
        onView(withId(R.id.goto_registration_button)).perform(click());

        LogHelper.log(TAG, "Testing new user registration");
        registerNewUser(testUserMail, testUserPassword, testUserPhone, testUserName, testUserLastName);

        viewIdDisplayedAfterNattempts(R.id.activity_splash, 3);
        onView(withId(R.id.goto_login_button)).perform(click());

        LogHelper.log(TAG, "Testing invalid login");
        login("HolaSenior@Shanchez.co", "PortesTriEstate");

        waitForIdNms(R.id.goto_reset_button, TimeUnit.SECONDS.toMillis(5));
        onView(withId(R.id.goto_reset_button)).perform(click());

        LogHelper.log(TAG, "Testing password reset activity");
        testResetFunctionality();


        LogHelper.log(TAG, "Testing test user login");
        viewIdDisplayedAfterNattempts(R.id.login_button, 5);
        waitForIdNms(R.id.login_button, TimeUnit.SECONDS.toMillis(10));
        login(testUserMail, testUserPassword);

        waitNms(TAG, TimeUnit.SECONDS.toMillis(6));
        viewIdDisplayedAfterNattempts(R.id.houseList, 6);
        onView(withId(R.id.houseList)).check(matches(isDisplayed()));

        onData(anything()).inAdapterView(withId(R.id.houseList)).atPosition(0).perform(click());

        LogHelper.log(TAG, "Testing contact request feature");
        viewIdDisplayedAfterNattempts(R.id.contactRequestButton, 3);
        onView(withId(R.id.contactRequestButton)).perform(click());
        onView(withId(R.id.contact_refuse)).perform(click());

        onView(withId(R.id.contactRequestButton)).perform(click());
        onView(withId(R.id.contact_accept)).perform(click());

        LogHelper.log(TAG, "Testing favorites feature");
        onView(withId(R.id.addToFavorites)).perform(click());
        pressBack();

        onView(withId(R.id.FavoritesButton)).perform(click());
        waitNms(TAG, TimeUnit.SECONDS.toMillis(3));
        onData(anything()).inAdapterView(withId(R.id.houseList)).atPosition(0).perform(click());

        onView(withId(R.id.addToFavorites)).perform(click());
        pressBack();

        onView(withId(R.id.FavoritesButton)).perform(click());

        // tests the favorites function
        addToFavorite();
        addToFavorite();
        addToFavorite();

        filterTest();

        onView(withId(R.id.FavoritesButton)).perform(click());

        onView(withId(R.id.logOutButton)).perform(click());
        viewIdDisplayedAfterNattempts(R.id.activity_splash, 3);

        LogHelper.log(TAG, "Testing description activity behavior");
        onView(withId(R.id.goto_login_button)).perform(click());

        waitForIdNms(R.id.activity_login, TimeUnit.SECONDS.toMillis(5));
        onView(withId(R.id.activity_login)).check(matches(isDisplayed()));

        onView(withId(R.id.login_email)).perform(replaceText("qwert@qwert.org"), closeSoftKeyboard());
        onView(withId(R.id.login_password)).perform(typeText("12345"));
        onView(withId(R.id.login_password)).perform(enterViewAction());

        viewIdDisplayedAfterNattempts(R.id.activity_list, 3);
        onView(withId(R.id.FavoritesButton)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.houseList)).atPosition(0).perform(click());
        onView(withId(R.id.activity_description)).check(matches(isDisplayed()));

        // wait 3s for the images to load
        waitNms(TAG, TimeUnit.SECONDS.toMillis(3));
        ViewInteraction img0 = onView(childAtPosition(withId(R.id.scroll), 0));
        img0.perform(new CustomClick());

        waitNms(TAG, TimeUnit.SECONDS.toMillis(3));
        pressBack();
        waitNms(TAG, TimeUnit.SECONDS.toMillis(5));

        viewIdDisplayedAfterNattempts(R.id.action_launch_panorama, 10);
        onView(withId(R.id.action_launch_panorama)).perform(click());
        waitNms(TAG, TimeUnit.SECONDS.toMillis(8));

        ViewAction generalClickAction = new GeneralClickAction(Tap.SINGLE,
                GeneralLocation.VISIBLE_CENTER, Press.FINGER);

        onView(withId(R.id.panorama_activity)).perform(actionWithAssertions(generalClickAction));
        waitNms(TAG, TimeUnit.SECONDS.toMillis(5));

        onView(withId(R.id.panorama_activity)).perform(actionWithAssertions(generalClickAction));
        wait1s(TAG);
        onView(withId(R.id.panorama_activity)).perform(actionWithAssertions(generalClickAction));
        wait1s(TAG);

        onView(withId(R.id.panorama_activity)).perform(actionWithAssertions(generalClickAction));
        waitNms(TAG, TimeUnit.SECONDS.toMillis(4));

        onView(withId(R.id.panorama_activity)).perform(clickShiftX());
        waitNms(TAG, TimeUnit.SECONDS.toMillis(3));

        onView(withId(R.id.panorama_activity)).perform(actionWithAssertions(generalClickAction));
        waitNms(TAG, TimeUnit.SECONDS.toMillis(3));
        pressBack();

        //Makes sure the Parse initializer is called twice
        ParseInitialiser.INSTANCE.initParse(mActivityTestRule.getActivity().getBaseContext());
    }

    private void filterTest() {

        onView(withId(R.id.filterButtonPopUp)).perform(click());
        onView(withId(R.id.MaxRooms)).perform(replaceText("3"), closeSoftKeyboard());
        onView(withId(R.id.MinRooms)).perform(replaceText("3"), closeSoftKeyboard());
        onView(withId(R.id.MaxSurface)).perform(replaceText("2000000"), closeSoftKeyboard());
        onView(withId(R.id.MinSurface)).perform(replaceText("2000000"), closeSoftKeyboard());

        onView(withId(R.id.MaxPrice)).perform(replaceText("100"), closeSoftKeyboard());
        onView(withId(R.id.MinPrice)).perform(replaceText("100"), closeSoftKeyboard());

        onView(withId(R.id.location)).perform(replaceText("Renens"), closeSoftKeyboard());
        onView(withId(R.id.spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(getString(R.string.building)))).perform(click());

        onView(withId(R.id.filterButton)).perform(click());

        final int[] counts = {0};
        onView(withId(R.id.houseList)).check(matches(new ViewTypeSafeMatcher(counts)));

        for (int i = 0; i < counts[0]; i++) {
            onData(anything()).inAdapterView(withId(R.id.houseList)).atPosition(i).
                    check(matches(hasDescendant(withText(containsString(
                            String.format(
                                    getString(R.string.text_location_surface),
                                    "Renens",
                                    "2'000'000",
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

        onView(withId(R.id.filterButtonPopUp)).perform(click());
        onView(withId(R.id.eraseButton)).perform(click());
        onView(withId(R.id.filterButton)).perform(click());
    }

    private void registerNewUser(String testUserMail, String testUserPassword, String testUserPhone,
                                 String testUserName, String testUserLastName) {

        waitForIdNms(R.id.activity_register, TimeUnit.SECONDS.toMillis(5));
        onView(withId(R.id.activity_register)).check(matches(isDisplayed()));

        onView(withId(R.id.registration_name)).perform(replaceText(testUserName), closeSoftKeyboard());
        onView(withId(R.id.registration_lastname)).perform(replaceText(testUserLastName), closeSoftKeyboard());
        onView(withId(R.id.registration_email)).perform(replaceText(testUserMail), closeSoftKeyboard());
        onView(withId(R.id.registration_password)).perform(replaceText(testUserPassword), closeSoftKeyboard());
        onView(withId(R.id.registration_password_bis)).perform(replaceText(testUserPassword), closeSoftKeyboard());
        onView(withId(R.id.registration_phone)).perform(replaceText(testUserPhone), closeSoftKeyboard());
        onView(withId(R.id.register_button)).perform(click());
    }

    private void testAlreadyRegisteredUser() {

        waitForIdNms(R.id.activity_register, TimeUnit.SECONDS.toMillis(5));
        onView(withId(R.id.activity_register)).check(matches(isDisplayed()));

        onView(withId(R.id.registration_name)).perform(replaceText("test"), closeSoftKeyboard());
        onView(withId(R.id.registration_lastname)).perform(replaceText("astutus"), closeSoftKeyboard());
        onView(withId(R.id.registration_email)).perform(replaceText("test@astutus.org"), closeSoftKeyboard());
        onView(withId(R.id.registration_password)).perform(replaceText("abcdef"), closeSoftKeyboard());
        onView(withId(R.id.registration_password_bis)).perform(replaceText("abcdef"), closeSoftKeyboard());
        onView(withId(R.id.registration_phone)).perform(typeText("0789123142"));
        onView(withId(R.id.registration_phone)).perform(enterViewAction());
        // Causes an error because the user is already registered

        onView(withId(R.id.registration_phone)).perform(closeSoftKeyboard());
        pressBack();
    }

    private void testResetFunctionality() {

        waitForIdNms(R.id.activity_reset, TimeUnit.SECONDS.toMillis(5));
        onView(withId(R.id.activity_reset)).check(matches(isDisplayed()));

        // Invalid mail
        onView(withId(R.id.reset_email)).perform(replaceText("test@invalidMail"), closeSoftKeyboard());
        onView(withId(R.id.reset_button)).perform(click());

        // Error "no email matching"
        onView(withId(R.id.reset_email)).perform(replaceText("UnkownEmail@astutus.org"), closeSoftKeyboard());
        onView(withId(R.id.reset_button)).perform(click());

        // perform real reset
        onView(withId(R.id.reset_email)).perform(replaceText("test@astutus.org"), closeSoftKeyboard());
        onView(withId(R.id.reset_button)).perform(click());
    }


    private void login(String testUserMail, String testUserPassword) {

        waitForIdNms(R.id.activity_login, TimeUnit.SECONDS.toMillis(5));
        onView(withId(R.id.activity_login)).check(matches(isDisplayed()));

        onView(withId(R.id.login_email)).perform(replaceText(testUserMail), closeSoftKeyboard());
        onView(withId(R.id.login_password)).perform(replaceText(testUserPassword), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
    }

    private void addToFavorite() {
        viewIdDisplayedAfterNattempts(R.id.houseList, 6);
        onData(anything()).inAdapterView(withId(R.id.houseList)).atPosition(0).perform(click());
        onView(withId(R.id.addToFavorites)).perform(click());
        pressBack();
    }

    private String getString(int id) {
        return mActivityTestRule.getActivity().getString(id);
    }

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

    private static class CustomClick implements ViewAction {
        @Override
        public Matcher<View> getConstraints() {
            return isDisplayingAtLeast(60);
        }

        @Override
        public String getDescription() {
            return "click plus button";
        }

        @Override
        public void perform(UiController uiController, View view) {
            float[] coordinates = GeneralLocation.VISIBLE_CENTER.calculateCoordinates(view);
            float[] precision = Press.PINPOINT.describePrecision();
            Tap.SINGLE.sendTap(uiController, coordinates, precision);
        }
    }

    public ViewAction enterViewAction() {
        return new KeyEventAction(new EspressoKey.Builder()
                .withKeyCode(KeyEvent.KEYCODE_ENTER).build());
    }

}
