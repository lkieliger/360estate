package ch.epfl.sweng.project.util;

import android.content.Context;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.epfl.sweng.project.R;

import static ch.epfl.sweng.project.util.Toaster.shortToast;

/**
 * This class helps to make sure the string is valid regarding a certain convention.
 */
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

        LogHelper.log(TAG, "Password length check returned " + passLen);

        if (!passLen) {
            shortToast(context,
                    context.getResources().getText(R.string.error_invalid_password));
        }

        return passLen;
    }

    /**
     * This method checks user email for validity. It should satisfy:
     * - At least a @
     *
     * @return true if the email is valid
     */
    public static boolean emailIsValid(String email, Context context) {
        boolean emailCheck = validate(email);

        LogHelper.log(TAG, "Email validity check returned " + emailCheck);

        if (!emailCheck) {
            shortToast(context,
                    context.getResources().getText(R.string.error_invalid_email));
        }

        return emailCheck;
    }


    private static boolean validate(CharSequence emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    /**
     * @return true if the input is twice the same password
     */
    public static boolean passwordMatches(String pwd1, String pwd2, Context context) {
        boolean match = pwd1.equals(pwd2);

        LogHelper.log(TAG, "Result of password matching was: " + match);

        if (!match) {
            shortToast(context,
                    context.getResources().getText(R.string.error_unmatching_passwords));
        }

        return match;
    }
}
