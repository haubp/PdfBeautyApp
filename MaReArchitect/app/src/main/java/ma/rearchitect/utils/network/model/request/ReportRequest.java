package opswat.com.network.model.request;

import com.google.gson.annotations.SerializedName;

import opswat.com.network.model.device.Soh;

/**
 * Created by H. Len Vo on 9/4/18.
 */
public class ReportRequest {
    @SerializedName("device_name")
    private String deviceName;

    @SerializedName("device_id")
    private String deviceId;

    @SerializedName("agent_version")
    private String agentVersion;

    @SerializedName("custom_id")
    private String customId;

    @SerializedName("user_identity")
    private String userIdentity;

    @SerializedName("trans_id")
    private String transId;

    @SerializedName("soh")
    private Soh soh;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAgentVersion() {
        return agentVersion;
    }

    public void setAgentVersion(String agentVersion) {
        this.agentVersion = agentVersion;
    }

    public String getCustomId() {
        return customId;
    }

    public void setCustomId(String customId) {
        this.customId = customId;
    }

    public String getUserIdentity() {
        return userIdentity;
    }

    public void setUserIdentity(String userIdentity) {
        this.userIdentity = userIdentity;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public Soh getSoh() {
        return soh;
    }

    public void setSoh(Soh soh) {
        this.soh = soh;
    }

    @Override
    public String toString() {
        return "ReportRequest{" +
                "deviceName='" + deviceName + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", agentVersion='" + agentVersion + '\'' +
                ", customId='" + customId + '\'' +
                ", userIdentity='" + userIdentity + '\'' +
                ", transId='" + transId + '\'' +
                ", soh=" + soh +
                '}';
    }
}
