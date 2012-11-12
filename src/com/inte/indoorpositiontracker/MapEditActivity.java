package com.inte.indoorpositiontracker;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import android.app.ProgressDialog;
import android.graphics.PointF;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class MapEditActivity extends MapActivity{
    private static final int SCAN_COUNT = 3;
    private static final int SCAN_DELAY = 500;
    
    private WifiPointView mPointer;
    private long mTouchStarted;
    private int mScansLeft = 0;
    
    private ProgressDialog mLoadingDialog; // loading bar which is shown while scanning access points
    
    private HashMap<String, Integer> mMeasurements;
    
    @Override
    public void onReceiveWifiScanResults(List<ScanResult> results) {
        if(mScansLeft != 0 && mPointer != null) {
            if(results.size() > 2) {
                mScansLeft--;
                
                HashMap<String, Integer> measurements = new HashMap<String, Integer>();
                Log.d("ss", "\n\n\n\n");
                for (ScanResult result : results) {
                    measurements.put(result.BSSID, result.level);
                    Log.d("ss", " " + result.BSSID + " " + result.level + "\n");
                }
                
                // go through scan results and add measurement values
                TreeSet<String> keys = new TreeSet<String>();
                keys.addAll(measurements.keySet());
                keys.addAll(mMeasurements.keySet());
                
                for (String key : keys) {
                    Integer value = measurements.get(key);
                    Integer oldValue = mMeasurements.get(key);
                    
                    if(oldValue == null) {
                        mMeasurements.put(key, value + (-119 * (SCAN_COUNT - 1 - mScansLeft)));
                    } else if(value == null) {
                        mMeasurements.put(key, -119 + oldValue);
                    } else {
                        mMeasurements.put(key, value + oldValue);
                    }
                }
                
                
                if(mScansLeft > 0) { // keep on scanning
                    scanNext();
                } else { // calculate average of measurements and add fingerprint
                    // calculate average for each measurement
                    Log.d("ss", "\n\n\n\n");
                    for (String key : mMeasurements.keySet()) {
                        int value = (int) mMeasurements.get(key) / SCAN_COUNT;
                        Log.d("ss", " " + key + " " + value + "\n");
                        mMeasurements.put(key, value);
                    }
                    
                    Fingerprint f = new Fingerprint(mMeasurements);
                    f.setLocation(mPointer.getLocation());
                    IndoorPositionTracker application = (IndoorPositionTracker) getApplication();
                    application.addFingerprint(f);
                    mLoadingDialog.dismiss();
                }
            } else {
                mLoadingDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Failed to create fingerprint. Could not find enough access points (found "
                        + results.size() + ", need at least 3).", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    public boolean onTouch(View v, MotionEvent event) {
        v.onTouchEvent(event);
        
        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mTouchStarted = event.getEventTime();
                break;
            case MotionEvent.ACTION_UP:
                if (event.getEventTime() - mTouchStarted < 150) {
                    PointF location = new PointF(event.getX(), event.getY());
                    if(mPointer == null) {
                        mPointer = mMap.createNewWifiPointOnMap(location);
                    } else {
                        mPointer.setLocation(location);
                    }
                    refreshMap();
                    startScan(); // show loading dialog and start wifi scan
                }
                break;
        }
        
        return true; // indicate event was handled
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_main, menu);    
        menu.add(1, 1, 0, "EXIT EDIT MODE");        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case 1:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void startScan() {
        mScansLeft = SCAN_COUNT;
        mMeasurements = new HashMap<String, Integer>();
        mLoadingDialog = ProgressDialog.show(this, "", "Scanning..", true);
        mWifi.startScan();
    }
    
    public void scanNext() {
        Timer timer = new Timer();
        
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                mWifi.startScan(); 
            }
            
        }, SCAN_DELAY);
    }
}
