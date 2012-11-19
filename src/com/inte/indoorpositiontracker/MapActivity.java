package com.inte.indoorpositiontracker;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class MapActivity extends Activity implements OnTouchListener {
    protected WifiManager mWifi;
    protected MapView mMap; // map object
    private BroadcastReceiver mReceiver; // for receiving wifi scan results
    
    
    
    /** INSTANCE METHODS */
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMap = (MapView) findViewById(R.id.mapView);
        mMap.setOnTouchListener(this);
        
        
        mWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        
    }
    
    public void onStart() {
        super.onStart();
        
        mReceiver = new BroadcastReceiver ()
        {
            @Override
            public void onReceive(Context c, Intent intent) 
            {
                onReceiveWifiScanResults(mWifi.getScanResults());

            }
        };
        
        registerReceiver(mReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }
    
    public void onReceiveWifiScanResults(List<ScanResult> results) {
        
    }

    public boolean onTouch(View v, MotionEvent event) {
    	v.onTouchEvent(event);
    	
        return true; // indicate event was handled
    }
    
    @Override
    protected void onStop()
    {
        unregisterReceiver(mReceiver);
        super.onStop();
    }
    
    public void refreshMap() {
        mMap.invalidate(); // redraws the map screen
    }
}

