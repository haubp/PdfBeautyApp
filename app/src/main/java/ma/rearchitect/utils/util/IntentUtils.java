package opswat.com.util;

import android.content.Context;
import android.content.Intent;

public class IntentUtils {
    public static boolean startActivity(Context context, String strIntent){
        try {
            Intent intent = new Intent(strIntent);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
