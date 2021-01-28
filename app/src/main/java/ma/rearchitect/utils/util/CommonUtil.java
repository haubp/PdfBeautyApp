package opswat.com.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by LenVo on 7/15/18.
 */

public class CommonUtil {
    public static void openBrowser(Context context, String url) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(browserIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
