package nu.appteam.safetyapplication2015.activity;

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
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import controller.ReportDataController;
import nu.appteam.safetyapplication2015.R;

public class ReportActivity extends ActionBarActivity {

    ReportDataController data;

    // Constructor.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        data = new ReportDataController(this);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        TextView dateText = (TextView) findViewById(R.id.lbl_report_date);
        dateText.setText(data.report.getProperty("Date"));

        ImageView mapImage = (ImageView) findViewById(R.id.img_report_location);
        mapImage.setVisibility(View.GONE);

        // Use the LocationManager class to obtain GPS locations
        LocationManager location_mgr = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener location_listener = new ReportLocationListener();
        location_mgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 10, location_listener);
    }

    // Activates after the camera app closes. Display the captured image on screen.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        ImageView img = (ImageView) findViewById(R.id.img_report_photo);
        img.setImageURI(Uri.parse(data.report.getProperty("Photo URI")));

        TextView txt = (TextView) findViewById(R.id.lbl_report_image);
        txt.setVisibility(View.GONE);

        Button btn = (Button) findViewById(R.id.btn_report_photo);
        btn.setText("Photo added (Tap to change)");
        btn.setTextColor(Color.GREEN);

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.parse(data.report.getProperty("Photo URI")));
        this.sendBroadcast(mediaScanIntent);
    }

    // Create a camera image and save it to the sdcard.
    public void addPhoto(View view){

        // Launch the default camera app.
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        // Save the captured image on the sdcard.
        DateFormat df = new SimpleDateFormat("HHmmssddMMyyyy");
        /*String new_photo_filename = data.report.getProperty("ID") + "_photo.jpg";
        data.report.getProperty("Photo Filename") = new_photo_filename;*/

        //String output_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SAFOBS/Pictures/";

        File photo = new File(data.report.getProperty("Output Path"), data.report.getProperty("Photo Filename"));
        data.report.setProperty("Photo URI", "" + Uri.fromFile(photo) + "");

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse(data.report.getProperty("Photo URI")));
        startActivityForResult(intent, 0);



        //MediaScannerConnection.scanFile(getApplicationContext(), new String[]{output_file.toString()}, null, null);
    }

    // Create the popup-dialog for the observation type.
    public void chooseSituationType(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Specify the dialog options.
        builder.setItems(R.array.situationtype_list,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int index) {

                        // Specify the actions after an item has been pressed.
                        Button btn = (Button) findViewById(R.id.btn_report_situationtype);
                        String[] observationList = getResources().getStringArray(R.array.situationtype_list);
                        String selectedItem = observationList[index];

                        data.report.setProperty("Situation Type", selectedItem);

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
                        Button btn = (Button) findViewById(R.id.btn_report_priority);
                        String[] priorityList = getResources().getStringArray(R.array.priority_list);
                        String selectedItem = priorityList[index];

                        data.report.setProperty("Priority", selectedItem);

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
                        EditText txt = (EditText)v.findViewById(R.id.txt_description_box);

                        data.report.setProperty("Description", txt.getText().toString());

                        Button btn = (Button) findViewById(R.id.btn_report_description);
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

        Intent i = new Intent(Intent.ACTION_SEND_MULTIPLE);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient});

        i.putExtra(Intent.EXTRA_SUBJECT,
                data.report.getProperty("Situation Type") + " Report (" + data.report.getProperty("Date") + ")"
        );

        //i.putExtra(Intent.EXTRA_TEXT, "Situation type: " + situation_type + "\nReport priority: " + priority + "\nDescription (optional): " + description + "\nLocation (Lat/Long): " + latitude + " / " + longitude + "\nhttp://maps.google.com/?ie=UTF8&hq=&ll=" + latitude + "," + longitude + "&z=19");
        i.putExtra(Intent.EXTRA_TEXT, "Test");

        ArrayList<Uri> uris = new ArrayList<Uri>();

        // String directory = "file://" + Environment.getExternalStorageDirectory().toString() + "/SAFOBS/Pictures/";
        //String directory = "file:///mnt/sdcard/SAFOBS/Pictures/";


        uris.add(Uri.parse("file://" + data.report.getProperty("Output Path") + data.report.getProperty("Photo Filename")));
        uris.add(Uri.parse("file://" + data.report.getProperty("Output Path") + data.report.getProperty("Location Filename")));
        //uris.add(Uri.parse(directory + data.report.getProperty("ID") + "_location.jpg"));

        Log.d("URI ARRAY 0", uris.get(0).toString());
        Log.d("URI ARRAY 1", uris.get(1).toString());

        i.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

        /*i.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///mnt/sdcard/" + photo_filename));
        i.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///mnt/sdcard/" + location_filename));*/
        try {
            startActivity(i);
            //startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    // Send the activity_report.
    public void sendReport(View view){

        if(data.checkReport()) {

            data.saveReportDataToDisk();

            Toast.makeText(getApplicationContext(), "Report saved to disk!", Toast.LENGTH_SHORT).show();
            //mailReportTo("robbert@appteam.nu");

        }else{
            Toast.makeText(getApplicationContext(), "The report is not yet complete!", Toast.LENGTH_SHORT).show();
        }
    }

    // ReportLocationListener class. Gets the users current lat and long using GPS and WIFI signals.
    public class ReportLocationListener implements LocationListener {

        TextView txt = (TextView) findViewById(R.id.lbl_report_location);
        ImageView mapImage = (ImageView) findViewById(R.id.img_report_location);

    @Override
    public void onLocationChanged(Location loc) {

        Log.d("location provider", "location changed!");

        data.report.setProperty("Latitude", "" + loc.getLatitude() + "");
        data.report.setProperty("Longitude", "" + loc.getLongitude() + "");

        double lat = Double.parseDouble(data.report.getProperty("Latitude"));
        double lon = Double.parseDouble(data.report.getProperty("Longitude"));

        String google_map_link = "https://maps.googleapis.com/maps/api/staticmap?"
                + "center=" + lat + "," + lon
                + "&zoom=15"
                + "&size=500x350"
                + "&maptype=roadmap"
                + "&scale=5"
                + "&markers=color:red%7C"+ lat + "," + lon;

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
        txt.setVisibility(View.GONE);

        mapImage.setVisibility(View.VISIBLE);
        mapImage.setImageBitmap(bmp);

        //String root = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SAFOBS/Pictures/";
        File myDir = new File(data.report.getProperty("Output Path"));

        File location_img = new File (myDir, data.report.getProperty("Location Filename"));

        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{location_img.toString()}, null, null);

        if (location_img.exists ()) location_img.delete ();
        try {
            FileOutputStream out = new FileOutputStream(location_img);
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

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