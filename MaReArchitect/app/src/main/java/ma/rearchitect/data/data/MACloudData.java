package ma.rearchitect.data.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import opswat.com.constant.MAContant;
import opswat.com.network.model.response.FetchPrivacyResponse;
import opswat.com.util.ExternalStorageUtils;
import opswat.com.util.InternalStorageUtils;
import opswat.com.util.PermissionUtils;
import opswat.com.util.StringUtil;

public class MACloudData {
    private final String MA_CLOUD_PREF_FILE_KEY = "ma_cloud_data_file";
    private final String MA_CLOUD_DATA_KEY = "ma_cloud_data";
    private final String MA_CLOUD_CONFIG_KEY = "ma_cloud_config";
    private final String MA_CLOUD_LAST_CONNECTED = "ma_cloud_last_connected";
    private final String MA_CLOUD_LAST_IP_SCANNING = "ma_cloud_last_ip_scanning";
    private static SharedPreferences sharedPref;
    private Gson gson = new Gson();
    private AccountData accountData;
    private static final String MA_CLOUD_INFO_FILE = "opswat_metaaccess";
    private static final String TOKEN_INFO_FILE_OLD = "opswattenp";
    private long lastConnected;
    private boolean isOutOfToken = false;
    private long lastScanningIP;
    private FetchPrivacyResponse privacy;
    private String transactionId;
    private boolean initialized;

    public MACloudData(Context context) {
        sharedPref = context.getSharedPreferences(MA_CLOUD_PREF_FILE_KEY, Context.MODE_PRIVATE);
        initialized = false;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void loadMaCloudData(Context context) {
        //Get last connected from sharedPref
        this.lastConnected = sharedPref.getLong(MA_CLOUD_LAST_CONNECTED, 0);

        //Load old app:
        AccountData oldDataInternal = loadInternalOldApp(context);
        AccountData oldDataExternal = loadExternalOldApp();

        String dataInternal = sharedPref.getString(MA_CLOUD_DATA_KEY, null);
        //Get account data from storage
        String dataExternal = null;
        if (PermissionUtils.checkPermissionReadExternalStorage(context, false)) {
            dataExternal = ExternalStorageUtils.loadTextFromFile(MA_CLOUD_INFO_FILE);
        }

        initialized = true;
        if (oldDataInternal != null && dataInternal == null) {
            accountData = oldDataInternal;
            updateDataInternal();
            updateDataInternal();
            return;
        }

        if (oldDataInternal != null) {
            AccountData accountDataInternal = gson.fromJson(dataInternal, AccountData.class);
            if (!accountDataInternal.isRegistered()) {
                accountData = oldDataInternal;
                updateDataInternal();
                updateDataInternal();
                return;
            }
        }

        if (oldDataExternal != null && dataInternal == null) {
            accountData = oldDataExternal;
            updateDataInternal();
            updateDataInternal();
            return;
        }

        if (oldDataExternal != null) {
            AccountData accountDataInternal = gson.fromJson(dataInternal, AccountData.class);
            if (!accountDataInternal.isRegistered()) {
                accountData = oldDataExternal;
                Log.i("LogFile", accountData.toString());
                setAccountData(accountData);
                return;
            }
        }

        if (StringUtil.isEmpty(dataExternal) && StringUtil.isEmpty(dataInternal)) {
            accountData = new AccountData();
            return;
        }

        if (!StringUtil.isEmpty(dataExternal) && StringUtil.isEmpty(dataInternal)) {
            try {
                accountData = gson.fromJson(dataExternal, AccountData.class);
                return;
            } catch (Exception e) {
                accountData = new AccountData();
                return;
            }
        }

        if (StringUtil.isEmpty(dataExternal)) {
            accountData = gson.fromJson(dataInternal, AccountData.class);
            return;
        }

        AccountData accountInternal = gson.fromJson(dataInternal, AccountData.class);
        try {
            AccountData accountExternal = gson.fromJson(dataExternal, AccountData.class);
            if (accountInternal.getLastModified() > accountExternal.getLastModified()) {
                accountData = accountInternal;
                updateDataExternal();
            } else {
                accountData = accountExternal;
                updateDataInternal();
            }
        } catch (Exception e) {
            accountData = accountInternal;
            updateDataExternal();
        }
    }

    private AccountData loadExternalOldApp() {
        String dataInternal = ExternalStorageUtils.loadTextFromFile(TOKEN_INFO_FILE_OLD);
        Log.i("LOG_EXTERNAL_FILE", dataInternal == null ? "" : dataInternal);
        if (StringUtil.isEmpty(dataInternal)) {
            return null;
        }
        try {
            JsonParser parser = new JsonParser();
            JsonObject dataJson = parser.parse(dataInternal).getAsJsonObject();
            if (dataJson.has("owner_email")
                    && "marketing@opswat.com".equalsIgnoreCase(dataJson.get("owner_email").getAsString())) {
                return null;
            }

            AccountData accountData = new AccountData();
            accountData.setDeviceId(dataJson.get("device_id").getAsString());
            accountData.setLicenseKey(dataJson.get("license_key").getAsString());
            accountData.setMoKey(dataJson.get("mo_key").getAsString());
            accountData.setAccessToken(dataJson.get("access_token").getAsString());
            accountData.setAccessKey(dataJson.get("access_key").getAsString());

            if(dataJson.has("group_id") && !StringUtil.isEmpty(dataJson.get("group_id").getAsString())) {
                accountData.setGroupId(dataJson.get("group_id").getAsString());
            }

            if(dataJson.has("reg_code") && !StringUtil.isEmpty(dataJson.get("reg_code").getAsString())) {
                accountData.setRegCode(dataJson.get("reg_code").getAsString());
            }

            String serverAddress = MAContant.SERVER_URL;
            if (dataJson.has("serverAddress") && !StringUtil.isEmpty(dataJson.get("serverAddress").getAsString())) {
                serverAddress = dataJson.get("serverAddress").getAsString();
            }
            accountData.setServerAddress(serverAddress);


            String ownerEmail = "";
            if (dataJson.has("owner_email") && !StringUtil.isEmpty(dataJson.get("owner_email").getAsString())) {
                ownerEmail = dataJson.get("owner_email").getAsString();
            }
            accountData.setOwnerEmail(ownerEmail);

            ExternalStorageUtils.deleteFile(TOKEN_INFO_FILE_OLD);
            return accountData;
        } catch (Exception e) {
            return null;
        }
    }

    private AccountData loadInternalOldApp(Context context) {
        String dataInternal = InternalStorageUtils.loadTextFromFile(context, TOKEN_INFO_FILE_OLD);
        Log.i("LOG_INTERNAL_FILE", dataInternal == null ? "" : dataInternal);
        if (StringUtil.isEmpty(dataInternal)) {
            return null;
        }
        try {
            JsonParser parser = new JsonParser();
            JsonObject dataJson = parser.parse(dataInternal).getAsJsonObject();
            if (dataJson.has("owner_email")
                    && "marketing@opswat.com".equalsIgnoreCase(dataJson.get("owner_email").getAsString())) {
                return null;
            }

            AccountData accountData = new AccountData();
            accountData.setDeviceId(dataJson.get("device_id").getAsString());
            accountData.setLicenseKey(dataJson.get("license_key").getAsString());
            accountData.setMoKey(dataJson.get("mo_key").getAsString());
            accountData.setAccessToken(dataJson.get("access_token").getAsString());
            accountData.setAccessKey(dataJson.get("access_key").getAsString());

            if(dataJson.has("group_id") && !StringUtil.isEmpty(dataJson.get("group_id").getAsString())) {
                accountData.setGroupId(dataJson.get("group_id").getAsString());
            }

            if(dataJson.has("reg_code") && !StringUtil.isEmpty(dataJson.get("reg_code").getAsString())) {
                accountData.setRegCode(dataJson.get("reg_code").getAsString());
            }

            String serverAddress = MAContant.SERVER_URL;
            if (dataJson.has("serverAddress") && !StringUtil.isEmpty(dataJson.get("serverAddress").getAsString())) {
                serverAddress = dataJson.get("serverAddress").getAsString();
            }
            accountData.setServerAddress(serverAddress);

            String ownerEmail = "";
            if (dataJson.has("owner_email") && !StringUtil.isEmpty(dataJson.get("owner_email").getAsString())) {
                ownerEmail = dataJson.get("owner_email").getAsString();
            }
            accountData.setOwnerEmail(ownerEmail);

            InternalStorageUtils.deleteFile(context, TOKEN_INFO_FILE_OLD);
            return accountData;
        } catch (Exception e) {
            return null;
        }
    }


    private void updateDataInternal() {
        String jsonObj = gson.toJson(accountData);
        SharedPreferences.Editor prefsEditor = sharedPref.edit();
        prefsEditor.putString(MA_CLOUD_DATA_KEY, jsonObj);
        prefsEditor.apply();
    }

    private void updateDataExternal() {
        if (ExternalStorageUtils.isExternalStorageWritable()) {
            String jsonObj = gson.toJson(accountData);
            ExternalStorageUtils.saveTextToFile(jsonObj, MA_CLOUD_INFO_FILE);
        }
    }

    public AccountData getAccountData() {
        return accountData;
    }

    public void resetAccountInfo() {
        accountData.resetAccountData();
        setAccountData(accountData);
    }

    public synchronized void setAccountData(AccountData accountData) {
        this.accountData = accountData;
        this.accountData.setLastModified(System.currentTimeMillis());
        updateDataExternal();
        updateDataInternal();
    }

    public synchronized void updateUserIdentity(UserIdentity userIdentity) {
        this.accountData.setUserIdentity(userIdentity);
        updateDataExternal();
        updateDataInternal();
    }

    public long getLastConnected() {
        return lastConnected;
    }

    public void setLastConnected(long lastConnected) {
        this.lastConnected = lastConnected;
        SharedPreferences.Editor prefsEditor = sharedPref.edit();
        prefsEditor.putLong(MA_CLOUD_LAST_CONNECTED, lastConnected);
        prefsEditor.apply();
    }

    public boolean isOutOfToken() {
        return isOutOfToken;
    }

    public void setOutOfToken(boolean outOfToken) {
        isOutOfToken = outOfToken;
    }

    public long getLastScanningIP() {
        return lastScanningIP;
    }

    public void setLastScanningIP(long lastScanningIP) {
        this.lastScanningIP = lastScanningIP;
        SharedPreferences.Editor prefsEditor = sharedPref.edit();
        prefsEditor.putLong(MA_CLOUD_LAST_IP_SCANNING, lastScanningIP);
        prefsEditor.apply();
    }

    public FetchPrivacyResponse getPrivacy() {
        return privacy;
    }

    public void setPrivacy(FetchPrivacyResponse privacy) {
        this.privacy = privacy;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
