package nu.appteam.safetyapplication2015.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import nu.appteam.safetyapplication2015.R;

public class ReportActivity extends ActionBarActivity {

    // Members.
    private Date date;
    private DateFormat df;
    private String photo_filename;
    private Uri photo_URI;
    private String situation_type;
    private String priority;
    private String description;
    private double latitude;
    private double longitude;
    private int zoom = 19;

    // Constructor.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        date = new Date();
        df = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");

        TextView dateText = (TextView) findViewById(R.id.reportdatetext);
        dateText.setText(df.format(date));

        ImageView mapImage = (ImageView) findViewById(R.id.mapImageView);
        mapImage.setVisibility(View.GONE);

        // Use the LocationManager class to obtain GPS locations
        LocationManager location_mgr = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener location_listener = new ReportLocationListener();
        location_mgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 10, location_listener);
        Log.d("LOCATIONMANAGER", location_mgr.toString());
        Log.d("LOCATIONLISTENER", location_listener.toString());
        Log.d("Location Provider status", Context.LOCATION_SERVICE);
    }

    // Activates after the camera app closes. Display the captured image on screen.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        ImageView img = (ImageView) findViewById(R.id.imageView1);
        img.setImageURI(photo_URI);

        TextView txt = (TextView) findViewById(R.id.imageText1);
        txt.setVisibility(View.GONE);

        Button btn = (Button) findViewById(R.id.reportbutton);
        btn.setText("Photo added (Tap to change)");
        btn.setTextColor(Color.GREEN);
    }

    // Create a camera image and save it to the sdcard.
    public void addPhoto(View view){

        // Launch the default camera app.
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        // Save the captured image on the sdcard.
        DateFormat df = new SimpleDateFormat("HHmmssddMMyyyy");
        String new_photo_filename = df.format(date) + ".jpg";
        photo_filename = new_photo_filename;
        String output_path = "/sdcard/" + new_photo_filename;

        File output_file = new File(output_path);
        photo_URI = Uri.fromFile(output_file);

        // Start the action.
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photo_URI);
        startActivityForResult(intent, 0);
    }

    // Create the popup-dialog for the observation type.
    public void chooseSituationType(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Specify the dialog options.
        builder.setItems(R.array.observation_list,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int index) {

                        // Specify the actions after an item has been pressed.
                        Button btn = (Button) findViewById(R.id.observationbutton);
                        String[] observationList = getResources().getStringArray(R.array.observation_list);
                        String selectedItem = observationList[index];

                        situation_type = selectedItem;

                        Toast toast = Toast.makeText(getApplicationContext(), (selectedItem + " selected."), Toast.LENGTH_SHORT);
                        btn.setText(selectedItem);
                        btn.setTextColor(Color.GREEN);

                        toast.show();
                    }
                });

        // Show the dialog.
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Create the popup-dialog for the observation type.
    public void choosePriority(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Specify the dialog options.
        builder.setItems(R.array.priority_list,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int index) {

                        // Specify the actions after an item has been pressed.
                        Button btn = (Button) findViewById(R.id.prioritybutton);
                        String[] priorityList = getResources().getStringArray(R.array.priority_list);
                        String selectedItem = priorityList[index];

                        priority = selectedItem;

                        Toast toast = Toast.makeText(getApplicationContext(), (selectedItem + " selected."), Toast.LENGTH_SHORT);
                        btn.setText(selectedItem + " priority");
                        btn.setTextColor(Color.GREEN);
                        toast.show();
                    }
                });

        // Show the dialog.
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Create the popup for the activity_report description.
    public void setDescription(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        final View v = inflater.inflate(R.layout.alertdialog_reportdescription, null);

        // Specify the dialog options.
        builder.setView(v)
                .setTitle("Add a description ...")
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Save the description.
                        EditText txt = (EditText)v.findViewById(R.id.descriptiontextbox);
                        description = txt.getText().toString();

                        Button btn = (Button) findViewById(R.id.descriptionbutton);
                        btn.setText("Description added (Tap to change)");
                        btn.setTextColor(Color.GREEN);

                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        // Show the dialog.
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Mail activity_report data to a given email adress.
    private void mailReportTo(String recipient){

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient});

        i.putExtra(Intent.EXTRA_SUBJECT,
                situation_type + " Report (" + df.format(date) + ")"
        );

        i.putExtra(Intent.EXTRA_TEXT,(
                "Situation type: " + situation_type +
                "\nReport priority: " + priority +
                "\nDescription (optional): " + description) +
                "\nLocation (Lat/Long): " + latitude + " / " + longitude +
                "\nhttp://maps.google.com/?ie=UTF8&hq=&ll=" + latitude + "," + longitude + "&z=" + zoom
        );

        i.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///mnt/sdcard/" + photo_filename));
        try {
            startActivity(i);
            //startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    // Send the activity_report.
    public void sendReport(View view){
        mailReportTo("robbert@appteam.nu");
    }

    // ReportLocationListener class. Gets the users current lat and long using GPS and WIFI signals.
    public class ReportLocationListener implements LocationListener {

        TextView txt = (TextView) findViewById(R.id.reportgpstext);
        ImageView mapImage = (ImageView) findViewById(R.id.mapImageView);

    @Override
    public void onLocationChanged(Location loc) {

        Log.d("location provider", "location changed!");

        latitude = loc.getLatitude();
        longitude = loc.getLongitude();
        String location = "Lat = " + latitude + "\nLong = " + longitude;

        String google_map_link = "https://maps.googleapis.com/maps/api/staticmap?center=" +
                latitude + "," + longitude + "&zoom=19&size=500x350&maptype=hybrid&scale=1" +
                "&markers=color:red%7C"+ latitude + "," + longitude;

        Bitmap bmp = null;
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet request = new HttpGet(google_map_link);

        InputStream in = null;
        try {
            in = httpclient.execute(request).getEntity().getContent();
            bmp = BitmapFactory.decodeStream(in);
            in.close();
        } catch (IllegalStateException e) {
            Log.d("EXCEPTION", "Illegal state exception caught!");
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            Log.d("EXCEPTION", "Client protocol exception caught!");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("EXCEPTION", "IO exception caught!");
            e.printStackTrace();
        }

        // Display location
        txt.setText(location);
        txt.setVisibility(View.GONE);

        mapImage.setVisibility(View.VISIBLE);
        mapImage.setImageBitmap(bmp);

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("location provider", "disabled!");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("location provider", "enabled!");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("location provider", "status changed!");
    }

}
}