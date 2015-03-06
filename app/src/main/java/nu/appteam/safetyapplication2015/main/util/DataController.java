package nu.appteam.safetyapplication2015.main.util;

import android.os.Environment;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class DataController {

    public int ID;
    public double latitude, longitude = 0;
    public String date, situationType, priority, description,
            photoFilename, locationFilename, photoURI, dataPath, outputPath;

    private static DataController instance = null;

    protected DataController() {
        // Exists only to defeat instantiation.
        init();
    }

    public static DataController getInstance() {
        if(instance == null) {
            instance = new DataController();
        }
        return instance;
    }

    public void init(){

        ID = generateRandomID();
        date = getDateString();

        dataPath =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/SAFOBS/Saved Reports/";
        outputPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SAFOBS/Pictures/";

        photoFilename = ID + "_photo.jpg";
        locationFilename = ID + "_location.jpg";
    }

    private int generateRandomID(){

        Random rng = new Random();
        return (rng.nextInt(Integer.MAX_VALUE));
    }

    private String getDateString(){

        Date date = new Date();
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        return df.format(date);
    }

    public void saveReportDataToDisk(){

        Log.d("Report", "ID = " + ID);
        Log.d("Report", "Date = " + date);
        Log.d("Report", "SituationType = " + situationType);
        Log.d("Report", "Priority = " + priority);
        Log.d("Report", "Description = " + description);
        Log.d("Report", "Latitude = " + latitude);
        Log.d("Report", "Longitude = " + longitude);

        /**try {

         File report_data = new File(report.getProperty("Data Path"), "report_data_"
         + Integer.parseInt(report.getProperty("ID")) + ".xml");

         OutputStream out = new FileOutputStream(report_data);
         report.storeToXML(out, "Report data");

         MediaScannerConnection.scanFile(parent.getApplicationContext(), new String[]{report_data.toString()}, null, null);

         }catch (Exception e){
         e.printStackTrace();
         }*/
    }

    public boolean checkReport(){

        return true;
    }
}
