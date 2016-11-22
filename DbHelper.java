package com.example.priya.graphgeneration;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;

/**
 * Created by rahulrao on 9/22/16.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static DbHelper mInstance = null;

    private static final int DATABASE_VERSION = 1;

    private static final String FILE_DIR = Environment.getExternalStorageDirectory().getPath();

    private static final String DATABASE_NAME = "GraphGeneration";

    private String TABLE_PATIENT_DETAIL;


    public DbHelper(Context context) {
        //super(context, DATABASE_NAME, null, DATABASE_VERSION);
       // public DatabaseHelper(final Context context) {
            super(context, Environment.getExternalStorageDirectory()
                    + File.separator + FILE_DIR
                    + File.separator + DATABASE_NAME, null, DATABASE_VERSION);
        }


    public static DbHelper getInstance(Context activityContext) {

        // Get the application context from the activityContext to prevent leak
        if (mInstance == null) {
            mInstance = new DbHelper (activityContext.getApplicationContext());
        }
        return mInstance;
    }
    public void setTABLE_PATIENT_DETAIL(String TABLE_PATIENT_DETAIL) {
        this.TABLE_PATIENT_DETAIL = TABLE_PATIENT_DETAIL;
    }

    public String getTABLE_PATIENT_DETAIL() {
        return TABLE_PATIENT_DETAIL;
    }
    String s = getTABLE_PATIENT_DETAIL();
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+s +"(AccDataX REAL,AccDataY REAL,AccDataZ REAL,timeStamp TEXT);");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS" + s);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}




