package hr.etfos.josipvojak.locateme;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IndexActivity extends AppCompatActivity{

    private ListView lvUsers;
    private ArrayList<User> myUsers = new ArrayList<User>();;
    private UserAdapter myArrayAdapter;

    private TextView tvView;

    @Override
    protected void onResume() {
        super.onResume();
        checkGooglePlayServiceVersion();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        checkGooglePlayServiceVersion();
        init();
        updateToken();
    }

    private void updateToken() {
        //Creating a string request
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        final String email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF,"Not Available");
        final String token = sharedPreferences.getString(Config.TOKEN_SHARED_PREF,"Not Available");

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
        //Fetching email from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF,"Not Available");

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
        final String request_sender_email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF,"Not Available");
        final ProgressDialog pDialog = new ProgressDialog(this);

        pDialog.setMessage("Sending a request...");
        pDialog.show();
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.SEND_NOTIFICATION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(!response.equalsIgnoreCase(Config.FAILURE)) {
                            Toast.makeText(IndexActivity.this, "You just sent a request to "+request_receiver_email+"!", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(IndexActivity.this, "User with that email does not exist", Toast.LENGTH_LONG).show();
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
        alertDialogBuilder.setMessage("Are you sure you want to logout?");
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

                        //Putting blank value to email
                        editor.putString(Config.EMAIL_SHARED_PREF, "");

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


    private void search(String query) {
        final String email = query;
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Searching...");
        pDialog.show();

        if (email == "") {
            Toast.makeText(IndexActivity.this, "You didn't enter any email.", Toast.LENGTH_LONG).show();
        } else {

            //Creating a string request
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.SEARCH_URL,
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
                                Toast.makeText(IndexActivity.this, "User with that email does not exist", Toast.LENGTH_LONG).show();
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
