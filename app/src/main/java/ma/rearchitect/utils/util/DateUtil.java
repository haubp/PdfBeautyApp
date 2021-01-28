package opswat.com.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by LenVo on 7/18/18.
 */

public class DateUtil {
    private final static String NORM_DATE_PATTERN = "MM/dd/yyyy";
    private final static String FULL_DATE_PATTERN = "LLLL d, yyyy 'at' HH:mm '('ZZZZ')'";
    private final static String FULL_DATE_LOG_PATTERN = "LLLL d, yyyy 'at' HH:mm:ss '('ZZZZ')'";
    private final static SimpleDateFormat NORM_DATE_FORMAT = new SimpleDateFormat(NORM_DATE_PATTERN, new Locale("en"));
    private final static SimpleDateFormat FULL_DATE_FORMAT = new SimpleDateFormat(FULL_DATE_PATTERN, new Locale("en"));
    private final static SimpleDateFormat FULL_DATE_LOG_FORMAT = new SimpleDateFormat(FULL_DATE_LOG_PATTERN, new Locale("en"));

    public static String getFormatDate(long time){
        Date date = new Date(time);
        return NORM_DATE_FORMAT.format(date);
    }

    public static String getFullFormatDate(long time){
        Date date = new Date(time);
        return FULL_DATE_FORMAT.format(date);
    }

    public static String getFullFormatDateLog(long time){
        Date date = new Date(time);
        return FULL_DATE_LOG_FORMAT.format(date);
    }
}
