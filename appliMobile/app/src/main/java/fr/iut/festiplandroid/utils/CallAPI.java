package fr.iut.festiplandroid.utils;

import android.content.Context
import android.content.Intent;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

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
                response -> handleResponse(response, activity),
                error -> handleError(error, msgConnection));
    }

    /**
     * Handles a successful API response.
     *
     * @param response The JSONObject response from the API.
     * @param activity The MainActivity instance.
     */
    private static void handleResponse(JSONObject response, MainActivity activity) {
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
    private static void handleError(VolleyError error, TextView msgConnection) {
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
     * @return A JsonObjectRequest object configured for fetching festival data.
     */
    public JsonObjectRequest getRequestAllFestivals(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                this::handleResponse,
                this::handleError) {
            @Override
            public Map<String, String> getHeaders() {
                return getRequestHeaders();
            }
        };
        return jsonObjectRequest;
    }

    /**
     * Handles a successful API response.
     *
     * @param response The JSONObject response from the API.
     */
    private void handleResponse(JSONObject response) {
        Iterator<String> keys = response.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            try {
                int idFestival = Integer.parseInt(festivalObject.getString("idFestival"));
                String title = festivalObject.getString("titre");
                String favoris = festivalObject.getString("favoris");

                allFestivals.put(idFestival, new String[]{title, favoris});
                scheduledFestival.add(title);
            } catch (JSONException e) {
                displayDataError();
            }
        }
        adapter = new CustomAdapter(activity, scheduledFestival);
        listFestival.setAdapter(adapter);
    }

    /**
     * Handles an error response from the API.
     *
     * @param error The VolleyError object representing the error.
     */
    private void handleError(VolleyError error) {
        if (error instanceof ServerError && error.networkResponse != null) {
            int statusCode = error.networkResponse.statusCode;
            if (statusCode == 500) {
                displayDBError();
            }
        }
    }

    /**
     * Constructs and returns the request headers.
     *
     * @return A Map containing the request headers.
     */
    private Map<String, String> getRequestHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put(Utils.API_KEY_NAME, Utils.apiKeyUser);
        return headers;
    }

    /**
     * Displays a toast message for a database error.
     */
    private void displayDBError(ListFestivalActivity activity) {
        Toast.makeText(activity, R.string.db_error, Toast.LENGTH_LONG).show();
    }

    /**
     * Displays a toast message for a data error.
     */
    private void displayDataError(ListFestivalActivity activity) {
        Toast.makeText(activity, R.string.data_error, Toast.LENGTH_LONG).show();
    }

    /**
     * Creates a JsonObjectRequest for adding a festival to favorites.
     *
     * @param url The URL for adding the festival to favorites.
     * @return The JsonObjectRequest for adding the festival to favorites.
     */
    public JsonObjectRequest requestAddFavoriteFestival(String url, Context context) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(context, R.string.msg_success_add_fav,
                                                 Toast.LENGTH_SHORT).show();
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof ServerError && error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;
                        if (statusCode == 500 || statusCode == 400) {
                            Toast.makeText(context,
                                    R.string.msg_error_add_fav,
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
