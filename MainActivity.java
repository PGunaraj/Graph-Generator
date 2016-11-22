package com.example.priya.graphgeneration;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase db;
    Context context;
    String patSex;
    EditText patientName;
    EditText patientID;
    EditText patientAge;
    int patID;
    String patName;
    int patAge;
    String str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


       final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioSex);

        final Button btn_ok = (Button) findViewById(R.id.button_OK);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                patientName = (EditText) findViewById(R.id.PAT_NAME);
                if (!TextUtils.isEmpty(patientName.getText())) {
                    patName = patientName.getText().toString();
                }

                patientID = (EditText) findViewById(R.id.PAT_ID);
                try {
                    if (!TextUtils.isEmpty(patientID.getText())) {
                        patID = Integer.parseInt(patientID.getText().toString());
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                patientAge = (EditText) findViewById(R.id.PAT_AGE);
                try {
                    if (!TextUtils.isEmpty(patientAge.getText())) {
                        patAge = Integer.parseInt(patientAge.getText().toString());
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        RadioButton radioButton = (RadioButton) findViewById(selectedId);
                        if(radioButton != null)
                          patSex = radioButton.getText().toString();

                str = new StringBuffer().append(patName).append("_").append(patID).append("_").append(patAge).append("_").append(patSex).toString();


                if(patName != null && patSex != null && patAge != 0 && patID != -1){
                    try{
                        context = getApplicationContext();
                        db = getApplicationContext().openOrCreateDatabase("/data/data/com.example.rahulrao.graphgeneration_assignment1/databases/Rahulrao.db",context.MODE_APPEND,null);
                        db.beginTransaction();

                        try {
                            //perform your database operations here ...
                            db.execSQL("create table IF NOT EXISTS "+str+"("
                                    + " AccDataX REAL, "
                                    + " AccDataY REAL, "
                                    + " AccDataZ REAL, "
                                    + "timestamp TEXT ); " );

                            db.setTransactionSuccessful(); //commit your changes
                            Intent intent = new Intent(MainActivity.this, GraphActivity.class);

                            intent.putExtra("str", str);
                            startActivity(intent);

                            Intent serviceIntent;
                            serviceIntent = new Intent(MainActivity.this, sensorHandlerClass.class);
                            serviceIntent.putExtra("str", str);
                            startService(serviceIntent);

                        }
                        catch (SQLiteException e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        finally {
                            db.endTransaction();
                        }
                    }catch (Exception e){

                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                }

        });
    }
}
