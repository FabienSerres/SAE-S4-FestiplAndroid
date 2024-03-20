package fr.iut.festiplandroid.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import fr.iut.festiplandroid.ListFestivalActivity;
import fr.iut.festiplandroid.R;

/**
 * Utility class containing methods for FestiPlAndroid application.
 */
public class Utils {
    public static int idUser;
    public static String apiKeyUser = "";
    public static final String API_KEY_NAME = "Moidoumbejleprendfacile";
    public static final String IP_SERVER = "10.108.0.103";
    public final static String URL_API_ALL_FESTIVALS = "http://" + Utils.IP_SERVER
                                                     + "/SAE-S4-FestiplAndroid/api/getAllFestivals/%d";
    public final static String URL_API_FAV_FESTIVALS = "http://" + Utils.IP_SERVER
                                                     + "/SAE-S4-FestiplAndroid/api/getFavoriteFestivals/%d";
    public final static String URL_API_ADD_FAV = "http://" + Utils.IP_SERVER
                                               + "/SAE-S4-FestiplAndroid/api/addFavoriteFestival/%d/%d";
    public final static String URL_API_DEL_FAV = "http://" + Utils.IP_SERVER
                                               + "/SAE-S4-FestiplAndroid/api/deleteFavoriteFestival/%d/%d";

    /**
     * Checks if the device is connected to the internet.
     *
     * @param context The context of the application.
     * @return True if the device is connected to the internet, false otherwise.
     */
    public static boolean checkConnection(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || ! networkInfo.isConnected()) {
            Toast.makeText(context,
                    context.getResources().getString(R.string.msg_error_connect),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
     * Sets up a custom action bar for the provided AppCompatActivity.
     *
     * @param app The AppCompatActivity for which the custom action bar will be set up.
     */
    public static void makeCustomActionBar(AppCompatActivity app) {
        ActionBar actionBar = app.getSupportActionBar();

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_custom);
        actionBar.setElevation(0);
    }
    /**
     * Retrieves the Volley request queue. If the request queue is not already created,
     * it initializes it.
     *
     * @return The Volley request queue.
     */
    public static RequestQueue getFileRequete(Context context, RequestQueue requestQueue) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

    /**
     * Retrieves the ID of the festival based on its position.
     *
     * @param position The position of the festival in the list.
     * @return The ID of the festival if found, otherwise -1.
     */
    public static int getFestivalId(int position) {
        if (position >= 0 && position < ListFestivalActivity.allFestivals.size()) {
            Set<Integer> keys = ListFestivalActivity.allFestivals.keySet();
            List<Integer> keyList = new ArrayList<>(keys);
            if (position < keyList.size()) {
                int festivalId = keyList.get(position);
                return festivalId;
            }
        }
        return -1;
    }

}