package com.inte.indoorpositiontracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.PointF;

public class FingerprintDatabaseHandler extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "fingerprints";
    private static final String TABLE_MEASUREMENTS = "measurements";
    private static final String TABLE_FINGERPRINTS = "fingerprints";
 
    // measurements table columns names
    private static final String KEY_MEASUREMENT_ID = "id";
    private static final String KEY_FINGERPRINT = "fingerprint_id";
    private static final String KEY_BSSID = "bssid";
    private static final String KEY_LEVEL = "value";
    
    // fingerprintss table column names
    private static final String KEY_FINGERPRINT_ID = "id";
    private static final String KEY_MAP_NAME = "map_name";
    private static final String KEY_POSITION_X = "position_x";
    private static final String KEY_POSITION_Y = "position_y";
    
    
    
    /** CONSTRUCTORS */
    public FingerprintDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    
    
    /** INSTANCE METHODS */
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create tables
        
        String CREATE_MEASUREMENTS_TABLE = "CREATE TABLE " + TABLE_MEASUREMENTS + "("
                + KEY_MEASUREMENT_ID + " INTEGER PRIMARY KEY,"
                + KEY_FINGERPRINT + " INTEGER,"
                + KEY_BSSID + " TEXT,"
                + KEY_LEVEL + " INTEGER" + ")";
        db.execSQL(CREATE_MEASUREMENTS_TABLE);  
        
        String CREATE_FINGERPRINT_TABLE = "CREATE TABLE " + TABLE_FINGERPRINTS + "("
                + KEY_FINGERPRINT_ID + " INTEGER PRIMARY KEY,"
                + KEY_MAP_NAME + " TEXT,"
                + KEY_POSITION_X + " FLOAT,"
                + KEY_POSITION_Y + " FLOAT" + ")";
        
        db.execSQL(CREATE_FINGERPRINT_TABLE);  
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEASUREMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FINGERPRINTS);
        
        // Create tables again
        onCreate(db);
    }
    
    public void addFingerprint(Fingerprint fingerprint) {
        SQLiteDatabase db = this.getWritableDatabase();
        PointF location = fingerprint.getLocation();
        
        ContentValues fingerprintValues = new ContentValues();
        fingerprintValues.put(KEY_MAP_NAME, fingerprint.getMap());
        fingerprintValues.put(KEY_POSITION_X, location.x);
        fingerprintValues.put(KEY_POSITION_Y, location.y);
        
        // insert fingerprint into fingerprints table
        long fingerprintId = db.insert(TABLE_FINGERPRINTS, null, fingerprintValues);
        
        
        // insert measurements into measurements table
        if(fingerprintId != -1) {
            Map<String, Integer> measurements = fingerprint.getMeasurements();
            for(String key : measurements.keySet()) {
                int value = measurements.get(key);
                
                ContentValues measurementValues = new ContentValues();
                measurementValues.put(KEY_FINGERPRINT, fingerprintId);
                measurementValues.put(KEY_BSSID, key);
                measurementValues.put(KEY_LEVEL, value);
                db.insert(TABLE_MEASUREMENTS, null, measurementValues);
            }
        }

        db.close();
    }
    
    public Fingerprint getFingerprint(int id) {
        Fingerprint fingerprint = null;
        
        SQLiteDatabase db = this.getReadableDatabase();
        
        // SQL query
        Cursor cursor = db.query(TABLE_FINGERPRINTS,
                new String[] {KEY_FINGERPRINT_ID, KEY_MAP_NAME, KEY_POSITION_X, KEY_POSITION_Y},
                KEY_FINGERPRINT_ID + " = ?", new String[] { String.valueOf(id) },
                null, null, null, null);
                
        if (cursor.moveToFirst()) {
            // parse fingerprint data
            String map = cursor.getString(1);
            PointF location = new PointF(cursor.getFloat(2), cursor.getFloat(3));
            HashMap<String, Integer> measurements = getMeasurements(id);
            
            // create fingerprint
            fingerprint = new Fingerprint(id, map, location, measurements);
        }

        cursor.close();
        db.close();
        return fingerprint;
    }
    
    public ArrayList<Fingerprint> getAllFingerprints() {
        ArrayList<Fingerprint> fingerprints = new ArrayList<Fingerprint>();
        
        String SELECT_QUERY = "SELECT * FROM " + TABLE_FINGERPRINTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(SELECT_QUERY, null); // SQL query
     
        // loop through all fingerprint rows and add to list
        if (cursor.moveToFirst()) {
            do {
                // parse fingerprint data
                int id = cursor.getInt(0);
                String map = cursor.getString(1);
                PointF location = new PointF(cursor.getFloat(2), cursor.getFloat(3));
                
                HashMap<String, Integer> measurements = getMeasurements(id); // query measurements
                
                Fingerprint fingerprint = new Fingerprint(id, map, location, measurements); // create fingerprint
                
                fingerprints.add(fingerprint); // add to list returned fingerprints
            } while (cursor.moveToNext());
        }
     
        cursor.close();
        db.close();
        return fingerprints;
    }
     
    public HashMap<String, Integer> getMeasurements(int fingerprintId) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        // SQL query
        Cursor cursor = db.query(TABLE_MEASUREMENTS,
                new String[] {KEY_BSSID, KEY_LEVEL},
                KEY_FINGERPRINT + " = ?", new String[] { String.valueOf(fingerprintId) },
                null, null, null, null);
        
        HashMap<String, Integer> measurements = new HashMap<String, Integer>();
        
        // loop through all measurement rows and add to list
        if(cursor.moveToFirst()) {
            do {
                // parse measurement data
                String BSSID = cursor.getString(0);
                int level = cursor.getInt(1);
                        
                measurements.put(BSSID, level); // add to list
            } while(cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return measurements;
    }
    
    public int getFingerprintCount() {
        String COUNT_QUERY = "SELECT  * FROM " + TABLE_FINGERPRINTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(COUNT_QUERY, null); // SQL query
        cursor.close();
 
        db.close();
        return cursor.getCount();
    }
     
    public void deleteFingerprint(Fingerprint fingerprint) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // SQL query for deleting fingerprint
        db.delete(TABLE_FINGERPRINTS, KEY_FINGERPRINT_ID + " = ?",
                new String[] { String.valueOf(fingerprint.getId()) });
        
        // SQL query for deleting measurements linked to given fingerprint
        db.delete(TABLE_MEASUREMENTS, KEY_FINGERPRINT + " = ?",
                new String[] { String.valueOf(fingerprint.getId()) });
        
        db.close();
    }
    
    public void deleteAllFingerprints() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FINGERPRINTS, null, null); // delete all fingerprints
        db.delete(TABLE_MEASUREMENTS, null, null); // delete all measurements
        db.close();
    }
}
