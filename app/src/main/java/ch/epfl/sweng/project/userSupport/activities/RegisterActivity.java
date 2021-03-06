package ch.epfl.sweng.project.userSupport.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.Arrays;

import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.util.LogHelper;

import static ch.epfl.sweng.project.util.InputValidityChecker.emailIsValid;
import static ch.epfl.sweng.project.util.InputValidityChecker.passwordIsValid;
import static ch.epfl.sweng.project.util.InputValidityChecker.passwordMatches;
import static ch.epfl.sweng.project.util.Toaster.longToast;
import static ch.epfl.sweng.project.util.Toaster.shortToast;

/**
 * A login screen that offers login via email/password.
 */
public final class RegisterActivity extends AppCompatActivity {

    public static final int USER_ALREADY_EXISTS = 202;
    private static final String TAG = "RegisterActivity";
    private TextView mEmail;
    private TextView mPassword;
    private TextView mPasswordBis;
    private TextView mPhoneNumber;
    private TextView mName;
    private TextView mLastName;
    private Context mAppContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmail = (TextView) findViewById(R.id.registration_email);
        mPassword = (TextView) findViewById(R.id.registration_password);
        mPasswordBis = (TextView) findViewById(R.id.registration_password_bis);
        mPhoneNumber = (TextView) findViewById(R.id.registration_phone);
        mName = (TextView) findViewById(R.id.registration_name);
        mLastName = (TextView) findViewById(R.id.registration_lastname);

        mAppContext = getApplicationContext();

        mPhoneNumber.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // If triggered by an enter key, this is the event; otherwise, this is null.
                if (event != null) {
                    attemptRegistration(v);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }


    /**
     * This method is called when the user clicks on the register button
     * after having entered all the details relative to his/her account
     *
     * @param view The view from which the event was generated
     */
    public void attemptRegistration(View view) {

        if (userDataIsValid()) {
            ParseUser newUser = new ParseUser();
            newUser.setUsername(mEmail.getText().toString());
            newUser.setEmail(mEmail.getText().toString());
            newUser.setPassword(mPassword.getText().toString());

            newUser.put("name", mName.getText().toString());
            newUser.put("lastName", mLastName.getText().toString());
            newUser.put("phone", mPhoneNumber.getText().toString());

            newUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        shortToast(getApplicationContext(),
                                getResources().getString(R.string.info_registration_successful));

                        finish();
                    } else {
                        switch (e.getCode()) {
                            case USER_ALREADY_EXISTS:
                                longToast(getApplicationContext(),
                                        getResources().getString(R.string.error_user_already_exists));
                                return;
                            default:
                                longToast(getApplicationContext(),
                                        getResources().getString(R.string.error_registration_unsuccessful));
                        }

                        Log.e(TAG, Arrays.toString(e.getStackTrace()));
                    }
                }
            });
        }

    }

    /**
     * This method should be called before attempting to register the user on
     * the server side. Its job is to make sure everything the user has entered
     * makes sense
     *
     * @return A boolean value indicating whether the data is valid or not
     */
    private boolean userDataIsValid() {

        return fieldsAreFilled()
                && emailIsValid(mEmail.getText().toString(), mAppContext)
                && passwordIsValid(mPassword.getText().toString(), mAppContext)
                && passwordMatches(mPassword.getText().toString(),
                mPasswordBis.getText().toString(), mAppContext);
    }


    /**
     * This method determines which fields should be filled for the registration
     * form to be valid
     *
     * @return true if all mandatory fields are filled with some input
     */
    private boolean fieldsAreFilled() {

        boolean filled = !mEmail.getText().toString().isEmpty() &&
                !mPassword.getText().toString().isEmpty() &&
                !mPasswordBis.getText().toString().isEmpty() &&
                !mName.getText().toString().isEmpty() &&
                !mLastName.getText().toString().isEmpty();

        LogHelper.log(TAG, "Result of field checking was: " + filled);

        if (!filled) {
            shortToast(getApplicationContext(),
                    getResources().getText(R.string.error_empty_field));
        }
        return filled;
    }
}

