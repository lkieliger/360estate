package ch.epfl.sweng.project;

import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseUser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

import ch.epfl.sweng.project.user.LoginActivity;
import ch.epfl.sweng.project.user.RegisterActivity;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)

public class LoginActivityTests {


    @Test
    public void testInvalidInput() {
        LoginActivity activity = Robolectric.buildActivity(LoginActivity.class).create().get();

        Button loginButton = (Button) activity.findViewById(R.id.login_button);
        assertNotNull(loginButton);
        loginButton.performClick();

        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(activity.getString(R.string.error_empty_field)));

        TextView loginText = (TextView) activity.findViewById(R.id.login_email);
        TextView passwordText = (TextView) activity.findViewById(R.id.login_password);

        loginText.append("test@");
        passwordText.append("abc");

        loginButton.performClick();

        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(activity.getString(R.string.error_invalid_email)));

        loginText.append("real.com");
        loginButton.performClick();

        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(activity.getString(R.string.error_invalid_password)));
    }


    @Test
    public void registerActivityIsLaunched() {
        SplashActivity activity = Robolectric.buildActivity(SplashActivity.class).create().get();
        Button registrationButton = (Button) activity.findViewById(R.id.goto_registration_button);
        registrationButton.performClick();

        Intent expectedIntent = new Intent(activity, RegisterActivity.class);

        assertThat(shadowOf(activity).getNextStartedActivity().toString(), equalTo(expectedIntent.toString()));
    }

}
