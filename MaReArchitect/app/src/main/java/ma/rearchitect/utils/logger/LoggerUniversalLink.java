package opswat.com.logger;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;

import opswat.com.constant.MAContant;
import opswat.com.data.MASettingData;
import opswat.com.util.DateUtil;
import opswat.com.util.SharedPrefsUtils;

public class LoggerUniversalLink {
    public static void writeLog(Context context, String... logs) {
        MASettingData settingData = new MASettingData(context);
        if (!settingData.isEnableDebugLog()) {
            return;
        }

        Queue<String> traceLogs = SharedPrefsUtils.getQueuePreference(context, MAContant.CONTENT_DEBUG_LOG_KEY);

        if (traceLogs.size() >= MAContant.MAX_DEBUG_LOG_LENGTH) {
            traceLogs.poll();
        }

        StringBuilder contentLog = new StringBuilder();
        if (logs != null && logs.length > 0) {
            for (String log : logs) {
                contentLog.append(log == null ? " null" : log);
            }
        }
        traceLogs.add(DateUtil.getFullFormatDateLog(System.currentTimeMillis()) + " - " + contentLog);
        SharedPrefsUtils.setQueuePreference(context, MAContant.CONTENT_DEBUG_LOG_KEY, traceLogs);
    }

    public static List<String> getLogs(Context context) {
        Queue<String> traceLogs =  SharedPrefsUtils.getQueuePreference(context, MAContant.CONTENT_DEBUG_LOG_KEY);
        return new ArrayList<>(traceLogs);
    }
}
