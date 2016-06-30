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
}
