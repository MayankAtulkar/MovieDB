package main.java.com.iiitdmj.collegemap;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Message;
import android.os.Handler.Callback;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class Direction extends AppCompatActivity implements SensorEventListener {

    Sensor accelerometer;
    SensorManager sensorManager;
    TextView acceleration;
    private Intent intent;
    private Context context;
    private int signalStrength;
    private String strengthWiFi = "";
    private int rssi;
    private Button N;
    private Button S;
    private Button E;
    private Button W;
    private Button NW;
    private Button SE;
    private Button NE;
    private Button WS;

    private Button Stop;
    private String direction = "N";
    private double current_X;
    private double current_Y;
    private double stepSize=0.00000896057;
    private double stepSize1 = 1.414* stepSize;

    public double accelerationX = 0.0;
    public double accelerationY = 0.0;
    public double accelerationZ = 0.0;
    public boolean stopPressed = false;
    private Handler mHandler;

//0.00000896057 degree in 1 meter distance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);


        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        //acceleration = (TextView) findViewById(R.id.textView3);

        N = (Button) findViewById(R.id.N);
        S = (Button) findViewById(R.id.S);
        E = (Button) findViewById(R.id.E);
        W = (Button) findViewById(R.id.W);
        NE = (Button) findViewById(R.id.NE);
        SE = (Button) findViewById(R.id.SE);
        WS = (Button) findViewById(R.id.WS);
        NW = (Button) findViewById(R.id.NW);

        Stop = (Button) findViewById(R.id.stop);


        mHandler = new Handler();
        mHandler.post(mUpdate);
    }

    private Runnable mUpdate = new Runnable() {
        public void run() {

            if (ISAccelerating() == true) {

                if (direction == "N") {

                    current_Y = current_Y + stepSize;
                } else if (direction == "W") {

                    current_X = current_X - stepSize;
                } else if (direction == "S") {

                    current_Y = current_Y - stepSize;
                } else if (direction == "E") {

                    current_X = current_X + stepSize;
                } else if ( direction == "NW" )
                {
                    current_Y += stepSize1;
                    current_X -= stepSize1;
                }
                else if ( direction == "NE")
                {
                    current_Y += stepSize1;
                    current_X += stepSize1;
                }
                else if ( direction == "WS")
                {
                    current_Y -= stepSize1;
                    current_X -= stepSize1;
                }
                else if( direction == "SE")
                {
                    current_Y -= stepSize1;
                    current_X += stepSize1;
                }

                sendDataToServer();

            }
            mHandler.postDelayed(this, 5000);
        }
    };
    @Override
    protected void onStart() {


        N.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                direction = "N";
                current_Y = current_Y + stepSize;
                Log.i("TAG","\tDirection N");
                sendDataToServer();;
            }
        });

        S.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //signalStrengthFunction();
                direction = "S";
                Log.i("TAG","\tDirection S");
                current_Y = current_Y - stepSize;
                sendDataToServer();;
            }
        });

        E.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //signalStrengthFunction();
                direction = "E";
                Log.i("TAG","\tDirection E");
                current_X = current_X + stepSize;
                sendDataToServer();;
            }
        });

        W.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                direction = "W";
                Log.i("TAG","\tDirection W");

                current_X = current_X - stepSize;
                sendDataToServer();
            }
        });


        Stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("TAG","\tStop button pressed");

                stopPressed = true;


            }
        });


        /*
        Log.i("TAG","\tReached before while loop");
        current_X = 0.0;
        current_Y = 0.0;
        direction = "N";

        while (stopPressed == false) {

            Log.i("TAG","\tReached inside while loop");
                SystemClock.sleep(5000);
                Log.i("TAG","\tSlept for 5 seconds");



            if (ISAccelerating() == true) {
                Log.i("TAG","\tInside IsAccelerating loop");
                if (direction == "N") {

                    current_Y = current_Y + stepSize;
                    Log.i("TAG",current_X + "");
                } else if (direction == "W") {

                    current_X = current_X - stepSize;
                } else if (direction == "S") {

                    current_Y = current_Y - stepSize;
                } else if (direction == "E") {

                    current_X = current_X + stepSize;
                }

            }
        }*/

        super.onStart();



    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        accelerationX = event.values[0];
        accelerationY = event.values[1];
        accelerationZ = event.values[2];
/*
        acceleration.setText("Acceleration\nX:    " + event.values[0] +
                "\nY:   " + event.values[1] +
                "\nZ:   " + event.values[2]);*/

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected void sendDataToServer(){

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
            //xCoordinate = Float.valueOf(xEditText.getText().toString());
            //yCoordinate = Float.valueOf(yEditText.getText().toString());
            System.out.println("Level is " + level + " out of 5");

        }

        Toast.makeText(getApplicationContext(), "x is " + current_X + ",y is " + current_Y + ",Signal Strength" + strengthWiFi,
                Toast.LENGTH_LONG).show();

        String json = "";

        try {
            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("type", "data");
            jsonObject.accumulate("ss", strengthWiFi);
            jsonObject.accumulate("x", current_X);
            jsonObject.accumulate("y", current_Y);
            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            new JsonAsyncTask().execute(json);

        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), "error " + e,
                    Toast.LENGTH_LONG).show();

        }


    }

    private class JsonAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {


            String json = urls[0];

            try {

                Socket socket = new Socket("172.27.16.19", 12345);


                OutputStream out = socket.getOutputStream();

                PrintWriter pw = new PrintWriter(out);
                Log.i("TAG2",json);
                pw.println(json);
                //out.flush();
                //out.close();
                //pw.flush();
                pw.close();
                socket = new Socket("172.27.16.19",11111);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String response = in.readLine();
                Log.i("TAG1",response);

                //Toast.makeText(getApplicationContext(), "TAG " + response,
                //socket.close();


            } catch (Exception e) {

               /* Toast.makeText(getApplicationContext(), "error " + e,
                        Toast.LENGTH_LONG).show();
*/
                Log.i("TAG",e.toString());
            }

            return "hii";
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
        }
    }


    protected boolean ISAccelerating(){

        if ( Math.abs(accelerationX-0.0) > 1.0 || Math.abs(accelerationY-0.0) > 1.0 )
            return true;
        else
            return false;
    }


}