package ch.epfl.sweng.project.user;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.Arrays;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.R;

import static ch.epfl.sweng.project.user.InputValidityChecker.emailIsValid;
import static ch.epfl.sweng.project.user.InputValidityChecker.passwordIsValid;
import static ch.epfl.sweng.project.user.InputValidityChecker.passwordMatches;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    public static final int USER_ALREADY_EXISTS = 202;

    private TextView mEmail;
    private TextView mPassword;
    private TextView mPasswordBis;
    private TextView mPhoneNumber;
    private Context mAppContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmail = (TextView) findViewById(R.id.registration_email);
        mPassword = (TextView) findViewById(R.id.registration_password);
        mPasswordBis = (TextView) findViewById(R.id.registration_password_bis);
        mPhoneNumber = (TextView) findViewById(R.id.registration_phone);
        mAppContext = getApplicationContext();
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

            newUser.put("phone", mPhoneNumber.getText().toString());

            newUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast popMsg = Toast.makeText(
                                getApplicationContext(),
                                getResources().getString(R.string.info_registration_successful),
                                Toast.LENGTH_SHORT);
                        popMsg.show();

                        finish();
                    } else {
                        Toast popMsg;
                        switch (e.getCode()) {
                            case USER_ALREADY_EXISTS:
                                popMsg = Toast.makeText(
                                        getApplicationContext(),
                                        getResources().getString(R.string.error_user_already_exists),
                                        Toast.LENGTH_LONG);
                                popMsg.show();
                                return;
                            default:
                                 popMsg = Toast.makeText(
                                        getApplicationContext(),
                                        getResources().getString(R.string.error_registration_unsuccessful),
                                        Toast.LENGTH_LONG);
                                popMsg.show();
                        }

                        ;

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

        return  fieldsAreFilled()
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
                !mPasswordBis.getText().toString().isEmpty();

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Result of field checking was: " + filled);
        }

        if (!filled) {
            Toast popMsg = Toast.makeText(
                    getApplicationContext(), R.string.error_empty_field, Toast.LENGTH_SHORT);
            popMsg.show();
        }
        return filled;
    }
}

