package fr.iut.festiplandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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

import fr.iut.festiplandroid.utils.CallAPI;
import fr.iut.festiplandroid.utils.Utils;

/**
 * The main activity of the FestiPlAndroid application.
 * Allows users to log in and access festival information.
 */
public class MainActivity extends AppCompatActivity {

    EditText txtId;
    EditText txtPassword;
    TextView msgConnection;
    Button btnConnection;
    
    private final static String URL_API = "http://" + Utils.IP_SERVER + "/authentification/%s/%s";

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        setContentView(R.layout.activity_main);
        txtId = findViewById(R.id.txt_id);
        txtPassword = findViewById(R.id.txt_password);
        msgConnection = findViewById(R.id.txt_connection);
        btnConnection = findViewById(R.id.btn_connect);

        Utils.makeCustomActionBar(this);

        if (!Utils.checkConnection(this)) {
            btnConnection.setEnabled(false);
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
        requestQueue = Utils.getFileRequete(this, requestQueue);
        JsonObjectRequest jsonObjectRequest = CallAPI.callAPI(url, this, msgConnection);
        requestQueue.add(jsonObjectRequest);
    }
}
