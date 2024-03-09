package fr.iut.festiplandroid;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * The main activity of the FestiPlAndroid application.
 * Allows users to log in and access festival information.
 */
public class MainActivity extends AppCompatActivity {

    EditText txtId;
    EditText txtPassword;
    TextView msgConnection;
    Button btnConnection;

    private final static String URL_API = "http://192.168.1.70/SAE-S4-FestiplAndroid/api/"
                                        + "authentification/%s/%s";

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtId = findViewById(R.id.txt_id);
        txtPassword = findViewById(R.id.txt_password);
        msgConnection = findViewById(R.id.txt_connection);
        btnConnection = findViewById(R.id.btn_connect);

        ActionBar actionBar = getSupportActionBar();

        // Set the custom layout & mode
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_custom);
        actionBar.setElevation(0);

        // Check the connection
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || ! networkInfo.isConnected()) {

            btnConnection.setEnabled(false);
            Toast.makeText(this,
                    getResources().getString(R.string.msg_error_connect),
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method called when a user wants to connect.
     *
     * @param view The view that triggered the action (in this case, a button).
     */
    public void connectAction(View view) {
        String id = txtId.getText().toString();
        String password = txtPassword.getText().toString();

        String url = String.format(URL_API, id, password);

        requestQueue = getFileRequete();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        msgConnection.setText(response.toString());
                        Intent intention = new Intent(MainActivity.this,
                                                        ListFestivalActivity.class);
                        startActivity(intention);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof ServerError && error.networkResponse != null) {
                            int statusCode = error.networkResponse.statusCode;
                            if (statusCode == 400) {
                                msgConnection.setText(R.string.connect_error);
                            }
                        }
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Retrieves the Volley request queue. If the request queue is not already created,
     * it initializes it.
     *
     * @return The Volley request queue.
     */
    private RequestQueue getFileRequete() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }
        return requestQueue;
    }
}
