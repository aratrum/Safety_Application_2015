package controller;

import android.app.Activity;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

public class ReportDataController {

    public Properties report;
    private Activity parent;

    // Constructor.
    public ReportDataController(Activity p){

        createReportProperties();
        parent = p;
    }

    private void createReportProperties(){

        report = new Properties();

        report.setProperty("ID", generateRandomID());
        report.setProperty("Date", getDateString());

        report.setProperty("Situation Type", "");
        report.setProperty("Priority", "");
        report.setProperty("Description", "");

        report.setProperty("Latitude", "");
        report.setProperty("Longitude", "");


        report.setProperty("Data Path", Environment.getExternalStorageDirectory().getAbsolutePath() + "/SAFOBS/Saved Reports/");
        report.setProperty("Output Path", Environment.getExternalStorageDirectory().getAbsolutePath() + "/SAFOBS/Pictures/");
        report.setProperty("Photo Filename", Integer.parseInt(report.getProperty("ID")) + "_photo.jpg");
        report.setProperty("Location Filename", Integer.parseInt(report.getProperty("ID")) + "_location.jpg");
        report.setProperty("Photo URI", "");

        Log.d("ID", "INT = " + Integer.parseInt(report.getProperty("ID")));
    }

    private String generateRandomID(){

        Random rng = new Random();
        return ("" + rng.nextInt(Integer.MAX_VALUE) + "");
    }

    private String getDateString(){

        Date date = new Date();
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        return df.format(date);
    }

    public void saveReportDataToDisk(){

        try {

            File report_data = new File(report.getProperty("Data Path"), "report_data_"
                    + Integer.parseInt(report.getProperty("ID")) + ".xml");

            OutputStream out = new FileOutputStream(report_data);
            report.storeToXML(out, "Report data");

            MediaScannerConnection.scanFile(parent.getApplicationContext(), new String[]{report_data.toString()}, null, null);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean checkReport(){

        return true;
    }

}
