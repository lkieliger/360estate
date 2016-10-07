package ch.epfl.sweng.project.user;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.R;

final class InputValidityChecker {

    private static final String TAG = "ValidityChecker";

    /*
    Regex used to validate the email field.
    Source : http://stackoverflow.com/questions/8204680/java-regex-email#answer-8204716
     */
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private InputValidityChecker(){

    }

    /**
     * This method checks user password for validity. It should satisfy:
     *  - At least 4 chars..
     *
     * @return true if the password is valid
     */
    static boolean passwordIsValid(String pwd, Context context){
        boolean passLen = pwd.length() > 4;

        if(BuildConfig.DEBUG){
            Log.d(TAG, "Password length check returned "+passLen);
        }

        if(!passLen){
            Toast popMsg = Toast.makeText(
                    context,
                    context.getResources().getText(R.string.error_invalid_password),
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
    static boolean emailIsValid(String email, Context context){
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(email);
        boolean emailCheck = matcher.find();

        if(BuildConfig.DEBUG){
            Log.d(TAG, "Email validity check returned "+ emailCheck);
        }

        if(!emailCheck){
            Toast popMsg = Toast.makeText(
                    context,
                    context.getResources().getText(R.string.error_invalid_email),
                    Toast.LENGTH_SHORT);
            popMsg.show();
        }

        return emailCheck;
    }



    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

    /**
     * @return true if the input is twice the same password
     */
    static boolean passwordMatches(String pwd1, String pwd2, Context context){
        boolean match = pwd1.equals(pwd2);

        if(BuildConfig.DEBUG){
            Log.d(TAG, "Result of password matching was: "+match);
        }

        if(!match){
            Toast popMsg = Toast.makeText(
                    context,
                    context.getResources().getText(R.string.error_unmatching_passwords),
                    Toast.LENGTH_SHORT);
            popMsg.show();
        }

        return match;
    }
}
