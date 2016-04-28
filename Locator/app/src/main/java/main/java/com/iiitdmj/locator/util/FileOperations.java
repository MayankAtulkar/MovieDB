package main.java.com.iiitdmj.locator.util;

import android.os.Environment;
import android.text.format.Time;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FileOperations {
    static Calendar calendar;

    public static void writeToFile(String fileName, String log) {
        File myFile = new File(Environment.getExternalStorageDirectory() + "/Experiment");
        if (!myFile.exists()) {
            myFile.mkdir();
        }

        File f = new File(Environment.getExternalStorageDirectory() + "/Experiment/" + fileName);
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fOut = new FileOutputStream(f, true);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(log + "\n");
            myOutWriter.close();
            fOut.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String timeStamp() {
        calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");
        String timeIs = sdf.format(calendar.getTime());
        return timeIs;
    }

    public static String dateStamp(){
        calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String dateIs = sdf.format(calendar.getTime());
        return dateIs;
    }
}
