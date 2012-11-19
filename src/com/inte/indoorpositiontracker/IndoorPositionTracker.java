package com.inte.indoorpositiontracker;

import java.util.ArrayList;

import android.app.Application;

public class IndoorPositionTracker extends Application {
    private ArrayList<Fingerprint> mFingerprints;
    
    private FingerprintDatabaseHandler mFingerprintDatabaseHandler;
    
    
    
    /** INSTANCE METHODS */
    
    @Override
    public void onCreate() {
        super.onCreate();
        mFingerprints = new ArrayList<Fingerprint>();
        //deleteDatabase("fingerprints"); 
        mFingerprintDatabaseHandler = new FingerprintDatabaseHandler(this);
        loadFingerprintsFromDatabase();
    }
    
    public void loadFingerprintsFromDatabase() {
        mFingerprints = mFingerprintDatabaseHandler.getAllFingerprints(); // fetch fingerprint data from the database
    }
    
    public ArrayList<Fingerprint> getFingerprintData() {
        return mFingerprints;
    }
    
    public void addFingerprint(Fingerprint fingerprint) {
        mFingerprints.add(fingerprint); // add to fingerprint arraylist
        mFingerprintDatabaseHandler.addFingerprint(fingerprint); // add to database
    }
    
    public void deleteAllFingerprints() {
        mFingerprints.clear(); // delete all fingerprints from arraylist
        mFingerprintDatabaseHandler.deleteAllFingerprints(); // delete all fingerprints from database
    }
}
