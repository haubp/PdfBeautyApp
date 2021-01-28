package opswat.com.util;

import android.content.Context;
import android.content.res.Resources;

import java.io.InputStream;

/**
 * Created by H. Len Vo on 9/29/18.
 */
public class ResourceUtils {
    public static  String loadResourceFile(Context context, int resId) {
        try {
            Resources res = context.getResources();
            InputStream in_s = res.openRawResource(resId);

            byte[] b = new byte[in_s.available()];
            in_s.read(b);
            return new String(b);
        } catch (Exception e) {
             e.printStackTrace();
        }
        return null;
    }
}
