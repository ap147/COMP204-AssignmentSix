package com.amarjot8.locationapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

//https://www.youtube.com/watch?v=QNb_3QKSmMk
//http://stackoverflow.com/questions/153724/how-to-round-a-number-to-n-decimal-places-in-java
//https://developers.google.com/maps/documentation/android-api/intents

public class MainActivity extends AppCompatActivity {
    List<SpinnerItem> spinnerList = Arrays.asList(
            // Items created here will be initialized into the list
            new SpinnerItem("From GPS", Uri.parse("google.streetview:cbll="))
            , new SpinnerItem("Waikato Uni", Uri.parse("geo:-37.786778,175.3160027"))
            ,  new SpinnerItem("Auckland Uni", Uri.parse("geo:0,0?q=The+University+of+Auckland"))
            ,  new SpinnerItem("Fifth Ave, Tauranga", Uri.parse("geo:0,0?q=Fifth+Ave,+Tauranga"))
            , new SpinnerItem("Main Street Wellington", Uri.parse("geo:-41.2442851,174.6217706?q=Main+St,+Upper+Hutt+5018"))
    );
    //Used to check what Location is selected when Map selection button clicked
    String Selected = "";
    //Used to send data to google maps about where device is located
    Double lat; Double lon;

    LocationManager locationManager;
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //Whenever location is changed update fields
            lat = location.getLatitude();
            updateLat_val(lat);
            lon = location.getLongitude();
            updateLon_val(lon);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    //Add code to your project that outputs messages to the debug log when a lifecycle method is called
    @Override
    protected void onStart() {
        super.onStart();
        //Use the tag Lifecycle for these messages
        //Simply printing the name of the called method will be sufficient
        updateLog("Lifecycle", "onStart");
        updateLifecycleText("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLog("Lifecycle", "onResume");
        updateLifecycleText("onResume");
        //If user hasnt allowed permision do nothing other wise update GPS status
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        else
        {
            updateGpsStatus(Status.ENABLED);
        }
        try
        {
            //If devices GPS is turned on then listen to it , otherwise up date GPS status
            if(locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER))
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 10, locationListener);
            else
                updateGpsStatus(Status.DISABLED);
        }
        catch (SecurityException e)
        {}
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateLog("Lifecycle", "onPause");
        //If permissions wernt granted then ask for them
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        else
            updateGpsStatus(Status.ENABLED);

        //Unregistering listener when user exits app
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
        //Updating lifecycle status
        updateLog("Lifecycle","onCreate");
        updateGpsStatus(Status.UNKNOWN);

        //Getting package manager  & Asking if this phone has GPS
        PackageManager packageManager = this.getPackageManager();
        boolean hasGPS = packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
        //If phone has gps set GPS Status textview to unknown otherwise unavailable
        if(hasGPS)
            updateGpsStatus(Status.UNKNOWN);
        else
            updateGpsStatus(Status.UNAVAILABLE);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //Check if GPS permission was granted if not request them
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},12);

        else
            updateGpsStatus(Status.ENABLED);

        //Adding spinnerlist to the spinner
        ((Spinner) findViewById(R.id.spinner)).setAdapter(new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, spinnerList));
        //Getting spinner and adding a listner to it
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                //Getting what is selected
                TextView t = (TextView)view;
                //Getting button
                Button b = (Button) findViewById(R.id.button);
                //If spinnner is selecting "From GPS"
                if(t.getText().equals("From GPS"))
                {
                    Selected = "From GPS";
                    //When GPS is not On, Disable button , otherwise enable it
                    if(!checkIfGPSEnabled())
                        b.setEnabled(false);
                    else
                        b.setEnabled(true);
                }
                //If anything else is selected, enable button incase it was disabled before, update Selected so we know what is selected when button is pressed
                else
                {
                   b.setEnabled(true);
                   Selected = t.getText().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //Getting Map Selection button
        Button b = (Button) findViewById(R.id.button);
        //What to do when its clicked
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Used to store Address which is sent to google maps
                Uri uri;
                Intent mapIntent  = null;
                //When user clicks button while "From GPS" is selected in spinner
                if(Selected.equals("From GPS"))
                {
                    //Checking these values were updated
                    if(lat != null & lon !=null)
                    {
                        //Sending data to google maps
                        uri = Uri.parse("geo:" + lat + "," + lon);
                        mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                    }
                }
                else if (Selected.equals("Waikato Uni"))
                {
                    uri = spinnerList.get(1).uri;
                    mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                }
                else if(Selected.equals("Auckland Uni"))
                {
                    uri = spinnerList.get(2).uri;
                    mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                }
                else if(Selected.equals("Fifth Ave, Tauranga"))
                {
                    uri = spinnerList.get(3).uri;
                    mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                }
                else if(Selected.equals("Main Street Wellington"))
                {
                    uri = spinnerList.get(4).uri;
                    mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                }
                if(mapIntent!= null)
                {
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getPackageManager()) != null)
                        startActivity(mapIntent);
                }
            }
        });
    }

    //Checks if User has granted permission
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
                    updateGpsStatus(Status.DISABLED);

                else if(grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    updateGpsStatus(Status.ENABLED);
            }
        }
    }

    //Prints message to log
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
    }
    protected void updateLifecycleText(String message)
    {
        //Getting textbox & altering to the recent method called
        TextView textView = (TextView) findViewById(R.id.currentLifecycle);
        textView.setText(message);
    }

    protected boolean checkIfGPSEnabled(){
        return locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER);
    }

    //These are the status a GPS could have
    protected enum Status{
        UNKNOWN, ENABLED, DISABLED, UNAVAILABLE
    }
    //Updates GPS status/Color
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
            //Occurs when User had allowed GPS access & device has gps
            case ENABLED:
                //Checking if GPS is enabled
                if ( locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                    textView.setTextColor(Color.GREEN);
                    break;
                }
                else
                {
                    this.updateGpsStatus(Status.DISABLED);
                    updateLog("GPS: ","GPS is turned off, Please enable" );
                }
             //Occurs when User doesnt allow GPS access or GPS is turned off
            case DISABLED:
                textView.setTextColor(Color.RED);
                break;
            //This case only occurs when device doesnt have GPS
            case UNAVAILABLE:
                textView.setTextColor(Color.BLUE);
                break;
        }
    }

    //These Two methods round down lat/lon to 4dp and update it
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
