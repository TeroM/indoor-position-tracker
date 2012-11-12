package com.inte.indoorpositiontracker;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class MapView extends ImageView {
	// We can be in one of these 3 states
	private static final int MAP_STATE_NONE = 0;
	private static final int MAP_STATE_DRAG = 1;
	private static final int MAP_STATE_ZOOM = 2;
	
	private static final String TAG = "Touch";
	
	
	// These matrices will be used to move and zoom image
	private Matrix mMatrix = new Matrix();
	private Matrix mSavedMatrix = new Matrix();

	private int mode = MAP_STATE_NONE;

	// Remember some things for zooming
	private PointF mStart = new PointF();
	private PointF mid = new PointF();
	float mOldDist = 1f;
	
	private ArrayList<WifiPointView> mWifiPoints;
	
	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mWifiPoints = new ArrayList<WifiPointView>();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		super.onDraw(canvas);
		
		float[] values = new float[9];
		mMatrix.getValues(values);
		
		for(WifiPointView point : mWifiPoints) {
			point.drawWithTransformations(canvas, values);
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                onTouchStart(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                onMultiTouchStart(event);
                break;
            case MotionEvent.ACTION_UP:
                onTouchEnd(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onMultiTouchEnd(event);
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(event);
                break;
        }
        
		return true;
	}
	    
	public void onTouchStart(MotionEvent event) {
        mSavedMatrix.set(mMatrix);
        mStart.set(event.getX(), event.getY());
        mode = MAP_STATE_DRAG;
        Log.d(TAG, "mode=DRAG");
    }
	    
    public void onTouchEnd(MotionEvent event) {
        mode = MAP_STATE_NONE;
        Log.d(TAG, "mode=NONE");
    }
	    
    public void onMultiTouchStart(MotionEvent event) {
        mOldDist = spacing(event);
        Log.d(TAG, "oldDist=" + mOldDist);
        if (mOldDist > 10f) {
            mSavedMatrix.set(mMatrix);
            midPoint(mid, event);
            mode = MAP_STATE_ZOOM;
            Log.d(TAG, "mode=ZOOM");
        }
    }
	    
    public void onMultiTouchEnd(MotionEvent event) {
        mSavedMatrix.set(mMatrix);
        mode = MAP_STATE_DRAG;
        Log.d(TAG, "mode=DRAG");
    }
    
    public void onTouchMove(MotionEvent event) {
        if (mode == MAP_STATE_DRAG) {
            mapMove(event);
        }
        else if (mode == MAP_STATE_ZOOM) {
            mapZoom(event);
        }
    }
    
    public void mapMove(MotionEvent event) {
        mMatrix.set(mSavedMatrix);
        mMatrix.postTranslate(event.getX() - mStart.x, event.getY() - mStart.y);
        setImageMatrix(mMatrix);
    }
    
    public void mapZoom(MotionEvent event) {
        float newDist = spacing(event);
        Log.d(TAG, "newDist=" + newDist);
        if (newDist > 10f) {
            mMatrix.set(mSavedMatrix);
            float scale = newDist / mOldDist;
            mMatrix.postScale(scale, scale, mid.x, mid.y);
            setImageMatrix(mMatrix);
        }
    }
	
	
	
	
	/*
	 * Helper methods for zoom functionality
	 */

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

	/*
	 * Creates a new WifiPointView and sets it's location.
	 */
	public WifiPointView createNewWifiPointOnMap(PointF location) {
		WifiPointView wpView = new WifiPointView(getContext());
		float[] values = new float[9];
		mMatrix.getValues(values);
		location.set(location.x - values[2] * values[0], location.y - values[5] * values[4]);
		wpView.setLocation(location);
		mWifiPoints.add(wpView);
		return wpView;
	}
	
	public WifiPointView createNewWifiPointOnMap(Fingerprint fingerprint) {
	    WifiPointView wpView = new WifiPointView(getContext());
	    wpView.setFingerprint(fingerprint);
	    mWifiPoints.add(wpView);
	    return wpView;
	}
	
	public WifiPointView createNewWifiPointOnMap(Fingerprint fingerprint, boolean visible) {
	    WifiPointView wpView = createNewWifiPointOnMap(fingerprint);
	    wpView.setVisible(visible);
	    return wpView;
	}
	
	public ArrayList<WifiPointView> getWifiPoints() {
		return mWifiPoints;
	}

	public void setWifiPoints(ArrayList<WifiPointView> wifiPoints) {
		mWifiPoints = wifiPoints;
	}
	
	public void setWifiPointsVisibility(boolean visible) {
	    for(WifiPointView point : mWifiPoints) {
	        point.setVisible(visible);
	    }
	}
}
