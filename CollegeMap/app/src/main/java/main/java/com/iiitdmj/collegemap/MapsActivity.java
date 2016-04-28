package main.java.com.iiitdmj.collegemap;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int signalStrength;
    private String strengthWiFi = "";
    private int rssi;
    public double currentX = 0.0;
    public double currentY = 0.0;
    public String response;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sendDataToServer();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // Add a marker in Sydney and move the camera
        LatLng latLng = new LatLng(23.1783 +currentX, 80.0254 + currentY);
        Log.i("TAG CurrentX","23.1783\t" + currentX);
        Log.i("TAG CurrentY","80.0254\t" + currentY);
        mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in IIITDMJ"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));
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
            System.out.println("Level is " + level + " out of 5");

        }

        Toast.makeText(getApplicationContext(), "signal strength is " + signalStrength + "Strength" + strengthWiFi + "see this " + rssi,
                Toast.LENGTH_LONG).show();

        String json = "";

        try {
            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("type", "data");
            jsonObject.accumulate("ss", strengthWiFi);
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
                pw.close();
                socket = new Socket("172.27.16.19",11111);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                response = in.readLine();
                Log.i("TAG1",response);

                int[] arr = convert(response);

                /*currentX = 0;
                currentY = 0;*/
                currentX = arr[0];
                currentY = arr[1];
                Log.i("TAG CurrentX from Map","23.1783\t" + currentX);
                Log.i("TAG CurrentY from Map","80.0254\t" + currentY);

            } catch (Exception e) {

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


    int[] convert(String response){
        String[] val = response.split(" ");
        int[] arr = new int[val.length];
        for (int i = 0; i < val.length; ++i){
            arr[i] = Integer.parseInt(val[i]);
        }
        return arr;
    }
}
