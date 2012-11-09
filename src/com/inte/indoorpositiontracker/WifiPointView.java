package com.inte.indoorpositiontracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.View;

public class WifiPointView extends View {
	
	private Fingerprint fingerprint;
	
	private boolean active;
	
	private Paint redpaint;
	private Paint activepaint;
	private Paint selectedpaint;
	
	private PointF mLocation;
	private float radius;
	
	// placeholders for calculated screen positions
	private float relativeX,relativeY;

	public WifiPointView(Context context) {
		super(context);
		this.redpaint = new Paint();
		this.redpaint.setColor(Color.RED);
		this.redpaint.setTextSize(25);
		this.redpaint.setAntiAlias(true);
		
		this.activepaint = new Paint();
		this.activepaint.setColor(Color.GREEN);
		this.activepaint.setTextSize(25);
		this.activepaint.setAntiAlias(true);
		
		this.active = false;
		
		this.radius = 10f;
		
		mLocation = new PointF(0,0);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	}
	
	protected void drawWithTransformations(Canvas canvas, float[] matrixValues) {
	    
		this.relativeX = mLocation.x + matrixValues[2] * matrixValues[0];
		this.relativeY = mLocation.y + matrixValues[5] * matrixValues[4];
		
		if(this.active) {
			this.selectedpaint = this.activepaint;
			this.radius = 20;
		} else {
			this.selectedpaint = this.redpaint;
		}
		
		canvas.drawCircle(this.relativeX, this.relativeY, this.radius, this.selectedpaint);
	}
	
	public void setLocation(PointF location) {
		mLocation = location;
	}
	
	public PointF getLocation() {
	    return mLocation;
	}
	
	public void setSize(float radius) {
	    this.radius = radius;
	}
	
	public void setFingerprint(Fingerprint fingerprint) {
		this.fingerprint = fingerprint;
		mLocation = fingerprint.getLocation();
	}
	
	public Fingerprint getFingerprint() {
	    return this.fingerprint;
	}
	
	public void activate() {
		this.active = true;
	}
	
	public void deactivate() {
		this.active = false;
	}
}
