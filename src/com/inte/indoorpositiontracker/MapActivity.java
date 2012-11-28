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
import android.view.Menu;
import android.view.SubMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class MapActivity extends Activity implements OnTouchListener {
    private static final int MENU_ITEM_CHOOSE_FLOOR = 1;
    private static final int MENU_ITEM_BASEMENT = 2;
    private static final int MENU_ITEM_1STFLOOR = 3;
    private static final int MENU_ITEM_2NDFLOOR = 4;
    private static final int MENU_ITEM_3RDFLOOR = 5;
    private static final int MENU_ITEM_4THFLOOR = 6;
    
    
    protected WifiManager mWifi;
    protected MapView mMap; // map object
    protected BroadcastReceiver mReceiver; // for receiving wifi scan results
    protected IndoorPositionTracker mApplication;
    
    protected String mSelectedMap; // id of the map which is currently being displayed
    
    
    
    
    /** INSTANCE METHODS */
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMap = (MapView) findViewById(R.id.mapView);
        mMap.setOnTouchListener(this);
        
        mApplication = (IndoorPositionTracker) getApplication();
        mWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        
        this.setMap(R.drawable.kerros); // set map to default location (== first floor)
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
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // add menu items
    	
    	super.onCreateOptionsMenu(menu);
    	SubMenu sub = menu.addSubMenu(Menu.NONE, MENU_ITEM_CHOOSE_FLOOR, Menu.NONE, "Choose floor");
    	
    	sub.add(Menu.NONE, MENU_ITEM_BASEMENT, Menu.NONE, "Basement");
    	sub.add(Menu.NONE, MENU_ITEM_1STFLOOR, Menu.NONE, "1. floor");
    	sub.add(Menu.NONE, MENU_ITEM_2NDFLOOR, Menu.NONE, "2. floor");
    	sub.add(Menu.NONE, MENU_ITEM_3RDFLOOR, Menu.NONE, "3. floor");
    	sub.add(Menu.NONE, MENU_ITEM_4THFLOOR, Menu.NONE, "4. floor");
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case MENU_ITEM_BASEMENT:
                setMap(R.drawable.pohja);
                return true;
            case MENU_ITEM_1STFLOOR:
                setMap(R.drawable.kerros);
                return true;
            case MENU_ITEM_2NDFLOOR:
                setMap(R.drawable.toka);
                return true;
            case MENU_ITEM_3RDFLOOR:
                setMap(R.drawable.kolmas);
                return true;
            case MENU_ITEM_4THFLOOR:
                setMap(R.drawable.neljas);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void refreshMap() {
        mMap.invalidate(); // redraws the map screen
    }
    
    public void setMap(int resId) {
        mSelectedMap = String.valueOf(resId);
        mMap.setImageResource(resId); // change map image
    }
}

