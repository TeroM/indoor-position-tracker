package com.inte.indoorpositiontracker;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;

import com.inte.indoorpositiontracker.MapView;

public class MainActivity extends Activity implements OnTouchListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MapView view = (MapView) findViewById(R.id.mapView);
        view.setOnTouchListener(this);
    }

    public boolean onTouch(View v, MotionEvent event) {
    	v.onTouchEvent(event);
    	
        return true; // indicate event was handled
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	
}

