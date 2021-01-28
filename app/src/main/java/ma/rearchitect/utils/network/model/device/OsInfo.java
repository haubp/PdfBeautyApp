package opswat.com.network.model.device;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import opswat.com.constant.MAContant;

public class OsInfo {
    @SerializedName("name")
    private String name;

    @SerializedName("version")
    private String version;

    @SerializedName("os_language")
    private String osLanguage;

    @SerializedName("family")
    private String family;

    @SerializedName("vendor")
    private String vendor;

    @SerializedName("device_type")
    private String deviceType = MAContant.DEVICE_TYPE;

    @SerializedName("network_adapters")
    private List<NetworkAdapter> networkAdapters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOsLanguage() {
        return osLanguage;
    }

    public void setOsLanguage(String osLanguage) {
        this.osLanguage = osLanguage;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public List<NetworkAdapter> getNetworkAdapters() {
        return networkAdapters;
    }

    public void setNetworkAdapters(List<NetworkAdapter> networkAdapters) {
        this.networkAdapters = networkAdapters;
    }

    @Override
    public String toString() {
        return "OsInfo{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", osLanguage='" + osLanguage + '\'' +
                ", family='" + family + '\'' +
                ", vendor='" + vendor + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", networkAdapters=" + networkAdapters +
                '}';
    }
}
