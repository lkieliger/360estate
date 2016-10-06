package ch.epfl.sweng.project;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity{

    private static final String TAG = "RegisterActivity";

    private TextView mEmail;
    private TextView mPassword;
    private TextView mPasswordBis;
    private TextView mPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmail = (TextView) findViewById(R.id.registration_email);
        mPassword = (TextView) findViewById(R.id.registration_password);
        mPasswordBis = (TextView) findViewById(R.id.registration_password_bis);
        mPhoneNumber = (TextView) findViewById(R.id.registration_phone);
    }


    /**
     * This method is called when the user clicks on the register button
     * after having entered all the details relative to his/her account
     *
     * @param view The view from which the event was generated
     */
    public void attemptRegistration(View view){

        if(userDataIsValid()){
            ParseUser newUser = new ParseUser();
            newUser.setUsername(mEmail.getText().toString());
            newUser.setEmail(mEmail.getText().toString());
            newUser.setPassword(mPassword.getText().toString());

            newUser.put("phone", mPhoneNumber.getText().toString());

            newUser.signUpInBackground((ParseException e) -> {
                if(e == null){
                    Toast popMsg = Toast.makeText(
                            getApplicationContext(),
                            getResources().getText(R.string.info_registration_successful),
                            Toast.LENGTH_SHORT);
                    popMsg.show();

                    finish();
                } else {
                    Toast popMsg = Toast.makeText(
                            getApplicationContext(),
                            getResources().getText(R.string.error_registration_unsuccessful),
                            Toast.LENGTH_LONG);
                    popMsg.show();

                    Log.e(TAG, e.getStackTrace().toString());
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
    private boolean userDataIsValid(){

        mEmail.setError(null);
        mPassword.setError(null);
        mPasswordBis.setError(null);
        mPhoneNumber.setError(null);

        return emailIsValid()&&
                passwordIsValid() &&
                fieldsAreFilled() &&
                passwordMatches();
    }

    /**
     * This method checks user password for validity. It should satisfy:
     *  - At least 4 chars..
     *
     * @return true if the password is valid
     */
    private boolean passwordIsValid(){
        boolean passLen = mPassword.getText().length() > 4;

        Log.d(TAG, "Password length check returned "+passLen);

        if(!passLen){
            Toast popMsg = Toast.makeText(
                    getApplicationContext(),
                    getResources().getText(R.string.error_invalid_password),
                    Toast.LENGTH_SHORT);
            popMsg.show();
        }

        return passLen;
    }

    /**
     * This method checks user email for validity. It should satisfy:
     *  - At least a @
     *
     * @return true if the email is valid
     */
    private boolean emailIsValid(){
        boolean emailCheck = mEmail.getText().toString().contains("@");

        Log.d(TAG, "Email validity check returned "+ emailCheck);

        if(!emailCheck){
            mEmail.setError(getResources().getString(R.string.error_invalid_email));
        }

        return emailCheck;
    }

    /**
     * @return true if the user entered twice the same password
     */
    private boolean passwordMatches(){
        boolean match = mPassword.getText().toString().equals(
                mPasswordBis.getText().toString());

        Log.d(TAG, "Result of password matching was: "+match);

        if(!match){
            Toast popMsg = Toast.makeText(
                    getApplicationContext(),
                    getResources().getText(R.string.error_unmatching_passwords),
                    Toast.LENGTH_SHORT);
            popMsg.show();
        }

        return match;
    }

    /**
     * This method determines which fields should be filled for the registration
     * form to be valid
     *
     * @return true if all mandatory fields are filled with some input
     */
    private boolean fieldsAreFilled(){

        boolean filled = !mEmail.getText().toString().isEmpty() &&
                !mPassword.getText().toString().isEmpty() &&
                !mPasswordBis.getText().toString().isEmpty();

        Log.d(TAG, "Result of field checking was: "+ filled);

        if(!filled){
            Toast popMsg = Toast.makeText(
                    getApplicationContext(), R.string.error_empty_field, Toast.LENGTH_SHORT);
            popMsg.show();
        }
        return filled;
    }
}

