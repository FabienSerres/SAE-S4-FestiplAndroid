package fr.iut.festiplandroid.utils;

import static fr.iut.festiplandroid.ListFestivalActivity.favoritesFestivalList;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

    private void processFestivalObject(JSONObject festivalObject, Map<Integer, String[]> allFestivals,
                                       ArrayList<String> scheduledFestival) throws JSONException {
        int idFestival = Integer.parseInt(festivalObject.getString("idFestival"));
        String titre = festivalObject.getString("titre");
        String favoris = festivalObject.getString("favoris");

        allFestivals.put(idFestival, new String[]{titre, favoris});
        scheduledFestival.add(titre);
    }

    private void handleError500(Context context, VolleyError error) {
        if (error instanceof ServerError && error.networkResponse != null) {
            int statusCode = error.networkResponse.statusCode;
            if (statusCode == 500) {
                showMessage(context, context.getString(R.string.db_error));
            }
        }
    }

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

    private Map<String, String> getRequestHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put(Utils.API_KEY_NAME, Utils.apiKeyUser);
        return headers;
    }

    private void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }


}
