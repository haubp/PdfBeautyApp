package ma.rearchitect.service.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ma.rearchitect.R;
import opswat.com.util.NotificationUtil;

/**
 * Created by H. Len Vo on 9/11/18.
 */
public class MaReminderAlarmReceiver extends BroadcastReceiver {
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
                doReminder();
            }
        });
        backgroundThread.start();
    }

    private void doReminder() {
        NotificationUtil.pushNotification(context.getResources().getString(R.string.notification_app_reminder), context,context.getResources().getString(R.string.channel_id));
    }
}
