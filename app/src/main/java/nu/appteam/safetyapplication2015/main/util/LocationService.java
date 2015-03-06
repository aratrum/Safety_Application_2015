package nu.appteam.safetyapplication2015.main.util;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class LocationService extends Service {

    // Service members.
    private ServiceHandler service_handler;
    private LocationManager locationManager;
    private LocationListener locationListener;

    DataController dc = DataController.getInstance();

    int times_loc_updated = 0;

    static int LOCATION_UPDATE_AMOUNT = 5;
    static String LOCATION_PROVIDER = LocationManager.NETWORK_PROVIDER;

    // Handler that receives messages from the thread.
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            // Acquire a reference to the system Location Manager
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // Define a listener that responds to location updates
            locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {

                    times_loc_updated++;

                    // Called when a new location is found by the network location provider.
                    Log.d("LocationListener", "Location Changed!");
                    useNewLocation(location);

                    if(times_loc_updated == LOCATION_UPDATE_AMOUNT){
                        locationManager.removeUpdates(locationListener);
                    }
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {}

                public void onProviderEnabled(String provider) {}

                public void onProviderDisabled(String provider) {}
            };

            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LOCATION_PROVIDER, 0, 0, locationListener);

            if(times_loc_updated == 0) {
                Location lastKnownLocation = locationManager.getLastKnownLocation(LOCATION_PROVIDER);
                useNewLocation(lastKnownLocation);
            }

            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job.
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {

        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments", 40);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        Looper service_looper = thread.getLooper();
        service_handler = new ServiceHandler(service_looper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Toast.makeText(this, " -- Starting Location Service -- ", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = service_handler.obtainMessage();
        msg.arg1 = startId;
        service_handler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        // Do something when the service closes.
    }

    private void useNewLocation(Location location){

        double newLat = location.getLatitude();
        double newLon = location.getLongitude();

        Log.d("useNewLocation", "Times updated = " + times_loc_updated);
        Log.d("useNewLocation", "Provider enabled = " + locationManager.isProviderEnabled(LOCATION_PROVIDER));
        Log.d("useNewLocation", "latitude = " + newLat);
        Log.d("useNewLocation", "Longitude = " + newLon);

        dc.latitude = newLat;
        dc.longitude = newLon;
    }
}
