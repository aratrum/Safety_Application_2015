package nu.appteam.safetyapplication2015.activity;

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

public class MainActivity extends ActionBarActivity {

    // Constructor.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createApplicationDirectory();
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
            case R.id.action_bar_user:
                return true;
            case R.id.action_bar_settings:
                return true;
            case R.id.action_bar_about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
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

    private void createApplicationDirectory() {

        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File picture_dir = new File(root + "/SAFOBS/Pictures/app//");

        Log.d("Picture path", picture_dir.getPath());

        if (!picture_dir.exists()) {
            try {
                if (picture_dir.mkdirs()) {
                    Log.d("Directory", "Directory is created!");
                } else {
                    Log.d("Directory", "Failed to create directory!");
                }
            } catch (Exception e) {
                Log.d("exception", e.toString());
            }
        }

        File data_dir = new File(root + "/SAFOBS/Saved Reports/app//");

        Log.d("Data path", data_dir.getPath());

        if (!data_dir.exists()) {
            try {
                if (data_dir.mkdirs()) {
                    Log.d("Directory", "Directory is created!");
                } else {
                    Log.d("Directory", "Failed to create directory!");
                }
            } catch (Exception e) {
                Log.d("exception", e.toString());
            }
        }

        MediaScannerConnection.scanFile(this, new String[]{picture_dir.toString()}, null, null);
        MediaScannerConnection.scanFile(this, new String[]{data_dir.toString()}, null, null);

    }
}


