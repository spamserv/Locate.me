package hr.etfos.josipvojak.locateme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent startingIntent = getIntent();
        Bundle extras = startingIntent.getExtras();
        if (extras.containsKey("email")){
            String email = extras.getString("email");
            Toast.makeText(MapActivity.this, email+" wants your location!", Toast.LENGTH_LONG).show();
        }


    }
}
