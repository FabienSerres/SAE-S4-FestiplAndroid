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
import android.util.Log;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This activity displays a list of festivals.
 * It retrieves festival information from the server and populates a ListView with the festival titles.
 * Users can navigate between the list of all festivals and their favorite festivals using the options menu.
 */
public class ListFestivalActivity extends AppCompatActivity {

    private ListView listFestival;
    private CustomAdapter adapter;
    public static ArrayList<String> scheduledFestival = new ArrayList<>();
    public static ArrayList<String> favoritesFestivalList = new ArrayList<>();
    private TextView tv;
    public static HashMap<Integer, String[]> allFestivals = new HashMap<>();
    public static HashMap<Integer, String> favoritesFestivals = new HashMap<>();

    private RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_festival);
        tv = findViewById(R.id.page_title);
        listFestival = findViewById(R.id.list_festivals);

        Utils.makeCustomActionBar(this);

        Utils.checkConnection(this);

        displayAllFestivals();
    }

    /**
     * Displays all festivals in the UI.
     * This method retrieves information about all festivals from the server and updates the UI
     * to display the list of festivals.
     */
    private void displayAllFestivals() {
        tv.setText(R.string.title_festivals);
        allFestivals.clear();
        scheduledFestival.clear();

        String url = String.format(Utils.URL_API_ALL_FESTIVALS, Utils.idUser);

        requestQueue = Utils.getFileRequete(this, requestQueue);
        JsonObjectRequest jsonObjectRequest = getRequestAllFestivals(url);

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
    private JsonObjectRequest getRequestAllFestivals(String url) {
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
        if (item.getItemId() == R.id.list_favorites) {
            displayListFavorites();
        } else if (item.getItemId() == R.id.list_festivals) {
            displayAllFestivals();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Nothing done for block the way to go back on the main activity
    }

    /**
     * Displays the list of favorite festivals in the UI.
     * This method updates the UI to show the list of festivals marked as favorites.
     * It clears the existing favorites list, iterates through all festivals in the
     * 'allFestivals' HashMap, and adds the title of festivals with 'favoris' set to "true"
     * to the favorites list. Finally, it updates the adapter and sets it to the ListView.
     */
    private void displayListFavorites() {
        tv.setText(R.string.option_favorites);

        favoritesFestivals.clear();
        favoritesFestivalList.clear();
        String url = String.format(Utils.URL_API_FAV_FESTIVALS, Utils.idUser);

        JsonArrayRequest jsonArrayRequest = getRequestFavoritesFestivals(url);

        requestQueue.add(jsonArrayRequest);
    }

    /**
     * Creates a JsonArrayRequest for retrieving information about favorite festivals.
     *
     * @param url The URL for retrieving favorite festival information.
     * @return The JsonArrayRequest for fetching favorite festival data.
     */
    private JsonArrayRequest getRequestFavoritesFestivals(String url) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject objectCurrent = response.getJSONObject(i);
                                favoritesFestivals.put(objectCurrent.getInt("idFestival"),
                                        objectCurrent.getString("titre"));
                                favoritesFestivalList.add(objectCurrent.getString("titre"));
                            }

                            adapter = new CustomAdapter(ListFestivalActivity.this, favoritesFestivalList);
                            listFestival.setAdapter(adapter);
                        } catch (JSONException e) {
                            Toast.makeText(ListFestivalActivity.this,
                                    getResources().getString(R.string.data_error),
                                    Toast.LENGTH_LONG).show();
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
        return jsonArrayRequest;
    }
}