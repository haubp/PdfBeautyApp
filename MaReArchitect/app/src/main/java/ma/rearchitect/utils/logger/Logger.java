package opswat.com.logger;

import com.google.gson.Gson;

import java.util.List;

import opswat.com.data.AccountData;
import opswat.com.device.HealthDevice;
import opswat.com.device.SecurityDevice;
import opswat.com.device.model.DeviceInfo;

/**
 * Created by H. Len Vo on 9/13/18.
 */
public class Logger {
    private String appVersion;
    private HealthDevice healthDevice;
    private SecurityDevice securityDevice;
    private DeviceInfo deviceInfo;
    private AccountData accountData;
    private List<String> logUniversalLink;

    public void setHealthDevice(HealthDevice healthDevice) {
        this.healthDevice = healthDevice;
    }

    public void setSecurityDevice(SecurityDevice securityDevice) {
        this.securityDevice = securityDevice;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public void setAccountData(AccountData accountData) {
        this.accountData = accountData;
    }

    public HealthDevice getHealthDevice() {
        return healthDevice;
    }

    public SecurityDevice getSecurityDevice() {
        return securityDevice;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public AccountData getAccountData() {
        return accountData;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public List<String> getLogUniversalLink() {
        return logUniversalLink;
    }

    public void setLogUniversalLink(List<String> logUniversalLink) {
        this.logUniversalLink = logUniversalLink;
    }
}
