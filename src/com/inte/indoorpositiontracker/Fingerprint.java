package com.inte.indoorpositiontracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import android.graphics.PointF;

public class Fingerprint {
    int mId;
    String mMap;
	PointF mLocation;
	HashMap<String, Integer> mMeasurements;
	
	
	
	/** CONSTRUCTORS */
	
	public Fingerprint() {
		mId = 0;
		mMap = "";
	}
	
	public Fingerprint(HashMap<String, Integer> measurements) {
	    this();
	    mMeasurements = measurements;
	}
	public Fingerprint(int id, String map, PointF location) {
	    this();
		mLocation = location;
	}
	
	public Fingerprint(int id, String map, PointF location, HashMap<String, Integer> measurements) {
	    this(id, map, location);
		mMeasurements = measurements;
	}
	
	
	
	/** INSTANCE METHODS*/
	
	public void setId(int id) {
	    mId = id;
	}
	
	public int getId() {
	    return mId;
	}
	
	public void setMap(String map) {
	    mMap = map;
	}
	
	public String getMap() {
	    return mMap;
	}
	
	public void setLocation(PointF location) {
		mLocation = location;
	}
	
	public void setLocation(float x, float y) {
		mLocation = new PointF(x, y);
	}
	
	public PointF getLocation() {
		return mLocation;
	}
	
	public void setMeasurements(HashMap<String, Integer> measurements) {
		mMeasurements = measurements;
	}
	
	public HashMap<String, Integer> getMeasurements() {
		return mMeasurements;
	}
	
	/** calculates the (squared) euclidean distance to the given fingerprint */
	public float compare(Fingerprint fingerprint) {
		float result = 0f;
		
		HashMap<String, Integer> fingerprintMeasurements = fingerprint.getMeasurements();
		TreeSet<String> keys = new TreeSet<String>();
		keys.addAll(mMeasurements.keySet());
		keys.addAll(fingerprintMeasurements.keySet());
		
		for (String key : keys) {
			int value = 0;
			Integer fValue = fingerprintMeasurements.get(key);
			Integer mValue = mMeasurements.get(key);
			value = (fValue == null) ? -119 : (int) fValue;
			value -= (mValue == null) ? -119 : (int) mValue;
			result += value * value;
		}
		
		//result = FloatMath.sqrt(result); // squared euclidean distance is enough, this is not needed
		
		return result;
	}
	
	/** compares the fingerprint to a set of fingerprints and returns the fingerprint with the smallest euclidean distance to it */
	public Fingerprint getClosestMatch(ArrayList<Fingerprint> fingerprints) {
	    //long time = System.currentTimeMillis();
	    Fingerprint closest = null;
	    float bestScore = -1;
	    
	    if(fingerprints != null) {
    	    for(Fingerprint fingerprint : fingerprints) {
    	        float score = compare(fingerprint);
    	        if(bestScore == -1 || bestScore > score) {
    	            bestScore = score;
    	            closest = fingerprint;
    	        }
    	    }	        
	    }

	    //time = System.currentTimeMillis() - time;
	    //Log.d("time", "\n\n\n\n\n\ncalculation location took " + time + " milliseconds.");
	    return closest;
	}
}
