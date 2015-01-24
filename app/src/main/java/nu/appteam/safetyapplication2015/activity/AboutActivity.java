package nu.appteam.safetyapplication2015.activity;

import android.app.Activity;
import android.os.Bundle;

import nu.appteam.safetyapplication2015.R;

public class AboutActivity extends Activity {

    // Constructor.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
}