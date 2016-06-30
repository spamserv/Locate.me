package hr.etfos.josipvojak.locateme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

public class MapActivity extends AppCompatActivity {
    private GoogleMap googleMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent startingIntent = getIntent();
        Bundle extras = startingIntent.getExtras();
        if (extras.containsKey("email") && extras.containsKey("latitude") && extras.containsKey("longitude")){
            String email = extras.getString("email");

            double latitude = Double.parseDouble(extras.getString("latitude"));
            double longitude = Double.parseDouble(extras.getString("longitude"));

        }

        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();



    }
}
