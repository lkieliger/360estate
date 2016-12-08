package ch.epfl.sweng.project.util;

import android.content.Context;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.R;

import static ch.epfl.sweng.project.util.Toaster.shortToast;

public final class InputValidityChecker {

    private static final String TAG = "ValidityChecker";

    /*
    Regex used to validate the email field.
    Source : http://stackoverflow.com/questions/8204680/java-regex-email#answer-8204716
     */
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private InputValidityChecker() {
    }

    /**
     * This method checks user password for validity. It should satisfy:
     * - At least 4 chars..
     *
     * @return true if the password is valid
     */
    public static boolean passwordIsValid(String pwd, Context context) {
        boolean passLen = pwd.length() > 4;

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Password length check returned " + passLen);
        }

        if (!passLen) {
            shortToast(context,
                    context.getResources().getText(R.string.error_invalid_password));
        }

        return passLen;
    }

    /**
     * This method checks user name or lastname for validity
     *
     * @return true if the name or lastname is valid
     */
    public static boolean nameIsValid(String name, Context context) {
        //TODO: implement validity check
        return true;
    }

    /**
     * This method checks user email for validity. It should satisfy:
     * - At least a @
     *
     * @return true if the email is valid
     */
    public static boolean emailIsValid(String email, Context context) {
        boolean emailCheck = validate(email);

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Email validity check returned " + emailCheck);
        }

        if (!emailCheck) {
            shortToast(context,
                    context.getResources().getText(R.string.error_invalid_email));
        }

        return emailCheck;
    }


    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    /**
     * @return true if the input is twice the same password
     */
    public static boolean passwordMatches(String pwd1, String pwd2, Context context) {
        boolean match = pwd1.equals(pwd2);

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Result of password matching was: " + match);
        }

        if (!match) {
            shortToast(context,
                    context.getResources().getText(R.string.error_unmatching_passwords));
        }

        return match;
    }
}
