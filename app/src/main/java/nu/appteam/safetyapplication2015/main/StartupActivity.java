package nu.appteam.safetyapplication2015.main;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import nu.appteam.safetyapplication2015.R;
import nu.appteam.safetyapplication2015.main.util.LocationService;

public class StartupActivity extends ActionBarActivity {

    private Thread startupThread;
    private int startupDelayMS = 5000;

    private TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        final StartupActivity startupActivity = this;

        // Create the startup thread
        startupThread =  new Thread(){

            @Override
            public void run(){
                try {
                    synchronized(this){

                        // Wait for the set startup time before launching the MainMenuActivity
                        wait(startupDelayMS);
                        Intent intent = new Intent(startupActivity, MainMenuActivity.class);
                        startActivity(intent);

                        // Close the StartupActivity
                        startupActivity.finish();
                    }
                } catch(InterruptedException ex){

                    Log.d("EXCEPTION", "InterruptedException caught!");
                }
            }
        };

        // Start the LocationService
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);

        // Create the application directories on the SD Card
        createApplicationDirectory();

        startupThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent evt)
    {
        // Skip the startup if the user taps the screen
        if(evt.getAction() == MotionEvent.ACTION_DOWN)
        {
            synchronized(startupThread){

                // StartupThread is cancelled
                startupThread.notifyAll();
            }
        }
        return true;
    }

    private void createApplicationDirectory() {

        String pictureDirectory = "/SAFOBS/Pictures/app//";
        String reportDirectory = "/SAFOBS/Saved Reports/app//";

        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File picture_dir = new File(root + pictureDirectory);

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

        File data_dir = new File(root + reportDirectory);

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
