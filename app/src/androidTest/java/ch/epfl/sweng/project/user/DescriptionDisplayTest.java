package ch.epfl.sweng.project.user;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.project.DescriptionActivity;
import ch.epfl.sweng.project.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;


@RunWith(AndroidJUnit4.class)
public class DescriptionDisplayTest {

    @Rule
    public ActivityTestRule<DescriptionActivity> mActivityTestRule = new ActivityTestRule<>(DescriptionActivity.class);

    @Test
    public void descriptionDisplayTest() {
        ViewInteraction textView = onView(
                allOf(withId(R.id.description),
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

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.Title),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.activity_description),
                                        0),
                                0),
                        isDisplayed()));
        textView2.check(matches(isDisplayed()));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.text_title),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.activity_description),
                                        0),
                                2),
                        isDisplayed()));
        textView3.check(matches(isDisplayed()));

        ViewInteraction imageView4  = onView(
                allOf(
                        withId(R.id.scroll),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.activity_description),0)
                                ,1),isDisplayed()
                ));
        imageView4.check(matches(isDisplayed()));

        onView(
                allOf(
                        withId(R.id.scroll),
                            childAtPosition(
                                childAtPosition(
                                    withId(R.id.activity_description),0)
                            ,1))).perform(click());




        ViewInteraction imageView3 = onView(
                allOf(withId(R.id.displayed_image),
                        childAtPosition(
                                allOf(withId(R.id.activity_display),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        imageView3.check(matches(isDisplayed()));

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
