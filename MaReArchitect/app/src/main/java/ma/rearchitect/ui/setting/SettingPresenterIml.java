package opswat.com.flow.setting;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import opswat.com.data.MASettingData;
import opswat.com.receiver.MaReminderAlarmReceiver;

/**
 * Created by H. Len Vo on 9/13/18.
 */
public class SettingPresenterIml implements ISettingPresenter {
    private ISettingView view;
    private Context context;

    @Override
    public void onAttach(ISettingView mvpView) {
        this.view = mvpView;
        this.context = mvpView.getContext();
    }

    @Override
    public void onDetach() {
    }

    public void bindingService() {
        boolean alarmRunning;
        Intent alarmAppReminder = new Intent(context, MaReminderAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmAppReminder, PendingIntent.FLAG_NO_CREATE);
        alarmRunning = (pendingIntent != null);
        if (alarmRunning) {
            pendingIntent.cancel();
        }

        MASettingData settingData = new MASettingData(context);
        if(settingData.isNotifyReminder()) {
            pendingIntent = PendingIntent.getBroadcast( context, 0, alarmAppReminder, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                long reminderTime = (long)settingData.reminderTime()* 24 * 3600 * 1000;
                long nextTime = SystemClock.elapsedRealtime() + reminderTime;
                alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, nextTime, reminderTime, pendingIntent);
            }
        }


    }
}
