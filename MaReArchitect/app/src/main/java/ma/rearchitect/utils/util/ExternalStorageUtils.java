package opswat.com.util;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

/**
 * Created by H. Len Vo on 8/23/18.
 */
public class ExternalStorageUtils {
    private final static String FILE_NAME_APPLICATION = "com.opswat.gears";

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean saveTextToFile(String data, String filePath) {
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/" + FILE_NAME_APPLICATION);
            myDir.mkdirs();
            File dataFile = new File(myDir, filePath);
            FileOutputStream fos = new FileOutputStream(dataFile);
            fos.write(data.getBytes());
            fos.close();
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public static String getApplicationDir() {
        String root = Environment.getExternalStorageDirectory().toString();
        return root + "/" + FILE_NAME_APPLICATION;
    }

    public static String loadTextFromFile(String filePath) {
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/" + FILE_NAME_APPLICATION);
            myDir.mkdirs();
            File dataFile = new File(myDir, filePath);
            FileInputStream fis = new FileInputStream(dataFile);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(in));
            String strLine;
            String result = "";
            while ((strLine = br.readLine()) != null) {
                result = result + strLine;
            }
            in.close();
            return result;
        } catch (Exception e) {
            return null;
        }

    }

    public static void deleteFile(String filePath) {
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/" + FILE_NAME_APPLICATION);
            File dataFile = new File(myDir, filePath);
            dataFile.delete();
        } catch (Exception e) {
        }
    }
}
