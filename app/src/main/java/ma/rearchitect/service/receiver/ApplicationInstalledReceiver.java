package ma.rearchitect.service.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import ma.rearchitect.R;
import ma.rearchitect.data.data.MASettingData;
import opswat.com.enums.ConfigStatus;
import opswat.com.util.NotificationUtil;

/**
 * Created by H. Len Vo on 9/12/18.
 */
public class ApplicationInstalledReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!"android.intent.action.PACKAGE_ADDED".equals(intent.getAction()) || intent.getData() == null) {
            return;
        }
        Uri data = intent.getData();
        String pkgName = data.getEncodedSchemeSpecificPart();
        PackageManager pm = context.getPackageManager();
        if (pkgName.equals("com.opswat.gears") || pkgName.equals("com.google.android.gms")) {
            return;
        }
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(pkgName, 0);
            final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
            MASettingData settingData = new MASettingData(context);
            settingData.loadSettingData();

            if (settingData.getNotifyInstalledApp() == ConfigStatus.DEFAULT_SELECTED_CONFIG ||
                settingData.getNotifyInstalledApp() == ConfigStatus.USER_SELECTED_CONFIG) {
                String message = context.getString(R.string.notification_app_installed).replace("$appName", applicationName);
                NotificationUtil.pushNotification(message, context, context.getResources().getString(R.string.channel_id));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
