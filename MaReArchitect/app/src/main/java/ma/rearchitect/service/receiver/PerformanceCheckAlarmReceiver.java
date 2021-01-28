package opswat.com.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import opswat.com.R;
import opswat.com.constant.MAContant;
import opswat.com.device.helper.HealthHelper;
import opswat.com.util.NotificationUtil;

/**
 * Created by H. Len Vo on 9/11/18.
 */
public class PerformanceCheckAlarmReceiver extends BroadcastReceiver {
    private Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        onHandleIntent();
    }
    private void onHandleIntent() {
        Thread backgroundThread = new Thread(new Runnable() {
            @Override
            public void run() {
                doPerformanceCheck();
            }
        });
        backgroundThread.start();
    }

    private void doPerformanceCheck() {
        HealthHelper helper = new HealthHelper(context);
        int cpuUsage = helper.readCPUUsage();
        int ramUsage = helper.readRamUsagePercent();
        int storageUsage = helper.readStorageUsagePercent();

        String notify = "Your ";
        boolean isCpuWarning = cpuUsage > MAContant.CPU_USAGE_WARNING;
        boolean isRamWarning = ramUsage > MAContant.RAM_USAGE_WARNING;
        boolean isStorageWarning = storageUsage > MAContant.STORAGE_USAGE_WARNING;

        boolean isPlural = false;
        if (!isCpuWarning && !isRamWarning && !isStorageWarning)
            return;

        if (isCpuWarning)
        {
            notify += "CPU performance";
        }
        if (isRamWarning)
        {
            if (isCpuWarning) {
                notify += ", ";
                isPlural = true;
            }
            notify += "RAM";
        }
        if (isStorageWarning)
        {
            if (isRamWarning) {
                notify += ", ";
                isPlural = true;
            }
            notify += "storage";
        }
        notify += isPlural? " are low" : " is low";

        NotificationUtil.pushNotification(notify, context, context.getResources().getString(R.string.channel_id));
    }
}
