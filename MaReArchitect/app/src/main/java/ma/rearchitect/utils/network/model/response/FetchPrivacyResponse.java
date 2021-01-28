package opswat.com.network.model.response;

import com.google.gson.annotations.SerializedName;

public class FetchPrivacyResponse extends MACloudResponse {
    @SerializedName("serial_number")
    private int serialNumber;

    @SerializedName("local_ip")
    private int localIp;

    @SerializedName("hostname")
    private int hostName;

    @SerializedName("public_ip")
    private int publicIp;

    @SerializedName("mac_addr")
    private int macAddr;

    @SerializedName("username")
    private int username;

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public int getLocalIp() {
        return localIp;
    }

    public void setLocalIp(int localIp) {
        this.localIp = localIp;
    }

    public int getHostName() {
        return hostName;
    }

    public void setHostName(int hostName) {
        this.hostName = hostName;
    }

    public int getPublicIp() {
        return publicIp;
    }

    public void setPublicIp(int publicIp) {
        this.publicIp = publicIp;
    }

    public int getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(int macAddr) {
        this.macAddr = macAddr;
    }

    public int getUsername() {
        return username;
    }

    public void setUsername(int username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "FetchPrivacyResponse{" +
                "serialNumber=" + serialNumber +
                ", localIp=" + localIp +
                ", hostName=" + hostName +
                ", publicIp=" + publicIp +
                ", macAddr=" + macAddr +
                ", username=" + username +
                '}';
    }
}
