package com.example.priya.graphgeneration;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class GraphActivity extends AppCompatActivity {

    int serverResponseCode = 0;
    ProgressDialog dialog = null;
    TextView messageText;
    String upLoadServerUri = null;
    String downloadServerUri = null;
    final float[] values = new float[10];
    final float[] values1 = new float[10];
    final float[] values2 = new float[10];
    final String uploadFilePath = "/data/data/com.example.rahulrao.graphgeneration_assignment1/databases/Rahulrao.db";
    final String downloadFilePath = "/data/data/com.example.rahulrao.graphgeneration_assignment1/databases/RahulraoDownload.db";
    int index = 0;
    SQLiteDatabase db;
    SQLiteDatabase db1;
    GraphView graphView;
    int downloadedSize = 0;
    int totalSize = 0;
    int flag = 0;
    Context context;
    String str;
    boolean speedController = true;
    boolean isDownloadbtn = false;
    List<Float> valX = new ArrayList<Float>();
    List<Float> valY = new ArrayList<Float>();
    List<Float> valZ = new ArrayList<Float>();
    float[] valXtemp = new float[20];
    float[] valYtemp = new float[20];
    float[] valZtemp = new float[20];
    String title = "Assignment 1";
    String[] horlabels = {"0", "3", "6", "9", "12", "15"};
    String[] verlabels = {"15", "12", "9", "6", "3", "0"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        Intent myIntent = getIntent();
        str = myIntent.getExtras().getString("str");
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        messageText = (TextView) findViewById(R.id.messageText);

        upLoadServerUri = "https://impact.asu.edu/CSE535Fall16Folder/UploadToServer.php";
        downloadServerUri = "https://impact.asu.edu/CSE535Fall16Folder/Rahulrao.db";


        graphView = new GraphView(this, values, values1, values2, title, horlabels, verlabels, true);

        Button buttonRun = (Button) findViewById(R.id.btn_RUN);
        buttonRun.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                graphView.invalidate();
                graphView.setVisibility(View.VISIBLE);
                runGraph();
            }
        });



        Button btn_upload = (Button) findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = ProgressDialog.show(GraphActivity.this, "", "Uploading file...", true);

                new Thread(new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                messageText.setText("uploading started.....");
                            }
                        });

                        uploadFile(uploadFilePath);

                    }
                }).start();
            }
        });

        final Button btn_download = (Button) findViewById(R.id.btn_download);
        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create Object and call AsyncTask execute Method
                new DownloadTask().execute(downloadServerUri);

            }

        });
        Button buttonStop = (Button) findViewById(R.id.btn_STOP);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                linearLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        graphView.setVisibility(View.GONE);
                        speedController = true;
                        graphView.invalidate();
                    }
                });
            }
        });
    }

    private void runGraph() {
            final Handler handler = new Handler();
            final Runnable updateGraph = new Runnable() {
                public void run() {
                    long currentTime = System.currentTimeMillis();
                    Context context = getApplicationContext();
                    String s = String.valueOf(context.getDatabasePath("graphRahul.db"));
                    final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
                    try {
                        db = getApplicationContext().openOrCreateDatabase("/data/data/com.example.rahulrao.graphgeneration_assignment1/databases/Rahulrao.db", MODE_APPEND, null);
                        Cursor c = db.rawQuery("select * from " + str + " ORDER BY timestamp DESC LIMIT 10", null);
                        if (c.moveToFirst()) {
                            while (!c.isAfterLast()) {
                                valX.add(c.getFloat(0));
                                valY.add(c.getFloat(1));
                                valZ.add(c.getFloat(2));
                                c.moveToNext();
                            }
                        }
                    }catch (SQLException e){
                        e.getMessage();
                    }
                    int i = 0;
                    int j = 0;
                    int k = 0;

                    int x = valX.size();
                    for (Float F : valX) {
                        values[i++] = (F != null ? F+2 : Float.NaN);
                    }

                    for (Float F1 : valY) {
                        values1[j++] = (F1 != null ? F1+2 : Float.NaN);
                    }

                    for (Float F2 : valZ) {
                        values2[k++] = (F2 != null ? F2 : Float.NaN);
                    }

                    if (graphView.getParent() != null)
                        ((ViewGroup) graphView.getParent()).removeView(graphView);
                    linearLayout.addView(graphView);
                    valX.clear();
                    valY.clear();
                    valZ.clear();
                    flag++;
                }
            };

            Timer timer = new Timer();
            if (speedController && !isDownloadbtn) {
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        handler.postAtFrontOfQueue(updateGraph);
                        speedController = false;
                    }
                }, 0, 1000);
            }

        }



    public int uploadFile(String sourceFileUri) {

        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

            dialog.dismiss();

            Log.e("uploadFile", "Source File not exist :"
                    + uploadFilePath);

            runOnUiThread(new Runnable() {
                public void run() {
                    messageText.setText("Source File not exist :" + uploadFilePath);
                }
            });

            return 0;

        } else {
            try {
                TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                }
                };

                try {
                    SSLContext sc = SSLContext.getInstance("TLS");

                    sc.init(null, trustAllCerts, new java.security.SecureRandom());

                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                // Open a HTTP  connection to  the URL
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);
                conn.connect();
                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=uploaded_file;filename=" + fileName + lineEnd + "");

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {

                    runOnUiThread(new Runnable() {
                        public void run() {

                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n";

                            messageText.setText(msg);
                            Toast.makeText(GraphActivity.this, "File Upload Complete.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        messageText.setText("MalformedURLException Exception : check script url.");
                        Toast.makeText(GraphActivity.this, "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        messageText.setText("Got Exception : see logcat ");
                        Toast.makeText(GraphActivity.this, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            dialog.dismiss();
            return serverResponseCode;

        } // End else block
    }


    private class DownloadTask extends AsyncTask<String, Void, Void> {

        InputStream is;

        private String Content;
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(GraphActivity.this);

        TextView uiUpdate = (TextView) findViewById(R.id.messageText);

        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            uiUpdate.setText("Output : ");
            Dialog.setMessage("Downloading source..");
            Dialog.show();
        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            try {

                    TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }
                    };

                    try {
                        SSLContext sc = SSLContext.getInstance("TLS");

                        sc.init(null, trustAllCerts, new java.security.SecureRandom());

                        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                    } catch (KeyManagementException | NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    URL url = new URL(urls[0]);
                    HttpURLConnection connection = null;
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true); // Allow Inputs
                    connection.setDoOutput(true); // Allow Outputs
                    connection.setUseCaches(false); // Don't use a Cached Copy
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.connect();
                    File file = new File(downloadFilePath);

                    FileOutputStream fileOutput = new FileOutputStream(file);

                    //Stream used for reading the data from the internet
                    InputStream inputStream = connection.getInputStream();

                    //create a buffer...
                    byte[] buffer = new byte[1024*1024];
                    int bufferLength = 0;

                    while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                        fileOutput.write(buffer, 0, bufferLength);
                        downloadedSize += bufferLength;
                    }
                fileOutput.close();
                //close the output stream when complete //


                     serverResponseCode = connection.getResponseCode();
                    String serverResponseMessage = connection.getResponseMessage();

                    Log.i("uploadFile", "HTTP Response is : "
                            + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(GraphActivity.this, "File download Complete.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                } catch (MalformedURLException e) {
                    Error = e.getMessage();
                    cancel(true);
                } catch (IOException e) {
                    Error = e.getMessage();
                    cancel(true);
                }
                return null;
            }


        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here
            // Close progress dialog
            Dialog.dismiss();
            isDownloadbtn = true;
            try{
                db = getApplicationContext().openOrCreateDatabase("/data/data/com.example.rahulrao.graphgeneration_assignment1/databases/RahulraoDownload.db",MODE_APPEND,null);
                db1 = getApplicationContext().openOrCreateDatabase("/data/data/com.example.rahulrao.graphgeneration_assignment1/databases/Rahulrao.db",MODE_APPEND,null);
                db.beginTransaction();
                Cursor c = db.rawQuery("select * from " + str + " ORDER BY timestamp DESC LIMIT 10", null);
                if (c.moveToFirst()) {
                    while (!c.isAfterLast() && index <= 10) {
                        valXtemp[index] = c.getFloat(0);
                        valYtemp[index] = c.getFloat(1);
                        valZtemp[index] = c.getFloat(2);
                        index++;
                        c.moveToNext();
                    }
                }

                db.endTransaction();
                for(int i =0; i<valXtemp.length;i++) {
                    db1.execSQL("insert into " + str + "(AccDataX, AccDataY, AccDataZ) values ('" + valXtemp[i] + "', '" + valYtemp[i] + "', '" + valZtemp[i] + "' );");

                }
                        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
                        Cursor c2 = db1.rawQuery("select * from " + str + " ORDER BY timestamp DESC LIMIT 10", null);
                        if (c2.moveToFirst()) {
                            while (!c2.isAfterLast()) {
                                valX.add(c2.getFloat(0));
                                valY.add(c2.getFloat(1));
                                valZ.add(c2.getFloat(2));
                                c2.moveToNext();
                            }
                        }
                        int i = 0;
                        int j = 0;
                        int k = 0;

                        int x = valX.size();
                        for (Float F : valX) {
                            values[i++] = (F != null ? F+2 : Float.NaN);
                        }

                        for (Float F1 : valY) {
                            values1[j++] = (F1 != null ? F1+2 : Float.NaN);
                        }

                        for (Float F2 : valZ) {
                            values2[k++] = (F2 != null ? F2 : Float.NaN);
                        }

                        if (graphView.getParent() != null)
                            ((ViewGroup) graphView.getParent()).removeView(graphView);
                        linearLayout.addView(graphView);
                        valX.clear();
                        valY.clear();
                        valZ.clear();


                isDownloadbtn = false;
                runGraph();


            }catch (SQLException e){
                Error = e.getMessage();
            }

            if (Error != null) {

                uiUpdate.setText("Output : " + Error);

            } else {

                uiUpdate.setText("Output : " + Content);

            }
        }


}
}













