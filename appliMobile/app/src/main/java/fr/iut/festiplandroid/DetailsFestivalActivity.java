package fr.iut.festiplandroid;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import fr.iut.festiplandroid.utils.Utils;

public class DetailsFestivalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Utils.makeCustomActionBar(this);

        Utils.checkConnection(this);

    }
}
