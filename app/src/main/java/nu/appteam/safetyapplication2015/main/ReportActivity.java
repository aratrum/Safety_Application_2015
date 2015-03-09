package nu.appteam.safetyapplication2015.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
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
import java.util.ArrayList;

import nu.appteam.safetyapplication2015.main.util.DataController;
import nu.appteam.safetyapplication2015.R;

public class ReportActivity extends ActionBarActivity {

    // Class Members.
    DataController dc = DataController.getInstance();
    static int REQUEST_IMAGE_CAPTURE = 100;
    ImageView img;
    TextView img_txt, date_txt;
    Button btn_situation, btn_priority, btn_description;

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

        initViewAndListeners();
    }

    // Initialise the Views and their dedicated Listeners.
    private void initViewAndListeners(){

        img = (ImageView)findViewById(R.id.img_report_photo);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //img.setImageResource(R.drawable.ic_camera_google_clicked);
                startPictureIntent();
            }
        });

        img_txt = (TextView) findViewById(R.id.myImageViewText);

        date_txt = (TextView) findViewById(R.id.lbl_report_date);
        date_txt.setText(dc.date);

        btn_situation = (Button) findViewById(R.id.btn_report_situationtype);
        btn_situation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setSituationType();
            }
        });

        btn_priority = (Button) findViewById(R.id.btn_report_priority);
        btn_priority.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setPriority();
            }
        });

        btn_description = (Button) findViewById(R.id.btn_report_description);
        btn_description.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setDescription();
            }
        });

    }

    // Create a camera image and save it to the sdcard.
    private void startPictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Save the captured image on the sdcard.
            File photo = new File(dc.outputPath, dc.photoFilename);
            dc.photoURI = "" + Uri.fromFile(photo) + "";

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse(dc.photoURI));
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    // Activates after the camera app closes. Display the captured image on screen.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Uri imageUri = Uri.parse(dc.photoURI);

            try {

                Bitmap imageBitmap = roundBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri), 300);
                img.setImageBitmap(imageBitmap);
                img_txt.setText("Tap to change");

            } catch (IOException e) {

                e.printStackTrace();
            }
        }

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.parse(dc.photoURI));
        this.sendBroadcast(mediaScanIntent);
    }

    // Create the popup-dialog for the observation type.
    public void setSituationType(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Specify the dialog options.
        builder.setItems(R.array.situationtype_list,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int index) {

                        // Specify the actions after an item has been pressed.
                        String[] observationList = getResources().getStringArray(R.array.situationtype_list);
                        String selectedItem = observationList[index];

                        dc.situationType = selectedItem;

                        Toast toast = Toast.makeText(getApplicationContext(), (selectedItem + " selected."), Toast.LENGTH_SHORT);
                        btn_situation.setText(selectedItem);
                        btn_situation.setTextColor(Color.argb(255, 10, 200, 10));

                        toast.show();
                    }
                });

        // Show the dialog.
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Create the popup-dialog for the observation type.
    public void setPriority(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Specify the dialog options.
        builder.setItems(R.array.priority_list,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int index) {

                        // Specify the actions after an item has been pressed.
                        String[] priorityList = getResources().getStringArray(R.array.priority_list);
                        String selectedItem = priorityList[index];

                        dc.priority = selectedItem;

                        Toast toast = Toast.makeText(getApplicationContext(), (selectedItem + " selected."), Toast.LENGTH_SHORT);
                        btn_priority.setText(selectedItem + " priority");
                        btn_priority.setTextColor(Color.argb(255, 10, 200, 10));
                        toast.show();
                    }
                });

        // Show the dialog.
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Create the popup for the activity_report description.
    public void setDescription(){

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
                        EditText textbox = (EditText) v.findViewById(R.id.txt_description_box);
                        dc.description = textbox.getText().toString();

                        btn_description.setText("Description added (Tap to change)");
                        btn_description.setTextColor(Color.argb(255, 10, 200, 10));

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
            mailReportTo(dc.managerEmail);

        }else{
            Toast.makeText(getApplicationContext(), "The report is not yet complete!", Toast.LENGTH_SHORT).show();
        }

        dc.init();
    }

    // Get the Google Maps picture of the current position.
    private void saveLocationBitmap(){

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

    // Rounds the edges of a Bitmap.
    private Bitmap roundBitmap(Bitmap bmp, int radius) {

        Bitmap scaledBm;
        if(bmp.getWidth() != radius || bmp.getHeight() != radius)
            scaledBm = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        else
            scaledBm = bmp;
        Bitmap output = Bitmap.createBitmap(scaledBm.getWidth(),
                scaledBm.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, scaledBm.getWidth(), scaledBm.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(scaledBm.getWidth() / 2+0.7f, scaledBm.getHeight() / 2+0.7f,
                scaledBm.getWidth() / 2+0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(scaledBm, rect, rect, paint);

        return output;
    }
}