package ch.epfl.sweng.project;

import android.widget.Button;
import android.widget.TextView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

import ch.epfl.sweng.project.userSupport.activities.RegisterActivity;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)

public class RegisterActivityTests {
    @Test
    public void testInvalidInput() {
        RegisterActivity activity = Robolectric.buildActivity(RegisterActivity.class)
                .create()
                .get();

        Button registerButton = (Button) activity.findViewById(R.id.register_button);
        assertNotNull(registerButton);

        registerButton.performClick();
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(activity.getString(R.string.error_empty_field)));

        TextView registrationEmail = (TextView) activity.findViewById(R.id.registration_email);
        registrationEmail.append("HolaSenior@Shanchez.co");

        TextView registrationPassword = (TextView) activity.findViewById(R.id.registration_password);
        registrationPassword.append("pepe");
        TextView registrationPassBis = (TextView) activity.findViewById(R.id.registration_password_bis);
        registrationPassBis.append("pepe");

        TextView registrationName = (TextView) activity.findViewById(R.id.registration_name);
        registrationName.append("testName");
        TextView registrationLastName = (TextView) activity.findViewById(R.id.registration_lastname);
        registrationLastName.append("testLastName");

        registerButton.performClick();
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(activity.getString(R.string.error_invalid_password)));

        registrationPassword.append("14");
        registrationPassBis.append("145");
        registerButton.performClick();

        assertThat(ShadowToast.getTextOfLatestToast(),
                equalTo(activity.getString(R.string.error_unmatching_passwords)));


        registrationEmail.setText("TOTO");
        registrationPassword.setText("");
        registerButton.performClick();
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(activity.getString(R.string.error_empty_field)));

        registrationPassword.setText("TOTO");
        registrationPassBis.setText("");
        registerButton.performClick();
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(activity.getString(R.string.error_empty_field)));

        registrationPassBis.setText("TOTO");
        registrationName.setText("");
        registerButton.performClick();
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(activity.getString(R.string.error_empty_field)));

        registrationName.setText("TOTO");
        registrationLastName.setText("");
        registerButton.performClick();
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(activity.getString(R.string.error_empty_field)));


    }
}
