package fr.iut.festiplandroid;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import fr.iut.festiplandroid.utils.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * The main activity of the FestiPlAndroid application.
 * Allows users to log in and access festival information.
 */
public class MainActivity extends AppCompatActivity {

    EditText txtId;
    EditText txtPassword;
    TextView msgConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtId = findViewById(R.id.txt_id);
        txtPassword = findViewById(R.id.txt_password);
        msgConnection = findViewById(R.id.txt_connection);

        ActionBar actionBar = getSupportActionBar();

        // Set the custom layout & mode
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_custom);
        actionBar.setElevation(0);
    }

    /**
     * Method called when a user wants to connect.
     *
     * @param view The view that triggered the action (in this case, a button).
     */
    public void connectAction(View view) {
        String id = txtId.getText().toString();
        String password = txtPassword.getText().toString();

        boolean connected = Utils.connect(id, password);

        if (connected) {
            Intent intention = new Intent(MainActivity.this, ListFestivalActivity.class);
            startActivity(intention);
        } else {
            msgConnection.setText(R.string.connect_error);
        }
    }
}