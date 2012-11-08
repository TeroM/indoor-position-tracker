package com.inte.indoorpositiontracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.graphics.PointF;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

public class MapViewActivity extends MapActivity {
    public static final int SCAN_DELAY = 2000;
    public static final int SCAN_INTERVAL = 2000;
    public static final int MAX_SCAN_THREADS = 2;
    
    private int mScanThreadCount = 0; 
    private WifiPointView mLocationPointer;
    
    // handler for callbacks to the UI thread
    private static Handler updateHandler = new Handler();

    // runnable to refresh map (called by the handler)
    private Runnable mRefreshMap = new Runnable() {
        public void run() {
             refreshMap();
        }
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mLocationPointer = mMap.createNewWifiPointOnMap(new PointF(-1000, -1000));
        mLocationPointer.activate();
        
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                mWifi.startScan();
            }
            
        }, SCAN_DELAY, SCAN_INTERVAL);
        editMap();
    }
    
    @Override
    public void onReceiveWifiScanResults(final List<ScanResult> results) {
        IndoorPositionTracker application = (IndoorPositionTracker) getApplication();
        final ArrayList<Fingerprint> fingerprints = application.getFingerprintData();
        
        // calculating the location might take some time in case there are a lot of fingerprints (>10000),
        // so it's reasonable to limit scan thread count to make sure there are not too many of these threads
        // going on at the same time
        if(results.size() > 0 && fingerprints.size() > 0 && mScanThreadCount <= MAX_SCAN_THREADS) {
            Thread t = new Thread() {
                public void run() {
                    mScanThreadCount++;
                    
                    HashMap<String, Integer> measurements = new HashMap<String, Integer>();
                    for (ScanResult result : results) {
                        measurements.put(result.BSSID, result.level);
                    }
                    Fingerprint f = new Fingerprint(measurements);
                    
                    Fingerprint closestMatch = f.getClosestMatch(fingerprints);
                    mLocationPointer.setFingerprint(closestMatch);
                    
                    // need to refresh map through updateHandler since only UI thread is allowed to touch its views
                    updateHandler.post(mRefreshMap); 
                    
                    mScanThreadCount--;
                }
            };
            t.start();
            

        }
    }
    
    public void editMap() {
        Intent intent = new Intent(MapViewActivity.this, MapEditActivity.class);
        startActivity(intent);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_main, menu);    
        menu.add(1, 1, 0, "EDIT MAP");        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case 1:
                editMap();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
