package fr.iut.festiplandroid;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.iut.festiplandroid.utils.CallAPI;
import fr.iut.festiplandroid.utils.Utils;

/**
 * This class represents the activity displaying details of a festival.
 * It displays the title, category, description, date, and the list of spectacles
 * associated with a given festival.
 * @version 1.0
 * @since 2024-03-21
 */
public class DetailsFestivalActivity extends AppCompatActivity {
    private RequestQueue requestQueue;

    private ListView listSpectacle;
    private TextView title;
    private TextView category;
    private TextView descriptionText;
    private TextView dateText;

    private ArrayList<String> spectaclesList = new ArrayList<>();
    ArrayAdapter<String> adapter;

    private int idFestival;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Utils.makeCustomActionBar(this);

        Utils.checkConnection(this);

        Intent intent = getIntent();
        idFestival = intent.getIntExtra("idFestival", -1);

        listSpectacle = findViewById(R.id.list_spectacle);
        title = findViewById(R.id.title);
        category = findViewById(R.id.categorie);
        descriptionText = findViewById(R.id.description_text);
        dateText = findViewById(R.id.date_text);
        displayDetails();
    }

    /**
     * Displays the details of the festival by retrieving data from an API.
     */
    private void displayDetails() {
        String url = String.format(Utils.URL_API_DETAILS, idFestival);

        requestQueue = Utils.getFileRequete(this, requestQueue);
        CallAPI ca = new CallAPI();
        JsonObjectRequest jsonObjectRequest = ca.requestDetailsFestival(url, this, adapter,
                                              listSpectacle, spectaclesList, title, category,
                                              descriptionText, dateText);

        requestQueue.add(jsonObjectRequest);
    }
}
