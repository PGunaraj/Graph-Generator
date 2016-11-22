package com.example.priya.graphgeneration;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created by rahulrao on 9/23/16.
 */
public class sensorHandlerClass extends Service implements SensorEventListener {
    private SensorManager accelManage;
    private Sensor senseAccel;
    SQLiteDatabase db;
    String str;
    float accelValuesX[] = new float[128];
    float accelValuesY[] = new float[128];
    float accelValuesZ[] = new float[128];
    int index = 0;
    Bundle b;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // TODO Auto-generated method stub

        Sensor mySensor = sensorEvent.sensor;


        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                index++;
                accelValuesX[index] = sensorEvent.values[0];
                accelValuesY[index] = sensorEvent.values[1];
                accelValuesZ[index] = sensorEvent.values[2];


                if (index >= 10) {
                    index = 0;
                    accelManage.unregisterListener(this);
                    final ContentValues contentValues = new ContentValues();
                    contentValues.put("AccDataX", accelValuesX[3]);
                    contentValues.put("AccDataY", accelValuesY[3]);
                    contentValues.put("AccDataZ", accelValuesZ[3]);
                    contentValues.put("timeStamp", System.currentTimeMillis());
                    long timestamp = System.currentTimeMillis();

                    try {
                        db = getApplicationContext().openOrCreateDatabase("/data/data/com.example.rahulrao.graphgeneration_assignment1/databases/Rahulrao.db",MODE_APPEND, null);
                        db.execSQL("insert into " + str + "(AccDataX, AccDataY, AccDataZ, timestamp) values ('" + accelValuesX[3] + "', '" + accelValuesY[3] + "', '" + accelValuesZ[3] + "', '" + timestamp + "' );");
                    } catch (SQLiteException e) {
                        Toast.makeText(sensorHandlerClass.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    accelManage.registerListener(this, senseAccel, SensorManager.SENSOR_DELAY_NORMAL);
                }
            }
        }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onCreate(){
        Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();
        accelManage = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senseAccel = accelManage.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelManage.registerListener(this, senseAccel, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        str = intent.getExtras().getString("str");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }



}
