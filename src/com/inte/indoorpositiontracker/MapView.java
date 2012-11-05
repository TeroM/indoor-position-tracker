package com.inte.indoorpositiontracker;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class MapView extends ImageView {

	private final String TAG = "Touch";
	// These matrices will be used to move and zoom image
	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();

	// We can be in one of these 3 states
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private int mode = NONE;

	// Remember some things for zooming
	private PointF start = new PointF();
	private PointF mid = new PointF();
	float oldDist = 1f;

	private Paint paint;
	private Bitmap drawingBitmap;
	private Canvas mapCanvas;
	
	private float[] matrixValues;
	private float relativeX,relativeY;
	
	private ArrayList<WifiPointView> wifiPoints;

	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.drawingBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.kerros);
		
		this.setImageBitmap(drawingBitmap);
		
		this.mapCanvas = new Canvas(this.drawingBitmap);

		this.paint = new Paint();
		paint.setColor(Color.RED);
		paint.setAntiAlias(true);

		this.matrixValues = new float[9];
		this.relativeX = 0;
		this.relativeY = 0;
		
		this.wifiPoints = new ArrayList<WifiPointView>();
		
		WifiPointView wp1 = createNewWifiPointOnMap(20, 20);
		wp1.activate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		
		super.onDraw(canvas);
		
		this.matrix.getValues(this.matrixValues);
		
		for(WifiPointView point : this.wifiPoints) {
			point.drawWithTransformations(canvas, this.matrixValues);
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

		// Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			this.savedMatrix.set(this.matrix);
			this.start.set(event.getX(), event.getY());
			Log.d(this.TAG, "mode=DRAG");
			this.mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			this.oldDist = this.spacing(event);
			Log.d(this.TAG, "oldDist=" + this.oldDist);
			if (this.oldDist > 10f) {
				this.savedMatrix.set(this.matrix);
				this.midPoint(this.mid, event);
				this.mode = ZOOM;
				Log.d(this.TAG, "mode=ZOOM");
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			this.mode = NONE;
			Log.d(this.TAG, "mode=NONE");

			/*
			 * Add new wifi-fingerprint-ball
			 * */
			
			this.matrix.getValues(this.matrixValues);
			
			this.relativeX = (event.getX() - this.matrixValues[2]) / this.matrixValues[0];
			this.relativeY = (event.getY() - this.matrixValues[5]) / this.matrixValues[4];
			
			createNewWifiPointOnMap(this.relativeX,this.relativeY);
			invalidate();


			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				// ...
				this.matrix.set(this.savedMatrix);
				this.matrix.postTranslate(event.getX() - start.x,
						event.getY() - start.y);
			}
			else if (mode == ZOOM) {
				float newDist = this.spacing(event);
				Log.d(TAG, "newDist=" + newDist);
				if (newDist > 10f) {
					this.matrix.set(this.savedMatrix);
					float scale = newDist / this.oldDist;
					this.matrix.postScale(scale, scale, this.mid.x, this.mid.y);
				}
			}
			break;
		}

		this.setImageMatrix(this.matrix);
		return true;
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
	 * Creates a new WifiPointView and sets it's location relative to map.
	 */
	public WifiPointView createNewWifiPointOnMap(float x,float y) {
		WifiPointView wpView = new WifiPointView(this.getContext());
		wpView.setLocation(x,y);
		this.wifiPoints.add(wpView);
		return wpView;
	}
	
	public ArrayList<WifiPointView> getWifiPoints() {
		return wifiPoints;
	}

	public void setWifiPoints(ArrayList<WifiPointView> wifiPoints) {
		this.wifiPoints = wifiPoints;
	}

}
