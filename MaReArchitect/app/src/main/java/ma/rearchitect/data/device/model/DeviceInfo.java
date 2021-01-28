package ma.rearchitect.data.device.model;

/**
 * Created by H. Len Vo on 8/23/18.
 */
public class DeviceInfo {
    private String deviceName;
    private String deviceType;
    private String osName;
    private String osVersion;
    private String osFamily;
    private String osVendor;
    private String osLanguage;
    private NetworkAdapter networkAdapter;
    private boolean wifiEnable;

    public boolean isWifiEnable() {
        return wifiEnable;
    }

    public void setWifiEnable(boolean wifiEnable) {
        this.wifiEnable = wifiEnable;
    }

    public String getOsLanguage() {
        return osLanguage;
    }

    public void setOsLanguage(String osLanguage) {
        this.osLanguage = osLanguage;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getOsFamily() {
        return osFamily;
    }

    public void setOsFamily(String osFamily) {
        this.osFamily = osFamily;
    }

    public String getOsVendor() {
        return osVendor;
    }

    public void setOsVendor(String osVendor) {
        this.osVendor = osVendor;
    }

    public NetworkAdapter getNetworkAdapter() {
        return networkAdapter;
    }

    public void setNetworkAdapter(NetworkAdapter networkAdapter) {
        this.networkAdapter = networkAdapter;
    }
}
