package fr.iut.festiplandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import fr.iut.festiplandroid.utils.CallAPI;
import fr.iut.festiplandroid.utils.CustomAdapter;
import fr.iut.festiplandroid.utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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
        favoritesFestivals.clear();
        favoritesFestivalList.clear();

        String url = String.format(Utils.URL_API_ALL_FESTIVALS, Utils.idUser);

        requestQueue = Utils.getFileRequete(this, requestQueue);
        CallAPI ca = new CallAPI();
        JsonObjectRequest jsonObjectRequest = ca.getRequestAllFestivals(url, this, allFestivals, 
                                                                         scheduledFestival, listFestival);

        requestQueue.add(jsonObjectRequest);
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


    /**
     * Displays the list of favorite festivals in the UI.
     * This method updates the UI to show the list of festivals marked as favorites.
     * It clears the existing favorites list, iterates through all festivals in the
     * 'allFestivals' HashMap, and adds the title of festivals with 'favoris' set to "true"
     * to the favorites list. Finally, it updates the adapter and sets it to the ListView.
     */
    public void displayListFavorites() {
        tv.setText(R.string.option_favorites);

        favoritesFestivals.clear();
        favoritesFestivalList.clear();
        String url = String.format(Utils.URL_API_FAV_FESTIVALS, Utils.idUser);

        CallAPI callApi = new CallAPI();
        JsonArrayRequest jsonArrayRequest = 
                callApi.getRequestFavoritesFestivals(url, this, favoritesFestivals, 
                                                     favoritesFestivalList, listFestival, adapter);

        requestQueue.add(jsonArrayRequest);
    }
}