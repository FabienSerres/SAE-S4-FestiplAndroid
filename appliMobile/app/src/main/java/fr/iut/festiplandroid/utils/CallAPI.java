package fr.iut.festiplandroid.utils;

import static fr.iut.festiplandroid.ListFestivalActivity.favoritesFestivalList;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fr.iut.festiplandroid.DetailsFestivalActivity;
import fr.iut.festiplandroid.ListFestivalActivity;
import fr.iut.festiplandroid.MainActivity;
import fr.iut.festiplandroid.R;

/**
 * Utility class for making API calls.
 */
public class CallAPI {

    /**
     * Makes an API call.
     *
     * @param url             The URL of the API.
     * @param activity        The MainActivity instance.
     * @param msgConnection   The TextView to display connection messages.
     * @return A JsonObjectRequest object representing the API call.
     */
    public static JsonObjectRequest callAPI(String url, MainActivity activity, TextView msgConnection) {
        return new JsonObjectRequest(Request.Method.GET, url, null,
                response -> handleResponseAuth(response, activity),
                error -> handleErrorAuth(error, msgConnection));
    }

    /**
     * Handles a successful API response.
     *
     * @param response The JSONObject response from the API.
     * @param activity The MainActivity instance.
     */
    private static void handleResponseAuth(JSONObject response, MainActivity activity) {
        String responseString = response.toString();
        String apiKey = responseString.split(":")[1];
        responseString = responseString.split(":")[2];
        int idUser = Integer.parseInt(responseString.substring(0, responseString.length() - 1));
        apiKey = apiKey.substring(1, apiKey.length() - 6);

        Utils.apiKeyUser = apiKey;
        Utils.idUser = idUser;

        Intent intention = new Intent(activity, ListFestivalActivity.class);
        activity.startActivity(intention);
    }

    /**
     * Handles an error response from the API.
     *
     * @param error         The VolleyError object representing the error.
     * @param msgConnection The TextView to display connection messages.
     */
    private static void handleErrorAuth(VolleyError error, TextView msgConnection) {
        Log.d("ERROR", error.toString());
        if (error instanceof ServerError && error.networkResponse != null) {
            int statusCode = error.networkResponse.statusCode;
            if (statusCode == 400) {
                msgConnection.setText(R.string.connect_error);
            } else if (statusCode == 500) {
                msgConnection.setText(R.string.db_error);
            }
        }
    }

    /**
     * Creates a JsonObjectRequest with the given URL for retrieving festival information.
     *
     * This method constructs a JsonObjectRequest object specifically tailored for retrieving
     * information about festivals from the provided URL. It's designed to handle GET requests
     * and includes listeners to process successful responses and handle errors.
     *
     * @param url The URL for retrieving festival information.
     * @param context The context of the calling activity.
     * @param allFestivals The map to store all festivals.
     * @param scheduledFestival The list to store scheduled festivals.
     * @param listFestival The ListView to update with festival data.
     * @return A JsonObjectRequest object configured for fetching festival data.
     */
    public JsonObjectRequest getRequestAllFestivals(String url, Context context, Map<Integer, String[]> allFestivals,
                                                     ArrayList<String> scheduledFestival, ListView listFestival) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> handleResponseAllFestivals(response, context, allFestivals, scheduledFestival, listFestival),
                error -> handleError500(context, error)) {
            @Override
            public Map<String, String> getHeaders() {
                return getRequestHeaders();
            }
        };
        return jsonObjectRequest;
    }

    /**
     * Handles the response containing all festival data received from the server.
     *
     * @param response          The JSON object containing the response data.
     * @param context           The context of the application.
     * @param allFestivals      A map to store all festivals with their corresponding details.
     * @param scheduledFestival An ArrayList to hold the titles of scheduled festivals.
     * @param listFestival      The ListView to which the festival data will be adapted.
     */
    private void handleResponseAllFestivals(JSONObject response, Context context, Map<Integer, String[]> allFestivals,
                                ArrayList<String> scheduledFestival, ListView listFestival) {
        try {
            Iterator<String> keys = response.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject objectCurrent = response.getJSONObject(key);
                processFestivalObject(objectCurrent, allFestivals, scheduledFestival);
            }
        } catch (JSONException e) {
            showMessage(context, context.getString(R.string.data_error));
        }

        updateAdapter(context, scheduledFestival, listFestival);
    }

    /**
     * Processes the JSON object representing a festival and adds its details to the provided maps and lists.
     *
     * @param festivalObject    The JSON object representing the festival.
     * @param allFestivals      A map to store all festivals with their corresponding details.
     * @param scheduledFestival An ArrayList to hold the titles of scheduled festivals.
     * @throws JSONException if there is an error parsing the JSON data.
     */
    private void processFestivalObject(JSONObject festivalObject, Map<Integer, String[]> allFestivals,
                                       ArrayList<String> scheduledFestival) throws JSONException {
        int idFestival = Integer.parseInt(festivalObject.getString("idFestival"));
        String titre = festivalObject.getString("titre");
        String favoris = festivalObject.getString("favoris");

        allFestivals.put(idFestival, new String[]{titre, favoris});
        scheduledFestival.add(titre);
    }

    /**
     * Handles an HTTP 500 error response from the server.
     *
     * @param context The context of the application.
     * @param error   The VolleyError instance representing the error response.
     */
    private void handleError500(Context context, VolleyError error) {
        if (error instanceof ServerError && error.networkResponse != null) {
            int statusCode = error.networkResponse.statusCode;
            if (statusCode == 500) {
                showMessage(context, context.getString(R.string.db_error));
            }
        }
    }

    /**
     * Updates the ListView adapter with the latest festival data.
     *
     * @param context           The context of the application.
     * @param scheduledFestival An ArrayList containing the titles of scheduled festivals.
     * @param listFestival      The ListView to be updated.
     */
    private void updateAdapter(Context context, ArrayList<String> scheduledFestival, ListView listFestival) {
        CustomAdapter adapter = new CustomAdapter(context, scheduledFestival);
        listFestival.setAdapter(adapter);
    }

    /**
     * Creates a JsonArrayRequest for retrieving information about favorite festivals.
     *
     * @param url The URL for retrieving favorite festival information.
     * @return The JsonArrayRequest for fetching favorite festival data.
     */
    public JsonArrayRequest getRequestFavoritesFestivals(String url, Context context, HashMap<Integer,
            String> favoritesFestivals, ArrayList<String> favoritesFestivalsList, ListView listFestival,
                                                         CustomAdapter adapter) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> handleResponseGetFav(response, context, favoritesFestivals, favoritesFestivalsList, listFestival, adapter),
                error -> handleError500(context, error)) {
            @Override
            public Map<String, String> getHeaders() {
                return getRequestHeaders();
            }
        };
        return jsonArrayRequest;
    }


    /**
     * Handles the response containing favorite festivals received from the server.
     *
     * @param response              The JSON array containing the favorite festival data.
     * @param context               The context of the application.
     * @param favoritesFestivals    A map to store favorite festivals with their corresponding IDs and titles.
     * @param favoritesFestivalsList An ArrayList to hold the titles of favorite festivals.
     * @param listFestival          The ListView to which the favorite festival data will be adapted.
     * @param adapter               The CustomAdapter used for adapting the data to the list.
     */
    private void handleResponseGetFav(JSONArray response, Context context, HashMap<Integer,
            String> favoritesFestivals, ArrayList<String> favoritesFestivalsList, ListView listFestival,
                                      CustomAdapter adapter) {
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject objectCurrent = response.getJSONObject(i);
                favoritesFestivals.put(objectCurrent.getInt("idFestival"),
                        objectCurrent.getString("titre"));
                favoritesFestivalList.add(objectCurrent.getString("titre"));
            }

            adapter = new CustomAdapter(context, favoritesFestivalList);
            listFestival.setAdapter(adapter);
        } catch (JSONException e) {
            Toast.makeText(context, context.getString(R.string.data_error),Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Constructs a JsonObjectRequest to fetch details of a festival from the server.
     *
     * @param url            The URL to send the request.
     * @param context        The context of the application.
     * @param adapter        The ListAdapter to adapt the data to the ListView.
     * @param listSpectacle  The ListView to display the festival details.
     * @param spectaclesList An ArrayList to hold details of spectacles in the festival.
     * @param title          The TextView to display the festival title.
     * @param categorie      The TextView to display the festival category.
     * @param description_text The TextView to display the festival description.
     * @param date_text      The TextView to display the festival dates.
     * @return A JsonObjectRequest instance.
     */
    public JsonObjectRequest requestDetailsFestival(String url, Context context, ListAdapter adapter,
                                                            ListView listSpectacle, ArrayList<String> spectaclesList,
                                                            TextView title, TextView categorie, TextView description_text,
                                                            TextView date_text) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> handleResponseDetailsFestival(response, context, adapter, listSpectacle,
                                                         spectaclesList, title, categorie, description_text,
                                                         date_text),
                error -> handleError500400(context, error)) {
            @Override
            public Map<String, String> getHeaders() {
                return getRequestHeaders();
            }
        };
        return jsonObjectRequest;
    }

    /**
     * Handles an HTTP 500 or 400 error response from the server.
     *
     * @param context The context of the application.
     * @param error   The VolleyError instance representing the error response.
     */
    private void handleError500400(Context context, VolleyError error) {
        if (error instanceof ServerError && error.networkResponse != null) {
            int statusCode = error.networkResponse.statusCode;
            if (statusCode == 500 || statusCode == 400) {
                Toast.makeText(context, R.string.data_error, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Handles the response containing details of a festival received from the server.
     *
     * @param response          The JSON object containing the festival details.
     * @param context           The context of the application.
     * @param adapter           The ListAdapter to adapt the data to the ListView.
     * @param listSpectacle     The ListView to display details of spectacles in the festival.
     * @param spectaclesList    An ArrayList to hold details of spectacles in the festival.
     * @param title             The TextView to display the festival title.
     * @param categorie         The TextView to display the festival category.
     * @param description_text  The TextView to display the festival description.
     * @param date_text         The TextView to display the festival dates.
     */
    private void handleResponseDetailsFestival(JSONObject response, Context context, ListAdapter adapter,
                                               ListView listSpectacle, ArrayList<String> spectaclesList,
                                               TextView title, TextView categorie, TextView description_text,
                                               TextView date_text) {
        try {
            JSONObject festivalInfo = response.getJSONObject("festival");
            title.setText(festivalInfo.getString("titre"));
            categorie.setText(categorie.getText() + festivalInfo.getString("nom"));
            description_text.setText(festivalInfo.getString("description"));
            date_text.setText(festivalInfo.getString("dateDebut") + " - "
                    + festivalInfo.getString("dateFin"));

            JSONArray spectaclesInfo = response.getJSONArray("spectacles");
            if (spectaclesInfo.length() != 0) {
                for (int i = 0; i < spectaclesInfo.length(); i++) {
                    JSONObject spectacle = spectaclesInfo.getJSONObject(i);
                    String info = spectacle.getString("titre") + "\n Catégorie : "
                            + spectacle.getString("nomCategorie") + "\n Durée : "
                            + spectacle.getString("duree");
                    spectaclesList.add(info);
                    adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,
                            spectaclesList);
                    listSpectacle.setAdapter(adapter);

                }
            }
        } catch (JSONException e) {
            Toast.makeText(context,R.string.msg_any_spectacle, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Constructs and returns request headers.
     *
     * @return A Map containing request headers.
     */
    private Map<String, String> getRequestHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put(Utils.API_KEY_NAME, Utils.apiKeyUser);
        return headers;
    }

    /**
     * Displays a message to the user.
     *
     * @param context The context of the application.
     * @param message The message to display.
     */
    private void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
