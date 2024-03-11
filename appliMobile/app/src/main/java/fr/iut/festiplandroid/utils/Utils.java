package fr.iut.festiplandroid.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import fr.iut.festiplandroid.R;

/**
 * Utility class containing methods for FestiPlAndroid application.
 */
public class Utils {

    private final static String LOGIN_DEV = "identifiant";
    private final static String PASSWORD_DEV = "password123";
    public static int idUser;
    public static String apiKeyUser = "";
    public static final String API_KEY_NAME = "Moidoumbejleprendfacile";
    public static final String IP_SERVER = "192.168.1.10";


    /**
     * Check if the param given by the user are valid (for the moment they need to be equals to
     * the LOGIN and PASSWORD _DEV) and if they are, connect him.
     *
     * @param id the id given by the user
     * @param password the password given by the user
     * @return true if the information of connection are true, false otherwise
     */
    public static boolean connect(String id, String password) {
        return id.equals(LOGIN_DEV) && password.equals(PASSWORD_DEV);
    }

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
}
