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
    
    public ArrayList<Fingerprint> getFingerprintData(String map) {
        ArrayList<Fingerprint> fingerprints = new ArrayList<Fingerprint>();
        for(Fingerprint fingerprint : mFingerprints) {
            if(fingerprint.getMap().compareTo(map) == 0) {
                fingerprints.add(fingerprint);
            }
        }
        
        return fingerprints;
    }
    
    public void addFingerprint(Fingerprint fingerprint) {
        mFingerprints.add(fingerprint); // add to fingerprint arraylist
        mFingerprintDatabaseHandler.addFingerprint(fingerprint); // add to database
    }
    
    public void deleteAllFingerprints() {
        mFingerprints.clear(); // delete all fingerprints from arraylist
        mFingerprintDatabaseHandler.deleteAllFingerprints(); // delete all fingerprints from database
    }
    
    public void deleteAllFingerprints(String map) {
        ArrayList<Fingerprint> itemsToRemove = new ArrayList<Fingerprint>();
        
        // collect fingerprints that need to be deleted
        for(Fingerprint fingerprint : mFingerprints) {
            if(fingerprint.getMap().compareTo(map) == 0) {
                itemsToRemove.add(fingerprint);
            }
        }
        
        // delete collected fingerprints
        for(Fingerprint fingerprint : itemsToRemove) {
            mFingerprintDatabaseHandler.deleteFingerprint(fingerprint); // delete from database
            mFingerprints.remove(fingerprint); // delete from arraylist
        }
    }
}
