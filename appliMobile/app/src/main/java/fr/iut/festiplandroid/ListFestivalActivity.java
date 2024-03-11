package fr.iut.festiplandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import fr.iut.festiplandroid.utils.CustomAdapter;
import fr.iut.festiplandroid.utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ListFestivalActivity extends AppCompatActivity {

    private ListView listFestival;
    private CustomAdapter adapter;
    private ArrayList<String> scheduledFestival = new ArrayList<>();
    private TextView tv;
    public static HashMap<Integer, String[]> allFestivals = new HashMap<>();

    private final static String URL_API = "http://" + Utils.IP_SERVER
                                        + "/SAE-S4-FestiplAndroid/api/getAllFestivals/%d";

    private RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_festival);
        tv = findViewById(R.id.page_title);
        listFestival = findViewById(R.id.list_festivals);

        Utils.makeCustomActionBar(this);

        Utils.checkConnection(this);

        String url = String.format(URL_API, Utils.idUser);

        requestQueue = Utils.getFileRequete(this, requestQueue);
        JsonObjectRequest jsonObjectRequest = getRequest(url);

        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Creates a JsonObjectRequest with the given URL for retrieving festival information.
     *
     * This method constructs a JsonObjectRequest object specifically tailored for retrieving
     * information about festivals from the provided URL. It's designed to handle GET requests
     * and includes listeners to process successful responses and handle errors.
     *
     * @param url The URL for retrieving festival information.
     * @return A JsonObjectRequest object configured for fetching festival data.
     */
    private JsonObjectRequest getRequest(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Iterator<String> keys = response.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            try {
                                JSONObject objectCurrent = response.getJSONObject(key);
                                allFestivals.put(Integer.parseInt(objectCurrent.get("idFestival")
                                                        .toString()), new String[]{
                                                                objectCurrent.get("titre").toString(),
                                                                objectCurrent.get("favoris").toString()});

                                scheduledFestival.add(objectCurrent.get("titre").toString());
                            } catch (JSONException e) {
                                Toast.makeText(ListFestivalActivity.this,
                                        getResources().getString(R.string.data_error),
                                        Toast.LENGTH_LONG).show();
                            }

                            adapter = new CustomAdapter(ListFestivalActivity.this,
                                                               scheduledFestival);
                            listFestival.setAdapter(adapter);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof ServerError && error.networkResponse != null) {
                            int statusCode = error.networkResponse.statusCode;
                            if (statusCode == 500) {
                                Toast.makeText(ListFestivalActivity.this,
                                        getResources().getString(R.string.db_error),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put(Utils.API_KEY_NAME, Utils.apiKeyUser);
                return headers;
            }
        };
        return jsonObjectRequest;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_option, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Nothing done for block the way to go back on the main activity
    }
}