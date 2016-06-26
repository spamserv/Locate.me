package hr.etfos.josipvojak.locateme;

/**
 * Created by jvojak on 25.6.2016..
 */
public class Config {
    //URL to our login.php file
    public static final String LOGIN_URL = "http://locate-me.azurewebsites.net/login.php";

    //Keys for email and password as defined in our $_POST['key'] in login.php
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";

    //If server response is equal to this that means login is successful
    public static final String LOGIN_SUCCESS = "success";
    public static final String FAILURE = "failure";

    //Keys for Sharedpreferences
    //This would be the name of our shared preferences
    public static final String SHARED_PREF_NAME = "locatemesession";

    //This would be used to store the session of current logged in user
    public static final String ID_SHARED_PREF = "id";
    public static final String EMAIL_SHARED_PREF = "email";

    //We will use this to store the boolean in sharedpreference to track user is loggedin or not
    public static final String LOGGEDIN_SHARED_PREF = "loggedin";
}
