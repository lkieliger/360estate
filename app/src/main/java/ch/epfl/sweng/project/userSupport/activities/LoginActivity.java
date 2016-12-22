package ch.epfl.sweng.project.userSupport.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.features.propertylist.ListActivity;
import ch.epfl.sweng.project.util.LogHelper;

import static ch.epfl.sweng.project.util.InputValidityChecker.emailIsValid;
import static ch.epfl.sweng.project.util.InputValidityChecker.passwordIsValid;
import static ch.epfl.sweng.project.util.Toaster.shortToast;

/**
 * A login screen that offers login via email/password.
 */
public final class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private AutoCompleteTextView mEmail = null;
    private EditText mPassword = null;
    private Context mAppContext = null;
    private boolean resetButtonInvisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = (AutoCompleteTextView) findViewById(R.id.login_email);
        mPassword = (EditText) findViewById(R.id.login_password);
        mAppContext = getApplicationContext();

        mPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // If triggered by an enter key, this is the event; otherwise, this is null.
                if (event != null) {
                    attemptLogin(v);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    /**
     * This method is called when the user clicks on the login button
     * after having entered his credentials
     *
     * @param view The view from which the event was generated
     */
    public void attemptLogin(View view) {

        if (fieldsAreFilled() && userDataIsValid()) {

            ParseUser.logInInBackground(mEmail.getText().toString(),
                    mPassword.getText().toString(),
                    new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (user != null) {
                                shortToast(getApplicationContext(),
                                        getResources().getText(R.string.info_login_successful));

                                Intent intent = new Intent(LoginActivity.this, ListActivity.class);
                                startActivity(intent);

                                finish();

                            } else {
                                shortToast(getApplicationContext(),
                                        getResources().getText(R.string.error_login_unsuccessful));

                                if (resetButtonInvisible) {

                                    Button resetButton = (Button) findViewById(R.id.goto_reset_button);
                                    resetButton.setVisibility(View.VISIBLE);

                                    resetButtonInvisible = false;
                                }
                            }

                        }
                    });
        }
    }


    /**
     * This method is called when the user wants to reset his password
     *
     * @param view The view from which the event was generated
     */
    public void proceedToReset(View view) {
        Intent intent = new Intent(this, ResetActivity.class);
        startActivity(intent);
    }


    /**
     * This method checks whether the user input is correct. That is if
     * all TextEdits are filled with apropriate information
     *
     * @return true if user data is valid
     */
    private boolean userDataIsValid() {

        return emailIsValid(mEmail.getText().toString(), mAppContext) &&
                passwordIsValid(mPassword.getText().toString(), mAppContext);
    }

    /**
     * This method determines which fields should be filled for the registration
     * form to be valid
     *
     * @return true if all mandatory fields are filled with some input
     */
    private boolean fieldsAreFilled() {

        boolean filled = !mEmail.getText().toString().isEmpty() &&
                !mPassword.getText().toString().isEmpty();

        LogHelper.log(TAG, "Result of field checking was: " + filled);


        if (!filled) {
            shortToast(getApplicationContext(),
                    getResources().getText(R.string.error_empty_field));
        }
        return filled;
    }

}

