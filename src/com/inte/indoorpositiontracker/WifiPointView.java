package com.inte.indoorpositiontracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.View;

public class WifiPointView extends View {
	private static final int COLOR_INACTIVE = Color.RED;
	private static final int COLOR_ACTIVE = Color.GREEN;
	
	private Fingerprint mFingerprint;
	
	private boolean mActive;
	
	private Paint mPaint; // draw color
	
	private PointF mLocation; // location on screen
	private float mRadius; // circle radius
	
	// placeholders for calculated screen positions
	private float mRelativeX, mRelativeY;

	private boolean mVisible;

	public WifiPointView(Context context) {
		super(context);
		mPaint = new Paint();
		mPaint.setColor(COLOR_INACTIVE);
		mPaint.setTextSize(25);
		mPaint.setAntiAlias(true);
		
		mActive = false;
		mVisible = true;
		mRadius = 10f;
		mLocation = new PointF(0,0);
		mFingerprint = null;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	}
	
	protected void drawWithTransformations(Canvas canvas, float[] matrixValues) {
		mRelativeX = matrixValues[2] + mLocation.x * matrixValues[0];
		mRelativeY = matrixValues[5] + mLocation.y * matrixValues[4];
    		
		if(mVisible == true) { // draw only if set visible
    		if(mActive) { // choose draw color based on active state
    		    mPaint.setColor(COLOR_ACTIVE);
    		} else {
    		    mPaint.setColor(COLOR_INACTIVE);
    		}
    		
    		canvas.drawCircle(mRelativeX, mRelativeY, mRadius, mPaint);
	    }
	}
	
	public void setLocation(PointF location) {
		mLocation = location;
	}
	
	public PointF getLocation() {
	    return mLocation;
	}
	
	public void setSize(float radius) {
	    mRadius = radius;
	}
	
	public void setFingerprint(Fingerprint fingerprint) {
		mFingerprint = fingerprint;
		mLocation = fingerprint.getLocation();
	}
	
	public Fingerprint getFingerprint() {
	    return mFingerprint;
	}
	
	public void activate() {
		mActive = true;
	}
	
	public void deactivate() {
		mActive = false;
	}
	
	public void setVisible(boolean visible) {
	    mVisible = visible;
	}
	
	public boolean isVisible() {
	    return mVisible;
	}
}
