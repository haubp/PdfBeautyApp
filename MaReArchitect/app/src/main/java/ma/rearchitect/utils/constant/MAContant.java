package opswat.com.constant;

public class MAContant {
    public final static int INTERVAL_REPORT = 180000; //180s
    public final static int INTERVAL_FETCH_CONFIG = 60000; //60s
    public final static int INTERVAL_IM_HERE = 60000; //60s

    public final static int INTERVAL_SCAN_CONNECTION = 10000; //10s
    public final static int INTERVAL_SCAN_APPLICATION = 10000; //10s
    public final static int INTERVAL_KEEP_SCAN_APPLICATION_RESULT = 3600 * 1000; //1 hour

    //Request code on Application
    public final static int REQUEST_CODE_ENROLL = 1000;

    //Request code on System Action
    public final static int REQUEST_CODE_READ_STORAGE = 2000;
    public final static int REQUEST_CODE_WRITE_STORAGE = 2001;

    //Constant custom check
    public final static int CPU_USAGE_WARNING = 90;
    public final static int RAM_USAGE_WARNING = 90;
    public final static int STORAGE_USAGE_WARNING = 90;
    public final static long PERFORMANCE_CHECK_INTERVAL = 1800000;

    //Cloud Code Request
    public final static int COMMAND_CODE_DELETE = 802;
    public final static int COMMAND_CODE_REIDENTITY = 807;

    public final static String SERVER_URL = "https://gears.opswat.com/";
    public final static String DEVICE_TYPE = "phone";
    public final static String OS_FAMILY = "android";
    public final static String OS_VENDOR = "Google Inc.";

    public final static String REG_CODE_MARKETING = "7JHFDD2S";
    public final static String EMAIL_MARKETING = "marketing@opswat.com";
    public final static String UNKNOWN_TEXT = "N/A";
    public final static String WARNING_STATUS = "warning";
    public final static String CRITICAL_STATUS = "critical";

    public final static String API_FETCH_PRIVACY = "gears/api/2/agent/fetch_privacy";
    public final static String API_REGISTER = "api/v2/accounts/devices/registration_code";
    public final static String API_GET_ACCOUNT_INFO = "api/v2/accounts/";
    public final static String API_DELETE_DEVICE = "api/v2/accounts/:license_key/devices/:device_id";
    public final static String API_POLICY_CHECK = "api/v2/accounts/:license_key/devices/:device_id/policy_check";
    public final static String API_FETCH_CONFIG = "api/v2/accounts/:license_key/devices/:device_id/config";
    public final static String API_REPORT = "api/v2/accounts/:license_key/devices/:device_id/report/health";
    public final static String API_IM_HERE = "gears/api/1/agent/imhere";
    public final static String API_RESULT = "gears/api/1/agent/result";

    //Logger
    public static final String LOGGER_FILE = "meta_access_log.json";

    //Deeplink + Universal link
    public static final String UNIVERSAL_LINK_KEY = "universal";
    public static final String TRANSACTION_ID_KEY = "transaction_id";
    public static final String APP_ID_KEY = "app_id";
    public static final String URL_REDIRECT_KEY = "redirect_url";
    public static final String APP_NAME_KEY = "app_name";
    public static final String REG_CODE_KEY = "reg_code";
    public static final String GROUP_ID_KEY = "group_id";

    //Setting
    public static final String SETTING_NOTIFI_INSTALL_APP = "ma_notify_installed_app";
    public static final String SETTING_NOTIFY_INSTALL_APP = "notify_installed_app";
    public static final String SETTING_DEBUG_LOG_KEY = "debug_log";
    public static final String CONTENT_DEBUG_LOG_KEY = "content_debug_log";
    public static final int MAX_DEBUG_LOG_LENGTH = 40;
    public static final String SETTING_NOTIFY_REMINDER = "notify_reminder";
    public static final String SETTING_REMINDER_TIME = "reminder_time" ;

    //Last time scan application
    public static final String LAST_TIME_SCAN_APPLICATION = "last_time_scan_application";

    //Eula Text
    public static final String EULA_PAGE_KEY = "eula_text";

    //Install referrer
    public static final String FIRST_RUN = "first_run";

    //JOB ID for background service
    public static final int PERFORMANCE_CHECK_JOB_ID = 1000;
    public static final int REMINDER_JOB_ID = 1001;
}
