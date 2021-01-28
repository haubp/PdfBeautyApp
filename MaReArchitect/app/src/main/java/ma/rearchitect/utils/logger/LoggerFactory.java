package opswat.com.logger;

import android.content.Context;

import com.google.gson.Gson;

import opswat.com.data.AccountData;
import opswat.com.data.MASettingData;
import opswat.com.device.DeviceBuilder;
import opswat.com.util.AppUtils;

/**
 * Created by H. Len Vo on 9/13/18.
 */
public class LoggerFactory {
    public static String generateLogger(Context context, DeviceBuilder deviceBuilder, AccountData accountData) {
        Logger logger = new Logger();
        Gson gson = new Gson();
        logger.setHealthDevice(deviceBuilder.getHealthDevice());
        logger.setSecurityDevice(deviceBuilder.getSecurityDevice());
        logger.setDeviceInfo(deviceBuilder.getDeviceInfo());
        logger.setAccountData(accountData);
        logger.setAppVersion(AppUtils.appVersion(context));

        MASettingData settingData = new MASettingData(context);
        if (settingData.isEnableDebugLog()) {
            logger.setLogUniversalLink(LoggerUniversalLink.getLogs(context));
        }

        return gson.toJson(logger, Logger.class);
    }
}
