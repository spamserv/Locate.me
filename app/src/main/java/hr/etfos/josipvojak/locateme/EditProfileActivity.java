package hr.etfos.josipvojak.locateme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

import static hr.etfos.josipvojak.locateme.R.color.locatePrimary;

public class EditProfileActivity extends AppCompatActivity{

    private EditText etUsername, etStatus;
    private CheckBox cbNotifications;
    private Button btnSaveProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        init();
        obtainProfileInformation();
    }

    private void init() {
        etUsername = (EditText) findViewById(R.id.etUsername);
        etStatus = (EditText) findViewById(R.id.etStatus);
        cbNotifications = (CheckBox) findViewById(R.id.cbNotifications);
        btnSaveProfile = (Button) findViewById(R.id.btnSaveProfile);

        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile(v);
            }
        });

        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(locatePrimary));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
    }


    public void saveProfile(View view) {
        final String username = etUsername.getText().toString().trim();
        final String status = etStatus.getText().toString().trim();

        SharedPreferences sharedPreferences = EditProfileActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        //Creating editor to store values to shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        boolean checked = false;
        String isTrue = "false";
        if(cbNotifications.isChecked()){
            checked = true;
            isTrue = "true";
        }


        //Adding values to editor
        editor.putString(Config.USERNAME_SHARED_PREF, username);
        editor.putString(Config.STATUS_SHARED_PREF, status);
        editor.putString(Config.CHECKED_SHARED_PREF, isTrue);

        //Saving values to editor
        editor.commit();

        updateProfileInDatabase(username,status,checked);
    }

    private void updateProfileInDatabase(final String username, final String status, final boolean checked) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Updating profile...");
        pDialog.show();

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, Constants.NOT_AVAILABLE);
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.UPDATE_PROFILE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(!response.equalsIgnoreCase(Config.FAILURE)) {
                            Toast.makeText(EditProfileActivity.this, "Updated profile successfully!", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(EditProfileActivity.this, "Could not update profile at this time.", Toast.LENGTH_LONG).show();
                        }
                        pDialog.hide();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        Toast.makeText(EditProfileActivity.this, error.toString(), Toast.LENGTH_LONG).show();

                        pDialog.hide();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                params.put(Config.KEY_EMAIL, email);
                params.put(Config.USERNAME_SHARED_PREF,username);
                params.put(Config.STATUS_SHARED_PREF,status);
                if(checked) {
                    params.put(Config.CHECKED_SHARED_PREF, "true");
                } else {
                    params.put(Config.CHECKED_SHARED_PREF, "false");
                }

                //returning parameter
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void obtainProfileInformation() {
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String username = sharedPreferences.getString(Config.USERNAME_SHARED_PREF, Constants.NOT_AVAILABLE);
        final String status = sharedPreferences.getString(Config.STATUS_SHARED_PREF, Constants.NOT_AVAILABLE);
        final String checked = sharedPreferences.getString(Config.CHECKED_SHARED_PREF, Constants.NOT_AVAILABLE);
        if(!username.equalsIgnoreCase(Constants.NOT_AVAILABLE)) {
            etUsername.setText(username);
        }
        if(!status.equalsIgnoreCase(Constants.NOT_AVAILABLE)) {
            etStatus.setText(status);
        }
        if(!checked.equalsIgnoreCase(Constants.NOT_AVAILABLE)) {
            if(checked.equalsIgnoreCase("true")) {
                cbNotifications.setChecked(true);
            }
        }
    }
}
