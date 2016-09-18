package com.amarjot8.locationapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

//https://www.youtube.com/watch?v=QNb_3QKSmMk
//http://stackoverflow.com/questions/153724/how-to-round-a-number-to-n-decimal-places-in-java



public class MainActivity extends AppCompatActivity {
    List<SpinnerItem> spinnerList = Arrays.asList(
            // Items created here will be initialized into the list
            new SpinnerItem("An Item", Uri.parse("")),
            new SpinnerItem("B", Uri.parse(""))
    );

    LocationManager locationManager;
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateLog("Location", "Location Changed");
            double lat = location.getLatitude();
            updateLat_val(lat);
            double lon = location.getLongitude();
            updateLon_val(lon);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    //Add code to your project that outputs messages to the debug log when a lifecycle method is called
    @Override
    protected void onStart() {
        super.onStart();
        //Use the tag Lifecycle for these messages
        //Simply printing the name of the called method will be sufficient
        updateLog("Lifecycle", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLog("Lifecycle", "onResume");
        //If permissions wernt granted then ask for them
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        else
        {
            updateGpsStatus(Status.ENABLED);
        }
        try
        {
            if(locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ))
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 10, locationListener);

            else
                updateGpsStatus(Status.DISABLED);
        }
        catch (SecurityException e)
        {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateLog("Lifecycle", "onPause");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        else
        {
            updateGpsStatus(Status.ENABLED);
        }
        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onStop() {
        updateLog("Lifecycle","onStop");
        super.onStop();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateLog("Lifecycle","onCreate");
        updateGpsStatus(Status.UNKNOWN);

        //Getting package manager  & Asking if this phone has GPS
        PackageManager packageManager = this.getPackageManager();
        boolean hasGPS = packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
        if(hasGPS)
            updateGpsStatus(Status.UNKNOWN);
        else
            updateGpsStatus(Status.UNAVAILABLE);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //Check if GPS permission was granted if not request them
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},12);
            return;
        }
        else
        {
            updateGpsStatus(Status.ENABLED);
        }

        ((Spinner) findViewById(R.id.spinner_id)).setAdapter(new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, spinnerList));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //Going through permissions
        for(int i =0; i < permissions.length; i++)
        {
            //when GPS permission is found
            if(permissions[i].compareTo(Manifest.permission.ACCESS_FINE_LOCATION) == 0)
            {
                //Check if it was denied & change status accordingly
                if(grantResults[i] == PackageManager.PERMISSION_DENIED)
                {
                    updateLog("Error", "User Denied GPS Permistion");
                    updateGpsStatus(Status.DISABLED);
                }
                else if(grantResults[i] == PackageManager.PERMISSION_GRANTED)
                {
                    updateGpsStatus(Status.ENABLED);
                }
            }
        }
    }

    protected void updateLog(String tag, String message)
    {
        //printing what lifecycle method was called
        Log.e(tag, message);
        try {
            //Slowing it down so they all come in order
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        updateLifecycleText(message);
    }
    protected void updateLifecycleText(String message)
    {
        //Getting textbox & altering to the recent method called
        TextView textView = (TextView) findViewById(R.id.currentLifecycle);
        textView.setText(message);
    }
    protected enum Status{
        UNKNOWN, ENABLED, DISABLED, UNAVAILABLE
    }
    protected void updateGpsStatus(Status message)
    {
        //Getting textview & changing status
        TextView textView = (TextView) findViewById(R.id.currentGpustatus);
        textView.setText(message.toString());
        Status s = message;

        //Depending on what case it is change text color
        switch (s)
        {
            case UNKNOWN:
                //Orange
                textView.setTextColor(Color.parseColor("#FFA500"));
                break;

            case ENABLED:
                if ( locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                    textView.setTextColor(Color.GREEN);
                    break;
                }
                else
                {
                    this.updateGpsStatus(Status.DISABLED);
                    updateLog("GPS: ","GPS is turned off, Please enable" );
                }


            case DISABLED:
                textView.setTextColor(Color.RED);
                break;

            case UNAVAILABLE:
                textView.setTextColor(Color.BLUE);
                break;
        }
    }
    @TargetApi(Build.VERSION_CODES.N)
    protected void updateLat_val(double lat)
    {
        double roundedDown = (double)Math.round(lat * 10000d) / 10000d;
        //Getting textview & changing status
        TextView textView = (TextView) findViewById(R.id.lat_val);
        String x = "" + roundedDown;
        textView.setText(x);
        updateLog("Lat", "updating lat");
    }
    protected void updateLon_val(double lon){
        double roundedDown = (double)Math.round(lon * 10000d) / 10000d;
        //Getting textview & changing status
        TextView textView = (TextView) findViewById(R.id.lon_val);
        String x = "" + roundedDown;
        textView.setText(x);
        updateLog("Lon", "updating lon");
    }
}
