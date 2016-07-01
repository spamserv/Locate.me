package hr.etfos.josipvojak.locateme;

/**
 * Created by jvojak on 29.6.2016..
 */
public class Constants {
    public static final String KEY_LOCATION_RECEIVER_EMAIL = "location_receiver_email";
    public static final String KEY_LOCATION_SENDER_EMAIL = "location_sender_email";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_REQUEST = "request_email";
    public static final String KEY_RESPONSE_EMAIL = "email";

    //Used as logger
    public static final String KEY_TAG = "MyFirebaseMsgService";

    //URLs
    public static final String LOGIN_URL = "http://locate-me.azurewebsites.net/login.php";
    public static final String REGISTER_TOKEN_URL = "http://locate-me.azurewebsites.net/register_token.php";
    public static final String SEARCH_URL = "http://locate-me.azurewebsites.net/search.php";
    public static final String SEND_NOTIFICATION_URL = "http://locate-me.azurewebsites.net/send_notification.php";
    public static final String CALLBACK_NOTIFICATION_URL = "http://locate-me.azurewebsites.net/callback_notification.php";
    public static final String UPDATE_PROFILE_URL = "http://locate-me.azurewebsites.net/update_profile.php";
    public static final String GET_PROFILE_URL = "http://locate-me.azurewebsites.net/get_profile.php";
    public static final String LOGOUT_URL  = "http://locate-me.azurewebsites.net/logout.php";
    //Constants
    public static final String NOT_AVAILABLE = "Not Available";
    public static final String SENDING_LOCATION = "You sent your location to ";
    public static final String INVALID_USER = "User with that email does not exist";

    public static final String API_KEY = "AIzaSyD6al_BVPd_x32eG7pjlWMJUgywqz9nFh0";

    public static final String GETTING_ROUTE = "Getting route...";
    public static final String GETTING_LOCATION = "Getting location";
    public static final String WAIT = "Please wait...";

}
