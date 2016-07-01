package hr.etfos.josipvojak.locateme;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SeeMapActivity extends Activity implements OnMapReadyCallback {
    private GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_map);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getPeopleLocations();
    }

    private void getPeopleLocations() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setTitle(Constants.OBTAINING_DATA);
        pDialog.setMessage(Constants.WAIT);
        pDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        Map<String, String> data = new HashMap();
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, Constants.NOT_AVAILABLE);
        data.put("email", email);

        JSONObject parameters = new JSONObject(data);

        JsonObjectRequest Req = new JsonObjectRequest(
                Request.Method.POST,
                Constants.GET_ALL_LOCATIONS,
                parameters,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("res");
                            for(int i = 0 ; i < jsonArray.length(); i++){
                                JSONObject jsonObj = jsonArray.getJSONObject(i);
                                if(!jsonObj.has("failure")) {
                                    String email = jsonObj.getString(Constants.KEY_RESPONSE_EMAIL);
                                    String latitude = jsonObj.getString(Constants.KEY_LATITUDE);
                                    String longitude = jsonObj.getString(Constants.KEY_LONGITUDE);

                                    LatLng location = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                    mMap.addMarker(new MarkerOptions()
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.location))
                                            .title(email)
                                            .snippet(Constants.MARKER_MESSAGE)
                                            .position(location));
                                } else {
                                    Toast.makeText(SeeMapActivity.this,Constants.INVALID_USER,Toast.LENGTH_LONG).show();
                                }
                            }
                            pDialog.hide();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.hide();
            }
        }) {

        };

        queue.add(Req);
    }
}
