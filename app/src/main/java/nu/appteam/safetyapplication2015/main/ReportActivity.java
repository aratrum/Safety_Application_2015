package nu.appteam.safetyapplication2015.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import nu.appteam.safetyapplication2015.main.util.DataController;
import nu.appteam.safetyapplication2015.R;

public class ReportActivity extends ActionBarActivity {

    //ReportDataController data;
    DataController dc = DataController.getInstance();

    // Constructor.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Start the location Service
        /**
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
         */

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        TextView dateText = (TextView) findViewById(R.id.lbl_report_date);
        dateText.setText(dc.date);

        /**
        ImageView mapImage = (ImageView) findViewById(R.id.img_report_location);
        mapImage.setVisibility(View.GONE);
         */

        // Get the location image.
        /**
        saveLocationBitmap();
        */

    }

    // Activates after the camera app closes. Display the captured image on screen.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        ImageView img = (ImageView) findViewById(R.id.img_report_photo);
        img.setImageURI(Uri.parse(dc.photoURI));

        TextView txt = (TextView) findViewById(R.id.lbl_report_image);
        txt.setVisibility(View.GONE);

        Button btn = (Button) findViewById(R.id.btn_report_photo);
        btn.setText("Photo added (Tap to change)");
        btn.setTextColor(Color.argb(255, 10, 200, 10));

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.parse(dc.photoURI));
        this.sendBroadcast(mediaScanIntent);
    }

    // Create a camera image and save it to the sdcard.
    public void addPhoto(View view){

        // Launch the default camera app.
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        // Save the captured image on the sdcard.
        DateFormat df = new SimpleDateFormat("HHmmssddMMyyyy");

        File photo = new File(dc.outputPath, dc.photoFilename);
        dc.photoURI = "" + Uri.fromFile(photo) + "";

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse(dc.photoURI));
        startActivityForResult(intent, 0);
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

                        dc.situationType = selectedItem;

                        Toast toast = Toast.makeText(getApplicationContext(), (selectedItem + " selected."), Toast.LENGTH_SHORT);
                        btn.setText(selectedItem);
                        btn.setTextColor(Color.argb(255, 10, 200, 10));

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

                        dc.priority = selectedItem;

                        Toast toast = Toast.makeText(getApplicationContext(), (selectedItem + " selected."), Toast.LENGTH_SHORT);
                        btn.setText(selectedItem + " priority");
                        btn.setTextColor(Color.argb(255, 10, 200, 10));
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
                .setPositiveButton("Save Description", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Save the description.
                        EditText txt = (EditText)v.findViewById(R.id.txt_description_box);

                        dc.description = txt.getText().toString();

                        Button btn = (Button) findViewById(R.id.btn_report_description);
                        btn.setText("Description added (Tap to change)");
                        btn.setTextColor(Color.argb(255, 10, 200, 10));

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
                dc.situationType + " Report (" + dc.date + ")"
        );

        i.putExtra(Intent.EXTRA_TEXT,
                "Situation type: " + dc.situationType +
                        "\nReport priority: " + dc.priority +
                        "\nDescription (optional): " + dc.description +
                        "\nLocation (Lat/Long): " + dc.latitude +
                        " / " + dc.longitude +
                        "\nhttp://maps.google.com/?ie=UTF8&hq=&ll=" + dc.latitude +
                        "," + dc.longitude + "&z=19");

        ArrayList<Uri> uris = new ArrayList<Uri>();

        uris.add(Uri.parse("file://" + dc.outputPath + dc.photoFilename));
        uris.add(Uri.parse("file://" + dc.outputPath + dc.locationFilename));

        Log.d("URI ARRAY 0", uris.get(0).toString());
        Log.d("URI ARRAY 1", uris.get(1).toString());

        i.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

        try {
            startActivity(i);
            this.finish();
            //startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    // Send the activity_report.
    public void sendReport(View view){

        saveLocationBitmap();

        if(dc.checkReport()) {

            dc.saveReportDataToDisk();

            Toast.makeText(getApplicationContext(), "Report saved to disk!", Toast.LENGTH_SHORT).show();
            mailReportTo("robbert@appteam.nu");

        }else{
            Toast.makeText(getApplicationContext(), "The report is not yet complete!", Toast.LENGTH_SHORT).show();
        }

        dc.init();
    }

    private void saveLocationBitmap(){

        /**
        TextView txt = (TextView) findViewById(R.id.lbl_report_location);
        ImageView mapImage = (ImageView) findViewById(R.id.img_report_location);
        */

        String google_map_link = "https://maps.googleapis.com/maps/api/staticmap?"
                + "center=" + dc.latitude + "," + dc.longitude
                + "&zoom=15"
                + "&size=500x350"
                + "&maptype=roadmap"
                + "&scale=5"
                + "&markers=color:red%7C"+ dc.latitude + "," + dc.longitude;

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
        /**
        txt.setVisibility(View.GONE);

        mapImage.setVisibility(View.VISIBLE);
        mapImage.setImageBitmap(bmp);

         */

        //String root = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SAFOBS/Pictures/";
        File myDir = new File(dc.outputPath);

        File location_img = new File (myDir, dc.locationFilename);

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
}