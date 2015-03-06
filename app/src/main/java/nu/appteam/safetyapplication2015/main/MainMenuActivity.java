package nu.appteam.safetyapplication2015.main;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.io.File;

import nu.appteam.safetyapplication2015.R;
import nu.appteam.safetyapplication2015.main.util.LocationService;

public class MainMenuActivity extends ActionBarActivity {

    // Constructor.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // Create the action bar menu.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Do something when an item has been selected in the action bar menu.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_bar_settings:

                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;

            case R.id.action_bar_about:

                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;

            default:

                return super.onOptionsItemSelected(item);
        }
    }

    // Do something when the activity_report button has been pressed.
    public void startReportActivity(View view){

        Intent intent = new Intent(this, ReportActivity.class);
        startActivity(intent);
    }
}


