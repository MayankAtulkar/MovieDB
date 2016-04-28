package main.java.com.iiitdmj.locator.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import main.java.com.iiitdmj.locator.R;

public class HomeActivity extends Activity implements SensorEventListener {

    /***************** New *******************/
    Sensor accelerometer;
    SensorManager sensorManager;
    TextView acceleration;
    /***************** New *******************/

    private Button submit;
    private Button map;
    private EditText xEditText, yEditText, orientationEditText;
    private Intent intent;
    private Context context;
    private float xCoordinate, yCoordinate;
    private String orientation;
    private String log;
    private String activityName;
    private String timeStamp;
    private int strengthInPercentage;
    private int signalStrength;
    private String strengthWiFi = "";

    private int rssi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        context = getApplicationContext();

        activityName = "HomeActivity";



        /***************** New *******************/
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        acceleration = (TextView)findViewById(R.id.textView3);

        map = (Button) findViewById(R.id.googleMap);

        /***************** New *******************/



        submit = (Button) findViewById(R.id.submit);
        xEditText = (EditText) findViewById(R.id.editText1);
        yEditText = (EditText) findViewById(R.id.editText2);
        //orientationEditText = (EditText) findViewById(R.id.editText3);

    }


    @Override
    protected void onStart() {

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(HomeActivity.this, MapsActivity.class);
                startActivity(i);

            }
            });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                if (wifiManager.isWifiEnabled()) {
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    if (wifiInfo != null) {
                        signalStrength = wifiInfo.getRssi();
                        //To get strength in percentage
                        //strengthInPercentage = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 100);
                    }

                    strengthWiFi = "{";
                    List<ScanResult> wifiList = wifiManager.getScanResults();
                    for (ScanResult scanResult : wifiList) {
                        //int strength = wifiInfo.getRssi();
                        //int level = WifiManager.calculateSignalLevel(scanResult.level, 5);
                        //System.out.println("Level is " + level + " out of 5");
                        if (strengthWiFi == "{") {
                            strengthWiFi = strengthWiFi + "\'" + scanResult.SSID + "\'" + ":" + scanResult.level;
                        } else {
                            strengthWiFi = strengthWiFi + "," + "\'" + scanResult.SSID + "\'" + ":" + scanResult.level;
                        }

                    }

                    strengthWiFi = strengthWiFi + "}";
                    // Level of current connection
                    rssi = wifiManager.getConnectionInfo().getRssi();
                    int level = WifiManager.calculateSignalLevel(rssi, 5);
                    System.out.println("Level is " + level + " out of 5");

                }


                timeStamp = FileOperations.timeStamp();


                Bundle extras = new Bundle();
                intent = new Intent(context, Communicator.class);


                xCoordinate = Float.valueOf(xEditText.getText().toString());
                yCoordinate = Float.valueOf(yEditText.getText().toString());
                orientation = orientationEditText.getText().toString();

                Toast.makeText(getApplicationContext(), "x is " + xCoordinate + "y is " + yCoordinate + "time is "
                                + timeStamp + "signal strength is " + signalStrength + "Strength" + strengthWiFi + "see this " + rssi,
                        Toast.LENGTH_LONG).show();

                String json = "";

                try {
                    // 3. build jsonObject
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("type", "data");
                    jsonObject.accumulate("x", xCoordinate);
                    jsonObject.accumulate("y", yCoordinate);
                    //jsonObject.accumulate("dir", orientation);
                    jsonObject.accumulate("ss", strengthWiFi);
                    // 4. convert JSONObject to JSON to String
                    json = jsonObject.toString();

                    new JsonAsyncTask().execute(json);

                } catch (Exception e) {

                    Toast.makeText(getApplicationContext(), "error " + e,
                            Toast.LENGTH_LONG).show();

                }

            }
        });


    super.onStart();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        acceleration.setText("Acceleration\nX:    "+event.values[0]+
                "\nY:   "+event.values[1]+
                "\nZ:   "+event.values[2]);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private class JsonAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {


            String json = urls[0];

            try {

                Socket socket = new Socket("172.27.16.19", 12345);

                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                pw.println(json);
                pw.close();
                socket.close();

            } catch (Exception e) {

                Toast.makeText(getApplicationContext(), "error " + e,
                        Toast.LENGTH_LONG).show();

            }

            return "hii";
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}