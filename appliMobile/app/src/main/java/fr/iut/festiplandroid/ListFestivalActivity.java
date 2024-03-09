package fr.iut.festiplandroid;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import fr.iut.festiplandroid.utils.Utils;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ListFestivalActivity extends AppCompatActivity {

    private ListView listFestival;
    private ArrayAdapter<String> adaptateur;
    private String[] scheduledFestival;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_festival);

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();

        // Set the custom layout & mode
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_custom);
        actionBar.setElevation(0);

        scheduledFestival = getResources().getStringArray(R.array.stub_festivals);

        listFestival = findViewById(R.id.list_festivals);
        adaptateur = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1
                                                         , scheduledFestival);
        listFestival.setAdapter(adaptateur);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_option, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Nothing done for block the way to go back on the main activity
    }
}