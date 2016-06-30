package hr.etfos.josipvojak.locateme;

import android.*;
import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class SuccessActivity extends AppCompatActivity {

    private static final int PERMISSION_LOCATION_REQUEST_CODE = 0;

    Location myLocation;
    LocationManager myLocationManager;

    String provider;
    Criteria criteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        sendLocation();
    }

    private void sendLocation() {
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        myLocationManager = (LocationManager)
                getSystemService(Service.LOCATION_SERVICE);
        provider = myLocationManager.getBestProvider(criteria, true);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        Intent startingIntent = getIntent();
        Bundle extras = startingIntent.getExtras();
        if (extras.containsKey("email")) {
            String email = extras.getString("email");
            String email_sender = sharedPreferences.getString(Config.EMAIL_SHARED_PREF,"Not Available");
            Toast.makeText(SuccessActivity.this, "You sent your location to " + email, Toast.LENGTH_LONG).show();

            if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_LOCATION_REQUEST_CODE);

                myLocation = myLocationManager.getLastKnownLocation(provider);

                // Obtaining location latitude and longitude
                double latitude = myLocation.getLatitude();
                double longitude = myLocation.getLongitude();

                sendCallbackNotification(email_sender, email, latitude, longitude);

                return;
            }
            if ( Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
             }

            myLocation = myLocationManager.getLastKnownLocation(provider);

            // Obtaining location latitude and longitude
            double latitude = myLocation.getLatitude();
            double longitude = myLocation.getLongitude();

            sendCallbackNotification(email_sender, email, latitude, longitude);
        }
    }

    private void sendCallbackNotification(final String location_sender_email, final String location_receiver_email, final double latitude, final double longitude) {

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.CALLBACK_NOTIFICATION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(!response.equalsIgnoreCase(Config.FAILURE)) {
                            // Do nothing
                        }else{
                            Toast.makeText(SuccessActivity.this, "User with that email does not exist", Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        Toast.makeText(SuccessActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                params.put(Constants.KEY_LOCATION_SENDER_EMAIL, location_sender_email);
                params.put(Constants.KEY_LOCATION_RECEIVER_EMAIL, location_receiver_email);
                params.put(Constants.KEY_LATITUDE, Double.toString(latitude));
                params.put(Constants.KEY_LONGITUDE, Double.toString(longitude));

                //returning parameter
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


}
