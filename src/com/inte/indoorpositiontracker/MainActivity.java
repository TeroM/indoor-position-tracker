package com.inte.indoorpositiontracker;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.view.Menu;



public class MainActivity extends Activity implements OnTouchListener {

	   private static final String TAG = "Touch";
	   // These matrices will be used to move and zoom image
	   Matrix matrix = new Matrix();
	   Matrix savedMatrix = new Matrix();

	   // We can be in one of these 3 states
	   static final int NONE = 0;
	   static final int DRAG = 1;
	   static final int ZOOM = 2;
	   int mode = NONE;

	   // Remember some things for zooming
	   PointF start = new PointF();
	   PointF mid = new PointF();
	   float oldDist = 1f;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView view = (ImageView) findViewById(R.id.imageView);
        view.setOnTouchListener(this);
    }
   

    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;

        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
           savedMatrix.set(matrix);
           start.set(event.getX(), event.getY());
           Log.d(TAG, "mode=DRAG");
           mode = DRAG;
           break;
        case MotionEvent.ACTION_POINTER_DOWN:
           oldDist = spacing(event);
           Log.d(TAG, "oldDist=" + oldDist);
           if (oldDist > 10f) {
              savedMatrix.set(matrix);
              midPoint(mid, event);
              mode = ZOOM;
              Log.d(TAG, "mode=ZOOM");
           }
           break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_POINTER_UP:
           mode = NONE;
           Log.d(TAG, "mode=NONE");
           break;
        case MotionEvent.ACTION_MOVE:
           if (mode == DRAG) {
              // ...
              matrix.set(savedMatrix);
              matrix.postTranslate(event.getX() - start.x,
                    event.getY() - start.y);
           }
           else if (mode == ZOOM) {
              float newDist = spacing(event);
              Log.d(TAG, "newDist=" + newDist);
              if (newDist > 10f) {
                 matrix.set(savedMatrix);
                 float scale = newDist / oldDist;
                 matrix.postScale(scale, scale, mid.x, mid.y);
              }
           }
           break;
        }

        view.setImageMatrix(matrix);
        return true; // indicate event was handled
     }

     /** Determine the space between the first two fingers */
     private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
     }

     /** Calculate the mid point of the first two fingers */
     private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
     }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_main, menu);   	
    	  menu.add(1, 1, 0, "RESET SCAN");
    	  menu.add(1, 2, 1, "OPTIONS");
    	  menu.add(1, 3, 2, "EXIT");    	     
        return true;
    }
}

