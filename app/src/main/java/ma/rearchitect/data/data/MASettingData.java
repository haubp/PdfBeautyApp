package ma.rearchitect.data.data;

import android.content.Context;
import android.content.SharedPreferences;

import opswat.com.constant.MAContant;
import opswat.com.enums.ConfigStatus;

/**
 * Created by H. Len Vo on 9/12/18.
 */
public class MASettingData {
    private static SharedPreferences sharedPref;
    private ConfigStatus notifyInstalledApp;
    private boolean enableDebugLog;
    private boolean notifyReminder;
    private int reminderTime;

    public MASettingData(Context context) {
        String MA_SETTING_FILE = "ma_file_setting";
        sharedPref = context.getSharedPreferences(MA_SETTING_FILE, Context.MODE_PRIVATE);
        loadSettingData();
    }

    public void loadSettingData() {
        if(sharedPref.getBoolean(MAContant.SETTING_NOTIFI_INSTALL_APP, true) == false){
            this.notifyInstalledApp = ConfigStatus.USER_UNSELECTED_CONFIG;
            SharedPreferences.Editor prefsEditor = sharedPref.edit();
            prefsEditor.putInt(MAContant.SETTING_NOTIFY_INSTALL_APP, notifyInstalledApp.getValue());
            prefsEditor.remove(MAContant.SETTING_NOTIFI_INSTALL_APP);
            prefsEditor.apply();
        }
        else {
            this.notifyInstalledApp = ConfigStatus.getConfigStatus(sharedPref.getInt(MAContant.SETTING_NOTIFY_INSTALL_APP, 1));
        }
        this.enableDebugLog = sharedPref.getBoolean(MAContant.SETTING_DEBUG_LOG_KEY, false);
        this.notifyReminder = sharedPref.getBoolean(MAContant.SETTING_NOTIFY_REMINDER, true);
        this.reminderTime = sharedPref.getInt(MAContant.SETTING_REMINDER_TIME, 7);
    }

    public ConfigStatus getNotifyInstalledApp() {
        return notifyInstalledApp;
    }

    public boolean isEnableDebugLog() {
        return enableDebugLog;
    }

    public boolean isNotifyReminder() {
        return notifyReminder;
    }

    public int reminderTime(){
        return reminderTime;
    }

    public void updateSetting(ConfigStatus notifyInstalledApp, boolean enableDebugLog , boolean notifyReminder, int reminderTime) {
        this.notifyInstalledApp = notifyInstalledApp;
        SharedPreferences.Editor prefsEditor = sharedPref.edit();
        prefsEditor.putInt(MAContant.SETTING_NOTIFY_INSTALL_APP, notifyInstalledApp.getValue());
        prefsEditor.putBoolean(MAContant.SETTING_DEBUG_LOG_KEY, enableDebugLog);
        prefsEditor.putBoolean(MAContant.SETTING_NOTIFY_REMINDER, notifyReminder);
        prefsEditor.putInt(MAContant.SETTING_REMINDER_TIME, reminderTime);
        prefsEditor.apply();
    }

    public void updateNotifyInstalledApp(ConfigStatus notifyInstalledApp) {
        if (this.notifyInstalledApp == ConfigStatus.USER_SELECTED_CONFIG || this.notifyInstalledApp == ConfigStatus.USER_UNSELECTED_CONFIG)
            return;
        SharedPreferences.Editor prefsEditor = sharedPref.edit();
        prefsEditor.putInt(MAContant.SETTING_NOTIFY_INSTALL_APP, notifyInstalledApp.getValue());
        prefsEditor.apply();
    }
}
