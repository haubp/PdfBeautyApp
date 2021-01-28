package opswat.com.network.model.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import opswat.com.constant.MAContant;
import opswat.com.network.model.device.NetworkAdapter;
import opswat.com.network.model.device.OsInfo;

public class RegisterRequest {
    @SerializedName("device_type")
    private String deviceType = MAContant.DEVICE_TYPE;

    @SerializedName("device_name")
    private String deviceName;

    @SerializedName("device_id")
    private String deviceId;

    @SerializedName("agent_version")
    private String agentVersion;

    @SerializedName("group_id")
    private String groupId;

    @SerializedName("os")
    private OsInfo osInfo;

    @SerializedName("network_adapter_info")
    private List<NetworkAdapter> networkAdapters;

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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public OsInfo getOsInfo() {
        return osInfo;
    }

    public void setOsInfo(OsInfo osInfo) {
        this.osInfo = osInfo;
    }

    public List<NetworkAdapter> getNetworkAdapters() {
        return networkAdapters;
    }

    public void setNetworkAdapters(List<NetworkAdapter> networkAdapters) {
        this.networkAdapters = networkAdapters;
    }

    public String getAgentVersion() {
        return agentVersion;
    }

    public void setAgentVersion(String agentVersion) {
        this.agentVersion = agentVersion;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "deviceType='" + deviceType + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", agentVersion='" + agentVersion + '\'' +
                ", groupId='" + groupId + '\'' +
                ", osInfo=" + osInfo +
                ", networkAdapters=" + networkAdapters +
                '}';
    }
}
