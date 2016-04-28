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
import org.json.JSONObject;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class Direction extends AppCompatActivity implements SensorEventListener {

    Sensor accelerometer;
    SensorManager sensorManager;
    TextView acceleration;
    TextView timeView;
    TextView timeView2;
    private Intent intent;
    private Context context;
    private float xCoordinate, yCoordinate;
    private int signalStrength;
    private String strengthWiFi = "";
    private int rssi;
    private Button N;
    private Button S;
    private Button E;
    private Button W;
    private Button Stop;
    private float x_coordinate;
    private float y_coordinate;
    private long starttime = 0;
    private String direction;
    private double current_X;
    private double current_Y;
    private double stepSize=2.0;
    public double accelerationX = 0.0;
    public double accelerationY = 0.0;
    public double accelerationZ = 0.0;
    public boolean stopPressed = false;

/***************NEW New ***********************/
//this  posts a message to the main thread from our timertask
//and updates the textfield
    final Handler h = new Handler(new Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            long millis = System.currentTimeMillis() - starttime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds     = seconds % 60;

            acceleration.setText(String.format("%d:%02d", minutes, seconds));
            return false;
        }
    });
    //runs without timer be reposting self
    Handler h2 = new Handler();
    Runnable run = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - starttime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds     = seconds % 60;

            timeView2.setText(String.format("%d:%02d", minutes, seconds));

            h2.postDelayed(this, 500);
        }
    };

    //tells handler to send a message
    class firstTask extends TimerTask {

        @Override
        public void run() {
            h.sendEmptyMessage(0);
        }
    };

    //tells activity to run on ui thread
    class secondTask extends TimerTask {

        @Override
        public void run() {
            Direction.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    long millis = System.currentTimeMillis() - starttime;
                    int seconds = (int) (millis / 1000);
                    int minutes = seconds / 60;
                    seconds     = seconds % 60;

                    timeView.setText(String.format("%d:%02d", minutes, seconds));
                }
            });
        }
    };


    Timer timer = new Timer();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);


        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        acceleration = (TextView) findViewById(R.id.textView3);
        timeView = (TextView) findViewById(R.id.textView4);
        timeView2 = (TextView) findViewById(R.id.textView5);

        N = (Button) findViewById(R.id.N);
        S = (Button) findViewById(R.id.S);
        E = (Button) findViewById(R.id.E);
        W = (Button) findViewById(R.id.W);
        Stop = (Button) findViewById(R.id.stop);
        }

    @Override
    protected void onStart() {


        N.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (N.getText().equals("Stop")) {
                    timer.cancel();
                    timer.purge();
                    h2.removeCallbacks(run);
                    N.setText("N");
                    signalStrengthFunction();

                }else{
                    starttime = System.currentTimeMillis();
                    timer = new Timer();
                    timer.schedule(new firstTask(), 0,500);
                    timer.schedule(new secondTask(), 0, 500);
                    h2.postDelayed(run, 0);
                    N.setText("Stop");
                }*/
                //signalStrengthFunction();
                direction = "N";
                current_Y = current_Y + stepSize;
                Log.i("TAG","\tDirection N");
                sendDataToServer();;
            }
        });
        //super.onStart();

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
//        super.onStart();

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
//        super.onStart();

        W.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                direction = "W";
                Log.i("TAG","\tDirection W");

                current_X = current_X - stepSize;
                sendDataToServer();
                //signalStrengthFunction();
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
    public void onPause() {
        super.onPause();
        timer.cancel();
        timer.purge();
        h2.removeCallbacks(run);
        Button N = (Button)findViewById(R.id.N);
        N.setText("N");
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

        Toast.makeText(getApplicationContext(), "x is " + current_X + "y is " + current_Y + "time is "
                         + "signal strength is " + signalStrength + "Strength" + strengthWiFi + "see this " + rssi,
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

