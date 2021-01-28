package opswat.com.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.ColorInt;

/**
 * Created by LenVo on 7/12/18.
 */

public class ColorUtil {
    @ColorInt
    public static int getColor(Context context, int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(resId);
        } else {
            return context.getResources().getColor(resId);
        }
    }
}
