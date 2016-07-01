package hr.etfos.josipvojak.locateme;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static hr.etfos.josipvojak.locateme.R.color.locatePrimary;

public class IndexActivity extends AppCompatActivity{

    private ListView lvUsers;
    private ArrayList<User> myUsers = new ArrayList<User>();;
    private UserAdapter myArrayAdapter;

    private TextView tvView, tvProfilePic;

    @Override
    protected void onResume() {
        super.onResume();
        checkGooglePlayServiceVersion();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(locatePrimary));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        checkGooglePlayServiceVersion();
        init();
        updateToken();
    }

    private void updateToken() {
        //Creating a string request
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        final String email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF,Constants.NOT_AVAILABLE);
        final String token = sharedPreferences.getString(Config.TOKEN_SHARED_PREF,Constants.NOT_AVAILABLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.REGISTER_TOKEN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Do nothing on response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                params.put(Config.KEY_EMAIL, email);
                params.put(Config.KEY_TOKEN, token);

                //returning parameter
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void init() {
        //Initializing textview
        tvView = (TextView) findViewById(R.id.tvView);
        tvProfilePic = (TextView) findViewById(R.id.tvProfilePic);
        //Fetching email from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF,Constants.NOT_AVAILABLE);

        //Showing the current logged in email to textview
        tvView.setText("Current User: " + email);

        lvUsers = (ListView) findViewById(R.id.lvUsers);
        this.lvUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = (User) myArrayAdapter.getItem(position);
                sendLocationRequest(user.getEmail());
            }
        });

    }

    private void sendLocationRequest(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        final String request_receiver_email = email;
        final String request_sender_email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF,Constants.NOT_AVAILABLE);
        final ProgressDialog pDialog = new ProgressDialog(this);

        pDialog.setMessage(Constants.SENDING_REQUEST);
        pDialog.show();
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.SEND_NOTIFICATION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(!response.equalsIgnoreCase(Config.FAILURE)) {
                            Toast.makeText(IndexActivity.this, "You just sent a request to "+request_receiver_email+"!", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(IndexActivity.this, Constants.INVALID_USER, Toast.LENGTH_LONG).show();
                        }
                        pDialog.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        Toast.makeText(IndexActivity.this, error.toString(), Toast.LENGTH_LONG).show();

                        pDialog.hide();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                params.put(Config.KEY_EMAIL, request_receiver_email);
                params.put(Constants.KEY_REQUEST, request_sender_email);

                //returning parameter
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    //Logout function
    private void logout(){
        //Creating an alert dialog to confirm logout
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(Constants.LOGOUT_QUESTION);
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        //Getting out sharedpreferences
                        SharedPreferences preferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        //Getting editor
                        SharedPreferences.Editor editor = preferences.edit();

                        //Puting the value false for loggedin
                        editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);
                        String email = preferences.getString(Config.EMAIL_SHARED_PREF,"Not Available");
                        deleteTokenFromDatabase(email);

                        //Putting blank value to email
                        editor.putString(Config.EMAIL_SHARED_PREF, "");
                        editor.putString(Config.USERNAME_SHARED_PREF, "");
                        editor.putString(Config.STATUS_SHARED_PREF, "");
                        editor.putString(Config.CHECKED_SHARED_PREF, "");

                        //Saving the sharedpreferences
                        editor.commit();

                        //Starting login activity
                        Intent intent = new Intent(IndexActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        //Showing the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private void deleteTokenFromDatabase(final String email) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Logging out...");
        pDialog.show();

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.LOGOUT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(!response.equalsIgnoreCase(Config.FAILURE)) {
                            String email = response.toString();
                            User user = new User(email);
                            myUsers.add(user);
                            myArrayAdapter = new UserAdapter(IndexActivity.this, myUsers);
                            lvUsers.setAdapter(myArrayAdapter);
                        }else{
                            Toast.makeText(IndexActivity.this, Constants.INVALID_USER, Toast.LENGTH_LONG).show();
                        }
                        pDialog.hide();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        Toast.makeText(IndexActivity.this, error.toString(), Toast.LENGTH_LONG).show();

                        pDialog.hide();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                params.put(Config.KEY_EMAIL, email);

                //returning parameter
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void search(String query) {
        final String email = query;
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(Constants.SEARCHING);
        pDialog.show();

        if (email.equalsIgnoreCase("")) {
            Toast.makeText(IndexActivity.this, Constants.NO_EMAIL, Toast.LENGTH_LONG).show();
        } else {

            RequestQueue queue = Volley.newRequestQueue(this);
            Map<String, String> data = new HashMap();
            data.put("email", email);

            JSONObject parameters = new JSONObject(data);

            JsonObjectRequest Req = new JsonObjectRequest(
                    Request.Method.POST,
                    Constants.SEARCH_URL,
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
                                        String username = jsonObj.getString(Constants.KEY_USERNAME);
                                        String status = jsonObj.getString(Constants.KEY_STATUS);
                                        String last_online = jsonObj.getString(Constants.KEY_LAST_ONLINE);
                                        if (username.equalsIgnoreCase("null")) {
                                            username = "Not set.";
                                        }

                                        if (status.equalsIgnoreCase("null")) {
                                            status = "Not status yet.";
                                        }

                                        User user = new User(email, username, status, last_online);
                                        myUsers.add(user);
                                        myArrayAdapter = new UserAdapter(IndexActivity.this, myUsers);
                                        lvUsers.setAdapter(myArrayAdapter);
                                    } else {
                                        Toast.makeText(IndexActivity.this,Constants.INVALID_USER,Toast.LENGTH_LONG).show();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            pDialog.hide();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Adding our menu to toolbar
        getMenuInflater().inflate(R.menu.menu, menu);

        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menuSearch));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                // this is your adapter that will be filtered
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                //Here u can get the value "query" which is entered in the search box.
                search(query);
                searchView.clearFocus();
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuLogout) {
            //calling logout method when the logout button is clicked
            logout();
        } else if(id == R.id.menuEditProfile) {
            Intent intent = new Intent(IndexActivity.this, EditProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.menuSeeMap) {
            Intent intent = new Intent(IndexActivity.this, SeeMapActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkGooglePlayServiceVersion() {
        Integer resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode == ConnectionResult.SUCCESS) {
            //Do what you want
        } else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                //This dialog will help the user update to the latest GooglePlayServices
                dialog.show();
            }
        }
    }

}
