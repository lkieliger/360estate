package ch.epfl.sweng.project.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.ListActivity;
import ch.epfl.sweng.project.R;

import static ch.epfl.sweng.project.user.InputValidityChecker.*;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity{

    private static final String TAG = "LoginActivity";

    private TextView mEmail;
    private TextView mPassword;

    private Context mAppContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = (TextView) findViewById(R.id.login_email);
        mPassword = (TextView) findViewById(R.id.login_password);
        mAppContext = getApplicationContext();
    }


    /**
     * This method is called when the user clicks on the login button
     * after having entered his credentials
     *
     * @param view The view from which the event was generated
     */
    public void attemptLogin(View view){

        if(userDataIsValid()) {

            ParseUser.logInInBackground(mEmail.getText().toString(),
                    mPassword.getText().toString(),
                    (ParseUser user, ParseException e) -> {
                if (user != null) {
                    Toast popMsg = Toast.makeText(
                            getApplicationContext(),
                            getResources().getText(R.string.info_login_successful),
                            Toast.LENGTH_SHORT);
                    popMsg.show();

                    Intent intent = new Intent(this, ListActivity.class);
                    startActivity(intent);

                    finish();

                } else {
                    Toast popMsg = Toast.makeText(
                            getApplicationContext(),
                            getResources().getText(R.string.error_login_unsuccessful),
                            Toast.LENGTH_SHORT);
                    popMsg.show();
                }
            });
        }
    }

    /**
     * This method is called when the user wants to register a new account
     * instead of directly logging in the app
     *
     * @param view The view from which the event was generated
     */
    public void proceedToRegistration(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * This method checks whether the user input is correct. That is if
     * all TextEdits are filled with apropriate information
     *
     * @return true if user data is valid
     */
    private boolean userDataIsValid(){

        return emailIsValid(mEmail.getText().toString(), mAppContext)&&
                passwordIsValid(mPassword.getText().toString(), mAppContext);
    }

    /**
     * This method determines which fields should be filled for the registration
     * form to be valid
     *
     * @return true if all mandatory fields are filled with some input
     */
    private boolean fieldsAreFilled(){

        boolean filled = !mEmail.getText().toString().isEmpty() &&
                !mPassword.getText().toString().isEmpty() ;

        if(BuildConfig.DEBUG){
            Log.d(TAG, "Result of field checking was: "+ filled);
        }

        if(!filled){
            Toast popMsg = Toast.makeText(
                    getApplicationContext(), R.string.error_empty_field, Toast.LENGTH_SHORT);
            popMsg.show();
        }
        return filled;
    }

}

