package com.inte.indoorpositiontracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class WifiPointView extends View {
	
	// private WifiFingerPrint fpData; <- fingerprintti... varmaankin osoittaa jossain muualla sijaitsevaan arrayhin?
	
	private boolean active;
	
	private Paint redpaint;
	private Paint activepaint;
	private Paint selectedpaint;
	
	private float[] location;
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
		
		this.location = new float[2];
		
		this.active = false;
		
		this.radius = 10f;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	}
	
	protected void drawWithTransformations(Canvas canvas, float[] matrixValues) {
	    
		this.relativeX = matrixValues[2] + location[0] * matrixValues[0];
		this.relativeY = matrixValues[5] + location[1] * matrixValues[4];
		
		if(this.active) {
			this.selectedpaint = this.activepaint;
		} else {
			this.selectedpaint = this.redpaint;
		}
		
		canvas.drawCircle(this.relativeX, this.relativeY, this.radius, this.selectedpaint);
	}
	
	public void setLocation(float x, float y) {
		this.location[0] = x;
		this.location[1] = y;
	}
	
	public void setSize(float radius) {
	    this.radius = radius;
	}
	
	public void setFingerPrint(/*WifiFingerPrint data*/) {
		//this.fpData = data;
	}
	
	public void activate() {
		this.active = true;
	}
	
	public void deactivate() {
		this.active = false;
	}

}
