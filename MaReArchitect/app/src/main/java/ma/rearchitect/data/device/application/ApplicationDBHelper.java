package ma.rearchitect.data.device.application;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;

import opswat.com.constant.MAContant;
import opswat.com.enums.ApplicationScanningStatus;
import opswat.com.network.model.application.Application;
import opswat.com.util.SharedPrefsUtils;

/**
 * Created by LenVo on 7/18/18.
 */

public class ApplicationDBHelper {
    private final String APPLICATION_PREF_FILE_KEY = "application_key";

    private JSONObject applicationsJson = new JSONObject();
    private HashMap<String, Application> mapApplications = new HashMap<>();
    private SharedPreferences sharedPref;
    private ApplicationScanningStatus applicationScanningStatus;
    private long lastTimeScanApplication;

    private Context context;
    private Gson gson = new Gson();

    public ApplicationDBHelper(Context context) {
        this.context = context;
        sharedPref = context.getSharedPreferences(APPLICATION_PREF_FILE_KEY, Context.MODE_PRIVATE);
        applicationScanningStatus = ApplicationScanningStatus.IDLE;
        lastTimeScanApplication = SharedPrefsUtils.getLongPreference(context, MAContant.LAST_TIME_SCAN_APPLICATION , 0);
    }

    public Application getApplication(String packageName, String version) {
        String key = packageName + version;
        String jsonObject = sharedPref.getString(key, null);
        if (jsonObject == null) {
            return null;
        }
        return gson.fromJson(jsonObject,Application.class);
    }

    public synchronized void saveApplication(Application application) {
        String key = application.getPackageName() + application.getVersion();
        String jsonObj = gson.toJson(application);
        SharedPreferences.Editor prefsEditor = sharedPref.edit();
        prefsEditor.putString(key, jsonObj);
        prefsEditor.apply();
    }

    public void resetAllResult() {
        if (sharedPref != null) {
            sharedPref.edit().clear().apply();
        }
    }

    public void setApplicationScanStatus(ApplicationScanningStatus status) {
        this.applicationScanningStatus = status;
    }

    public ApplicationScanningStatus getApplicationScanningStatus(){
        return this.applicationScanningStatus;
    }

    public long getLastTimeScanApplication() {
        return lastTimeScanApplication;
    }

    public void setLastTimeScanApplication(long lastTimeScanApplication) {
        this.lastTimeScanApplication = lastTimeScanApplication;
        SharedPrefsUtils.setLongPreference(context, MAContant.LAST_TIME_SCAN_APPLICATION, lastTimeScanApplication);
    }
}
