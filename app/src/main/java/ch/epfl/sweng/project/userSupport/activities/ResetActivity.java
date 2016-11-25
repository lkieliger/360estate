package ch.epfl.sweng.project.userSupport.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import java.util.Arrays;

import ch.epfl.sweng.project.R;

import static ch.epfl.sweng.project.util.InputValidityChecker.emailIsValid;
import static ch.epfl.sweng.project.util.Toaster.longToast;
import static ch.epfl.sweng.project.util.Toaster.shortToast;

/**
 * A login screen that permits the user to reset its password.
 */
public final class ResetActivity extends AppCompatActivity {

    private static final String TAG = "ResetActivity";
    private static final int NO_EMAIL_MATCHING = 205;
    private TextView mEmail;
    private Context mAppContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        mEmail = (TextView) findViewById(R.id.reset_email);
        mAppContext = getApplicationContext();
    }


    /**
     * This method is called when the user clicks on the reset button
     *
     * @param view The view from which the event was generated
     */
    public void attemptPasswordReset(View view) {

        if (userDataIsValid()) {

            ParseUser.requestPasswordResetInBackground(mEmail.getText().toString(), new RequestPasswordResetCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        longToast(getApplicationContext(),
                                getResources().getString(R.string.info_reset_successful));
                        finish();
                    } else {
                        switch (e.getCode()) {
                            case NO_EMAIL_MATCHING:
                                longToast(getApplicationContext(),
                                        getResources().getString(R.string.error_reset_no_email_matching));
                                break;
                            default:
                                longToast(getApplicationContext(),
                                        getResources().getString(R.string.error_reset_unsuccessful));
                        }

                        Log.e(TAG, Arrays.toString(e.getStackTrace()));

                    }
                }
            });
        }
    }

    /**
     * This method should be called before attempting to reset the password
     *
     * @return A boolean value indicating whether the data is valid or not
     */
    private boolean userDataIsValid() {

        return fieldsAreFilled() &&
                emailIsValid(mEmail.getText().toString(), mAppContext);
    }


    /**
     * This method determines which fields should be filled for the registration
     * form to be valid
     *
     * @return true if all mandatory fields are filled with some input
     */
    private boolean fieldsAreFilled() {

        boolean filled = !mEmail.getText().toString().isEmpty();

        if (!filled) {
            shortToast(getApplicationContext(),
                    getResources().getText(R.string.error_empty_field));
        }
        return filled;
    }
}

