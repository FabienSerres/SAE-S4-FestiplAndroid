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

public class DetailsFestivalActivity extends AppCompatActivity {
    private RequestQueue requestQueue;

    private ListView listSpectacle;
    private TextView title;
    private TextView categorie;
    private TextView description_text;
    private TextView date_text;

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

        Intent intention = getIntent();
        idFestival = intention.getIntExtra("idFestival", -1);

        listSpectacle = findViewById(R.id.list_spectacle);
        title = findViewById(R.id.title);
        categorie = findViewById(R.id.categorie);
        description_text = findViewById(R.id.description_text);
        date_text = findViewById(R.id.date_text);

        displayDetails();
    }

    private void displayDetails() {
        String url = String.format(Utils.URL_API_DETAILS, idFestival);

        requestQueue = Utils.getFileRequete(this, requestQueue);
        CallAPI ca = new CallAPI();
        JsonObjectRequest jsonObjectRequest = requestDeleteFavoriteFestival(url);

        requestQueue.add(jsonObjectRequest);
    }

    private JsonObjectRequest requestDeleteFavoriteFestival(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("RESULTAT", response.toString());

                        try {
                            JSONObject festivalInfo = response.getJSONObject("festival");
                            title.setText(festivalInfo.getString("titre"));
                            categorie.setText(categorie.getText() + festivalInfo.getString("nom"));
                            description_text.setText(festivalInfo.getString("description"));
                            date_text.setText(festivalInfo.getString("dateDebut") + " - "
                                              + festivalInfo.getString("dateFin"));

                            JSONArray spectaclesInfo = response.getJSONArray("spectacles");
                            if (spectaclesInfo.length() == 0) {
                                for (int i = 0; i < spectaclesInfo.length(); i++) {
                                    JSONObject spectacle = spectaclesInfo.getJSONObject(i);
                                    String info = spectacle.getString("titre") + "\n Catégorie : "
                                            + spectacle.getString("nomCategorie") + "\n Durée : "
                                            + spectacle.getString("duree");
                                    spectaclesList.add(info);
                                    adapter = new ArrayAdapter<String>(DetailsFestivalActivity.this, android.R.layout.simple_list_item_1,
                                            spectaclesList);
                                    listSpectacle.setAdapter(adapter);

                                }
                            }
                        } catch (JSONException e) {
                            Toast.makeText(DetailsFestivalActivity.this,
                                    R.string.data_error,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof ServerError && error.networkResponse != null) {
                            int statusCode = error.networkResponse.statusCode;
                            if (statusCode == 500 || statusCode == 400) {
                                Toast.makeText(DetailsFestivalActivity.this,
                                        R.string.msg_error_delete_fav,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put(Utils.API_KEY_NAME, Utils.apiKeyUser);
                return headers;
            }
        };
        return jsonObjectRequest;
    }
}
