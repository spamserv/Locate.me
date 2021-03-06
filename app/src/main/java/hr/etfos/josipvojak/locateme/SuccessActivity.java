package hr.etfos.josipvojak.locateme;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class SuccessActivity extends AppCompatActivity implements LocationListener{

    private static final int PERMISSION_LOCATION_REQUEST_CODE = 0;

    private LocationManager myLocationManager;

    private String provider, email, email_sender;
    private Criteria criteria;

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

        final ProgressDialog pDialog = new ProgressDialog(SuccessActivity.this);
        pDialog.show(this,Constants.GETTING_LOCATION,Constants.WAIT);
        if (extras.containsKey(Config.KEY_EMAIL)) {
            email = extras.getString(Config.KEY_EMAIL);
            email_sender = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, Constants.NOT_AVAILABLE);

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_LOCATION_REQUEST_CODE);

                return;
            }
            if (Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            myLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,this);
            myLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
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
                            Toast.makeText(SuccessActivity.this, Constants.INVALID_USER, Toast.LENGTH_LONG).show();
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

    private void startIndexActivity() {
        Intent i = new Intent();
        i.setClass(this, IndexActivity.class);
        this.startActivity(i);
    }

    @Override
    public void onLocationChanged(Location location) {
        // Obtaining location latitude and longitude
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        sendCallbackNotification(email_sender, email, latitude, longitude);
        if (ActivityCompat.checkSelfPermission(SuccessActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SuccessActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Toast.makeText(SuccessActivity.this, Constants.SENDING_LOCATION + email, Toast.LENGTH_LONG).show();
        myLocationManager.removeUpdates(this);
        startIndexActivity();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
