package nu.appteam.safetyapplication2015.main;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import nu.appteam.safetyapplication2015.R;
import nu.appteam.safetyapplication2015.main.util.DataController;

public class SettingsActivity extends ActionBarActivity {

    DataController dc = DataController.getInstance();
    private TextView currentManager;
    private EditText textField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        currentManager = (TextView) findViewById(R.id.lbl_current_manager);
        currentManager.setText(dc.managerEmail);

        textField = (EditText) findViewById(R.id.txt_email_field);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveEmailAddress(View view){
        // Save the description.
        dc.managerEmail = textField.getText().toString();
        currentManager.setText(dc.managerEmail);
    }
}
