package ma.rearchitect.data.device.helper;

import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;


import ma.rearchitect.R;
import ma.rearchitect.data.device.DeviceDefine;
import ma.rearchitect.data.device.model.AdTracking;
import ma.rearchitect.data.device.model.Encryption;
import ma.rearchitect.data.device.model.Jailbreak;
import ma.rearchitect.data.device.model.OsUpToDate;
import ma.rearchitect.data.device.model.PasswordAndLockScreen;
import opswat.com.util.SharedPrefsUtils;
import opswat.com.util.ShellUtil;

import static android.content.Context.KEYGUARD_SERVICE;

/**
 * Created by LenVo on 7/13/18.
 */

public class SecurityHelper {
    private Context context;

    public SecurityHelper(Context context) {
        this.context = context;
    }

    public Jailbreak getJaibreak() {
        Jailbreak jaibreak = new Jailbreak();
        boolean isRooted = !(checkRootMethod1() || checkRootMethod2() || checkRootMethod3() || checkRootMethod4("su"));
        jaibreak.setAuthentic(isRooted);
        return jaibreak;
    }

    public PasswordAndLockScreen getPasswordAndLockScreen() {
        PasswordAndLockScreen passwordAndLockScreen = new PasswordAndLockScreen();
        boolean isProtected;
        KeyguardManager mgr = ( KeyguardManager ) context.getSystemService( KEYGUARD_SERVICE );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isProtected = mgr.isDeviceSecure();
        }
        else {
            isProtected = mgr.isKeyguardSecure();
        }
        String lockType = "none";
        try {
            String LOCKSCREEN_UTILS = "com.android.internal.widget.LockPatternUtils";
            Class<?> lockUtilsClass = Class.forName(LOCKSCREEN_UTILS);
            Object lockUtils = lockUtilsClass.getConstructor(Context.class).newInstance(context);
            Method method = lockUtilsClass.getMethod("getActivePasswordQuality");

            int lockProtectionLevel = (Integer) method.invoke(lockUtils); // Thank you esme_louise for the cast hint

            if (lockProtectionLevel >= DevicePolicyManager.PASSWORD_QUALITY_SOMETHING) {
                isProtected = true;
                switch (lockProtectionLevel) {
                    case DevicePolicyManager.PASSWORD_QUALITY_SOMETHING:
                        lockType = "pattern";
                        break;
                    case DevicePolicyManager.PASSWORD_QUALITY_NUMERIC:
                        lockType = "pass_code";
                        break;
                    case DevicePolicyManager.PASSWORD_QUALITY_COMPLEX:
                        lockType = "pass_code";
                        break;
                    default:
                        lockType = "pass_code";
                        break;
                }
            } else
                lockType = "none_or_slider";
        } catch (Exception e) {
            e.printStackTrace();
        }

        passwordAndLockScreen.setProtected(isProtected);
        passwordAndLockScreen.setLockType(lockType);
        return passwordAndLockScreen;
    }

    public OsUpToDate getOsUpToDate() {
        OsUpToDate osUpToDate = new OsUpToDate();
        StringBuilder builder = new StringBuilder();
        String osName = builder.toString();
        try {
            Field[] fields = Build.VERSION_CODES.class.getFields();
            for (int i = fields.length-1; i>=0; i--) {
                Field field = fields[i];
                String fieldName = field.toString();
                int fieldValue = field.getInt(new Object());
                if (fieldValue == Build.VERSION.SDK_INT) {
                    builder.append(fieldName);
                    break;
                }
            }
            osName = builder.toString();
            int index = osName.indexOf("VERSION");
            osName = osName.substring(index + 14, osName.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
        osUpToDate.setOsName(osName);
        osUpToDate.setOsVersion(Build.VERSION.RELEASE);
        return osUpToDate;
    }

    public Encryption getEncryption() {
        Encryption encryption = new Encryption();
        int status = DevicePolicyManager.ENCRYPTION_STATUS_UNSUPPORTED;
        final DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (dpm != null) {
            status = dpm.getStorageEncryptionStatus();
        }

        String encryption_state, state_name;
        if (status == DevicePolicyManager.ENCRYPTION_STATUS_INACTIVE || status == DevicePolicyManager.ENCRYPTION_STATUS_UNSUPPORTED) {
            encryption_state = DeviceDefine.UNENCRYPTED;
            state_name = context.getResources().getString(R.string.unencrypted);
        } else if (status == DevicePolicyManager.ENCRYPTION_STATUS_ACTIVATING) {
            encryption_state = DeviceDefine.PARTIAL_ENCRYPTED;
            state_name = context.getResources().getString(R.string.partial_encrypted);
        } else if (status == DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE || status == DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE_PER_USER || status == DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE_DEFAULT_KEY ) {
            encryption_state = DeviceDefine.FULLY_ENCRYPTED;
            state_name = context.getResources().getString(R.string.fully_encrypted);
        }
        else {
            encryption_state = DeviceDefine.UNKNOWN;
            state_name = context.getResources().getString(R.string.unknown);
        }

        encryption.setStateName(state_name);
        encryption.setState(encryption_state);
        return encryption;
    }

    public AdTracking getAdTracking() {
        AdTracking adTracking = new AdTracking();
        TrackingDetect trackingDetect = new TrackingDetect(context);
        trackingDetect.execute();
        boolean adTrackingStorage = SharedPrefsUtils.getBooleanPreference(context,
                DeviceDefine.AD_TRACKING_KEY, false);

        adTracking.setEnable(adTrackingStorage);
        return adTracking;
    }

    class TrackingDetect extends AsyncTask<String, Integer, String> {
        private Context _context;
        boolean isEnable = true;
        TrackingDetect(Context context) {
            _context = context;
        }
        @Override
        protected String doInBackground(String... param) {
            try {
                AdvertisingIdClient.Info adsInfo = AdvertisingIdClient.getAdvertisingIdInfo(_context);
                isEnable = adsInfo.isLimitAdTrackingEnabled();
                SharedPrefsUtils.setBooleanPreference(context, DeviceDefine.AD_TRACKING_KEY, isEnable);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
            return "success";
        }
    }

    private boolean checkRootMethod1()
    {
        String buildTags = Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private boolean checkRootMethod2()
    {
        try {
            File file = new File("/system/app/Superuser.apk");
            if (file.exists()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean checkRootMethod3()
    {
        return ShellUtil.executeCommand(ShellUtil.SHELL_CMD.check_su_binary) != null;
    }

    private boolean checkRootMethod4(String binaryName) {
        boolean found = false;
        String[] places = {"/sbin/", "/system/bin/", "/system/xbin/", "/data/local/xbin/",
                "/data/local/bin/", "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/"};
        for (String where : places) {
            if ( new File( where + binaryName ).exists() ) {
                found = true;
                break;
            }
        }
        return found;
    }
}
