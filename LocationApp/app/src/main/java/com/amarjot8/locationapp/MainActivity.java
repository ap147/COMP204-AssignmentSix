package com.amarjot8.locationapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
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
    }
    @Override
    protected void onPause() {
        super.onPause();
        updateLog("Lifecycle","onPause");
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
}
