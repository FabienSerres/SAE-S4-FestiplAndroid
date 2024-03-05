package fr.iut.festiplandroid;

import androidx.appcompat.app.AppCompatActivity;
import fr.iut.festiplandroid.utils.Utils;

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
            msgConnection.setText(R.string.connect_successful);
        } else {
            msgConnection.setText(R.string.connect_error);
        }
    }
}