package ch.epfl.sweng.project.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;


import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;


import org.rajawali3d.loader.ParsingException;

import java.util.List;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.DescriptionActivity;
import ch.epfl.sweng.project.ListActivity;
import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.data.Item;

import static ch.epfl.sweng.project.user.InputValidityChecker.emailIsValid;
import static ch.epfl.sweng.project.user.InputValidityChecker.passwordIsValid;
import static ch.epfl.sweng.project.util.Toaster.shortToast;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static boolean parseNotInitialized = true;
    public static final String APP_ID = "360ESTATE";
    private TextView mEmail = null;
    private TextView mPassword = null;
    public static final String  CURRENTUSERID = "currentUserId";


    //private static ParseUser currentUser = null;

    private Context mAppContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        if (parseNotInitialized) {
            //Initialize connection with the parse server


            Parse.initialize(new Parse.Configuration.Builder(this)
                    // The network interceptor is used to debug the communication between server/client
                    //.addNetworkInterceptor(new ParseLogInterceptor())
                    .applicationId(APP_ID)
                    .server("https://360.astutus.org/parse/")
                    .enableLocalDataStore()  // enable the Offline Mode
                    .build()
            );
            //noinspection AssignmentToStaticFieldFromInstanceMethod
            parseNotInitialized = false;
        }
        ParseObject.registerSubclass(Item.class);

        mEmail = (TextView) findViewById(R.id.login_email);
        mPassword = (TextView) findViewById(R.id.login_password);
        mAppContext = getApplicationContext();

        // Check if the LD persisted user information if so transit to the listActivity else proceed on login.


        if(userAlreadyLogIn()){
            Intent intent = new Intent(LoginActivity.this, ListActivity.class);
            startActivity(intent);

            finish();

        }





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


                                // persist user Information
                                System.out.println("confirm login");
                                /**
                                ParseUser.getCurrentUser().pinInBackground(CURRENTUSERID,new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e == null){
                                            System.out.println("the User parse object has been saved correcly");

                                        }else{
                                            System.out.println("Pinn background does not work.");
                                        }
                                    }
                                });

                                 **/





                                Intent intent = new Intent(LoginActivity.this, ListActivity.class);
                                startActivity(intent);

                                finish();




                            } else {
                                shortToast(getApplicationContext(),
                                        getResources().getText(R.string.error_login_unsuccessful));
                            }
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
    public void proceedToRegistration(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
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

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Result of field checking was: " + filled);
        }

        if (!filled) {
            shortToast(getApplicationContext(),
                    getResources().getText(R.string.error_empty_field));
        }
        return filled;
    }

    /**
     * This method helps to decide if the user need to reconnect.
     * @return true if user's information is already cached.
     */
    private boolean userAlreadyLogIn(){
        /**
        int nbrUser=0;
        ParseQuery queryExistingUser = ParseUser.getQuery();
        try {
            nbrUser= queryExistingUser.fromLocalDatastore().count();
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        **/
        boolean isLogIn = false;
        ParseUser currentUser = ParseUser.getCurrentUser();

        if(currentUser != null) {
            isLogIn = currentUser.isAuthenticated();
        }else{
            System.out.println("currentUser is Null.");
        }

        if(isLogIn){
            System.out.println("User already LogIn");

        }else{
            System.out.println("User not logIn.");
        }



        return isLogIn;
    }

}

