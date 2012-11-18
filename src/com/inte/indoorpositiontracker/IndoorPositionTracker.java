package com.inte.indoorpositiontracker;

import java.util.ArrayList;

import android.app.Application;

public class IndoorPositionTracker extends Application {
    private ArrayList<Fingerprint> mFingerprints;
    
    private FingerprintDatabaseHandler mFingerprintDatabaseHandler;
    
    
    @Override
    public void onCreate() {
        super.onCreate();
        mFingerprints = new ArrayList<Fingerprint>();
        //deleteDatabase("fingerprints"); 
        mFingerprintDatabaseHandler = new FingerprintDatabaseHandler(this);
        loadFingerprintsFromDatabase();
    }
    
    public void loadFingerprintsFromDatabase() {
        mFingerprints = mFingerprintDatabaseHandler.getAllFingerprints();
    }
    
    public ArrayList<Fingerprint> getFingerprintData() {
        return mFingerprints;
    }
    
    public void addFingerprint(Fingerprint fingerprint) {
        mFingerprints.add(fingerprint); // add to fingerprint arraylist
        mFingerprintDatabaseHandler.addFingerprint(fingerprint); // add to database
    }
}
