package opswat.com.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Created by H. Len Vo on 10/1/18.
 */
public class InternalStorageUtils {

    public static String loadTextFromFile(Context context, String filePath) {
        try {
            String data_internal = null;
            FileInputStream inputStream = context.openFileInput(filePath);
            if (inputStream != null) {
                InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(reader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                data_internal = stringBuilder.toString();
                bufferedReader.close();
                inputStream.close();
            }
            return data_internal;
        } catch (Exception e) {
            return null;
        }
    }

    public static void deleteFile(Context context, String filePath) {
        context.deleteFile(filePath);
    }
}
