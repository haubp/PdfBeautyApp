package opswat.com.util;

import android.content.Context;
import android.graphics.Typeface;

import opswat.com.R;

/**
 * Created by LenVo on 7/15/18.
 */

public class FontUtil {
    public static Typeface regularTypeface(Context context) {
        return Typeface.createFromAsset(context.getAssets(),
                context.getString(R.string.font_regular));
    }

    public static Typeface mediumTypeface(Context context) {
        return Typeface.createFromAsset(context.getAssets(),
                context.getString(R.string.font_medium));
    }

    public static Typeface boldTypeface(Context context) {
        return Typeface.createFromAsset(context.getAssets(),
                context.getString(R.string.font_bold));
    }
}
